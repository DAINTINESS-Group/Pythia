package gr.uoi.cs.pythia.clustering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.spark.ml.clustering.PowerIterationClustering;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

import gr.uoi.cs.pythia.model.ClusteringProfile;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.clustering.ClusteringType;

public class GraphBasedClusteringPerformer extends GeneralClusteringPerformer{

	private final int numOfClusters;
	private List<String> selectedFeatures;
	
	public GraphBasedClusteringPerformer(int numOfClusters, DatasetProfile datasetProfile, List<String> selectedFeatures) {
		super(datasetProfile);
		this.numOfClusters = numOfClusters;
		this.selectedFeatures = selectedFeatures;
	}
	
	@Override
	public ClusteringProfile performClustering(Dataset<Row> dataset) {
		Dataset<Row> categoricalDataset = getCategoricalDataset(dataset, selectedFeatures);
		String[] featureColumns = getFeatureColumns(categoricalDataset);
		Dataset<Row> assembledData = getAssembledData(categoricalDataset, featureColumns);
		
		Dataset<Row> similaritiesTable = extractSimilarities(categoricalDataset);
	
	    // Perform Graph-Based clustering
		PowerIterationClustering  pic = new PowerIterationClustering ()
	            .setK(numOfClusters)
	            .setWeightCol("similarity");

		Dataset<Row> clusteredIds = pic.assignClusters(similaritiesTable);
	    List<Integer> clusterIds = sortClusters(clusteredIds);
	    
	    Dataset<Row> predictions = addColumn("cluster", assembledData, clusterIds);
	    
	    double meanSilhuette = calculateMeanSilhouetteScore(predictions, "cluster", "features");
	    
	    List<Cluster> clusters = retrieveClusters(predictions, numOfClusters, featureColumns);
	    
	    double error = calculateError(clusters);
	    
	    ClusteringProfile result = setupClusteringProfile(ClusteringType.GRAPH_BASED, error, predictions.drop("features"), clusters, meanSilhuette);
	    datasetProfile.setClusteringProfile(result);
	    createResultCsv(predictions.drop("features"));
	    return result;
	}
	
	private Dataset<Row> extractSimilarities(Dataset<Row> dataset) {
		List<Row> similarityRows = new ArrayList<>();
		StructType schema = new StructType()
	            .add("src", DataTypes.IntegerType)
	            .add("dst", DataTypes.IntegerType)
	            .add("similarity", DataTypes.DoubleType);
		
	    Row[] rows = (Row[]) dataset.collect();
	    int numOfRows = rows.length;

	    for (int i = 0; i < numOfRows - 1; i++) {
	        for (int j = i + 1; j < numOfRows; j++) {
	            Row row1 = rows[i];
	            Row row2 = rows[j];

	            // Convert row1 to double array
	            Double[] row1Vector = new Double[row1.length()];
	            for (int k = 0; k < row1.length(); k++) {
	                if (row1.get(k) instanceof Integer) {
	                	row1Vector[k] = ((Integer) row1.get(k)).doubleValue();
	                } else if (row1.get(k) instanceof Double) {
	                	row1Vector[k] = (Double) row1.get(k);
	                }
	            }

	            // Convert row2 to double array
	            Double[] row2Vector = new Double[row2.length()];
	            for (int k = 0; k < row2.length(); k++) {
	                if (row2.get(k) instanceof Integer) {
	                	row2Vector[k] = ((Integer) row2.get(k)).doubleValue();
	                } else if (row2.get(k) instanceof Double) {
	                	row2Vector[k] = (Double) row2.get(k);
	                }
	            }
	            
	            double cosSimilarity = cosineSimilarity(row1Vector, row2Vector);
	            
	            Row currentRow = RowFactory.create(i, j, cosSimilarity);
	            similarityRows.add(currentRow);
	        }
	    }
	    return dataset.sparkSession().createDataFrame(similarityRows, schema); 
	}
	
	private Dataset<Row> addColumn(String columnName, Dataset<Row> dataset, List<Integer> values) {
        // Convert the list of values to a DataFrame
        Dataset<Row> valuesDF = dataset.sqlContext().createDataFrame(
                values.stream().map(RowFactory::create).collect(Collectors.toList()),
                DataTypes.createStructType(Arrays.asList(DataTypes.createStructField(columnName, DataTypes.IntegerType, false)))
        );

        // Generate row numbers for joining
        Dataset<Row> datasetWithRowNum = dataset.withColumn("row_num", functions.monotonically_increasing_id());

        // Generate row numbers for values DataFrame
        Dataset<Row> valuesDFWithRowNum = valuesDF.withColumn("row_num", functions.monotonically_increasing_id());

        // Join the datasets on the row numbers and drop the row number column
        Dataset<Row> result = datasetWithRowNum
                .join(valuesDFWithRowNum, datasetWithRowNum.col("row_num").equalTo(valuesDFWithRowNum.col("row_num")), "inner")
                .drop("row_num");

        // Drop the intermediate DataFrame
        valuesDF.unpersist();

        return result;
    }

	private List<Integer> sortClusters(Dataset<Row> dataset) {
	    List<Integer> clusterIds = new ArrayList<>();

	    // Collect the rows into an array
	    Row[] rows = (Row[]) dataset.collect();

	    // Sort the rows based on the first column
	    Arrays.sort(rows, Comparator.comparing(row -> {
	        Object value = row.get(0);
	        if (value instanceof Integer) {
	            return ((Integer) value).longValue();
	        } else if (value instanceof Long) {
	            return (Long) value;
	        }
	        return 0L; // Default value if type is neither Integer nor Long
	    }));

	    // Extract cluster ids and add them to the list
	    for (Row row : rows) {
	        clusterIds.add((Integer) row.get(1));
	    }

	    return clusterIds;
	}
	
	private double cosineSimilarity(Double[] vectorA, Double[] vectorB) {
	    double dotProduct = 0.0;
	    double normA = 0.0;
	    double normB = 0.0;
	    for (int i = 0; i < vectorA.length; i++) {
	        dotProduct += vectorA[i] * vectorB[i];
	        normA += Math.pow(vectorA[i], 2);
	        normB += Math.pow(vectorB[i], 2);
	    }   
	    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	}
}
