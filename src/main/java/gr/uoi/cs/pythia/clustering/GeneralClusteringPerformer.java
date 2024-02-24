package gr.uoi.cs.pythia.clustering;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.spark.ml.evaluation.ClusteringEvaluator;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.functions;

import au.com.bytecode.opencsv.CSVWriter;
import gr.uoi.cs.pythia.model.ClusteringProfile;
import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.clustering.ClusteringType;

public abstract class GeneralClusteringPerformer implements IClusteringPerformer{
	
	protected DatasetProfile datasetProfile;
	
	public GeneralClusteringPerformer(DatasetProfile datasetProfile) {
		this.datasetProfile = datasetProfile;
	}
	
	@Override
	public abstract ClusteringProfile performClustering(Dataset<Row> dataset);
	
	protected Dataset<Row> getCategoricalDataset(Dataset<Row> dataset, List<String> selectedFeatures){
		// Apply StringIndexer to convert categorical strings to numerical indices
	    for (String column : selectedFeatures) {
	        StringIndexer indexer = new StringIndexer()
	                .setInputCol(column)
	                .setOutputCol(column + "_index");

	        dataset = indexer.fit(dataset).transform(dataset);
	    }
	    String[] featuresToDrop = getNonNumericalFeaturesNames().toArray(new String[0]);
	    return dataset.drop(featuresToDrop);
	}
	
	protected String[] getFeatureColumns(Dataset<Row> dataset) {
	    return dataset.columns();
	}
	
	protected Dataset<Row> getAssembledData(Dataset<Row> dataset,  String[] featureColumns){
	    VectorAssembler assembler = new VectorAssembler()
	            .setInputCols(featureColumns)
	            .setOutputCol("features");
	    return assembler.transform(dataset);
	}
	
	protected double calculateMeanSilhouetteScore(Dataset<Row> dataset, String predictionCol, String featuresCol) {
		ClusteringEvaluator evaluator = new ClusteringEvaluator()
				.setPredictionCol(predictionCol)
				.setFeaturesCol(featuresCol)
				.setMetricName("silhouette")
				.setDistanceMeasure("squaredEuclidean");
		
		return evaluator.evaluate(dataset);
	}
	
	protected List<Cluster> retrieveClusters(Dataset<Row> predictions, int numOfClusters, String[] featureColumns){
		List<Cluster> clusters = new ArrayList<>();
		
		for (int clusterId = 0; clusterId < numOfClusters; clusterId++) {
			// Filter the predictions dataset to get points belonging to the current cluster
			Dataset<Row> clusterData = predictions
					.selectExpr(featureColumns)
					.filter(functions.col("cluster").equalTo(clusterId));
			
			if(!clusterData.isEmpty()) {
				double[] meanPoint = calculateMeanPoint(clusterData);
				Row[] rows = (Row[]) clusterData.collect();
			    int numOfRows = rows.length;
			    
			    double errorSum = 0.0;
			    for(int i=0; i<numOfRows; i++) {
			    	Row row = rows[i];
			    	double[] rowVector = new double[row.length()];
			    	for (int k = 0; k < row.length(); k++) {
		                if (row.get(k) instanceof Integer) {
		                	rowVector[k] = ((Integer) row.get(k)).doubleValue();
		                } else if (row.get(k) instanceof Double) {
		                	rowVector[k] = (Double) row.get(k);
		                }
		            }
			    	double dist = calculateEuclideanDistance(rowVector, meanPoint);
			    	errorSum += dist*dist;
			    }
			    clusters.add(setupCluster(clusterData, clusterId, errorSum));
			}
	    }
	    return clusters;
	}
	
	protected double calculateError(List<Cluster> clusters) {
		double error = 0.0;
	    for(int i=0; i<clusters.size(); i++) {
	    	error += clusters.get(i).getError();
	    }
	    return error;
	}
	
	protected ClusteringProfile setupClusteringProfile(ClusteringType type,
			double error,
			Dataset<Row> result,
			List<Cluster> clusters,
			double avgSilhouetteScore) {
		return new ClusteringProfile(type,
				error,
				result,
				clusters,
				avgSilhouetteScore);
	}
	 
	protected double calculateEuclideanDistance(double[] vector1, double[] vector2) {
        if (vector1.length != vector2.length) {
            throw new IllegalArgumentException("Vectors must have the same dimensionality");
        }
        
        double sumOfSquares = 0.0;
        for (int i = 0; i < vector1.length; i++) {
            double diff = vector1[i] - vector2[i];
            sumOfSquares += diff * diff;
        }
        
        return Math.sqrt(sumOfSquares);
    }
	
	protected void createResultCsv(Dataset<Row> dataset) {
		String fileName = "clustering_results.csv";
		String csvFilePath = new File(String.format("%s%s%s",
    			datasetProfile.getAuxiliaryDataOutputDirectory(), File.separator, fileName)).getAbsolutePath();
		try {
            FileWriter fileWriter = new FileWriter(csvFilePath);

            //create header (encoded)
            CSVWriter csvWriter = new CSVWriter(fileWriter);
			Row[] rows = (Row[]) dataset.collect();
			String[] columns = dataset.columns();
			csvWriter.writeNext(columns);
			
		    int numOfRows = rows.length;
		    for(int i=0; i<numOfRows; i++) {
		    	Row row = rows[i];
		    	double[] rowVector = new double[row.length()];
		    	for (int k = 0; k < row.length(); k++) {
	                if (row.get(k) instanceof Integer) {
	                	rowVector[k] = ((Integer) row.get(k)).doubleValue();
	                } else if (row.get(k) instanceof Double) {
	                	rowVector[k] = (Double) row.get(k);
	                }
	            }	csvWriter.writeNext(doubleArrayToStringArray(rowVector));
		    }
		    csvWriter.close();
		}
		catch (IOException e) {
			System.err.println("Error writing to CSV file: " + e.getMessage());
		}
	}
	
	
	
	
	private List<String> getNonNumericalFeaturesNames(){
		List<String> names = new ArrayList<String>();
		for(Column column : datasetProfile.getColumns()) {
			if(column.getDatatype().equals("StringType")) {
				names.add(column.getName());
			}
		}
		return names;
	}

	private Cluster setupCluster(Dataset<Row> dataset, int clusterId, double errorSum){
	
		List<Integer> count = new ArrayList<Integer>();
		List<Double> allMean = new ArrayList<Double>();
		List<Double> allStandardDeviation = new ArrayList<Double>();
		List<Double> allMedian = new ArrayList<Double>();
		List<Double> allMin = new ArrayList<Double>();
		List<Double> allMax = new ArrayList<Double>();
		
		Row[] rows = (Row[]) dataset.collect();
	    int numOfRows = rows.length;
	    
	    //if cluster has 1 point we need to have different approach
	    if(numOfRows==1) {
	    	Row row = rows[0];
	    	double[] rowVector = new double[row.length()];
	    	for (int k = 0; k < row.length(); k++) {
                if (row.get(k) instanceof Integer) {
                	rowVector[k] = ((Integer) row.get(k)).doubleValue();
                } else if (row.get(k) instanceof Double) {
                	rowVector[k] = (Double) row.get(k);
                }
                count.add(1);
                allMean.add(rowVector[k]);
                allStandardDeviation.add(0.0);
                allMedian.add(rowVector[k]);
                allMin.add(rowVector[k]);
                allMax.add(rowVector[k]);
            }
	    }
	    else {
	    	Dataset<Row> descriptiveStatistics =
	                dataset.summary(
	                        "count",
	                        "mean",
	                        "stddev",
	                        "50%",
	                        "min",
	                        "max");
			Set<String> summaryColumns = new HashSet<>(Arrays.asList(descriptiveStatistics.columns()));
			for (String columnName : dataset.columns()) {
				if (summaryColumns.contains(columnName)) {
				    List<Row> columnNames = descriptiveStatistics.select(columnName).collectAsList();
				    List<Object> descriptiveStatisticsRow =
				            columnNames.stream().map(col -> col.get(0)).collect(Collectors.toList());

				    count.add(Integer.parseInt((String) descriptiveStatisticsRow.get(0)));
				    allMean.add(Double.parseDouble((String) descriptiveStatisticsRow.get(1)));
				    allStandardDeviation.add(Double.parseDouble((String) descriptiveStatisticsRow.get(2)));
				    allMedian.add(Double.parseDouble((String) descriptiveStatisticsRow.get(3)));
				    allMin.add(Double.parseDouble((String) descriptiveStatisticsRow.get(4)));
				    allMax.add(Double.parseDouble((String) descriptiveStatisticsRow.get(5)));
				}
			}
	    }
	    
		if(count.size()>0)
			return new Cluster(clusterId, count.get(0), allMean, allStandardDeviation, allMedian, allMin, allMax, errorSum);
		else
			return null;
	}

	private double[] calculateMeanPoint(Dataset<Row> dataset) {
	    // Calculate the mean of each numerical column
	    String[] columnNames = dataset.columns();
	    double[] meanValues = new double[columnNames.length];
	    
	    for (int i = 0; i < columnNames.length; i++) {
	        String columnName = columnNames[i];
	        meanValues[i] = dataset.agg(functions.mean(columnName)).head().getDouble(0);
	    }
	    
	    return meanValues;
	}
	
	private String[] doubleArrayToStringArray(double[] doubleArray) {
        String[] stringArray = new String[doubleArray.length];
        for (int i = 0; i < doubleArray.length; i++) {
            stringArray[i] = String.valueOf(doubleArray[i]);
        }
        return stringArray;
    }
	
	

}
