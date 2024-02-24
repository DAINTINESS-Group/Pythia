package gr.uoi.cs.pythia.clustering;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.functions;

import org.apache.spark.ml.clustering.KMeansModel;
import org.apache.spark.ml.clustering.KMeansSummary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.spark.ml.clustering.KMeans;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.VectorAssembler;

import gr.uoi.cs.pythia.model.ClusteringProfile;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.clustering.ClusteringType;


public class KmeansClusteringPerformer extends GeneralClusteringPerformer{
	
	private final int numOfClusters;
	private List<String> selectedFeatures;
	
	public KmeansClusteringPerformer(int numOfClusters, DatasetProfile datasetProfile, List<String> selectedFeatures) {
		super(datasetProfile);
		this.numOfClusters = numOfClusters;
		this.selectedFeatures = selectedFeatures;
	}
	
	@Override
	public ClusteringProfile performClustering(Dataset<Row> dataset) {
		Dataset<Row> categoricalDataset = getCategoricalDataset(dataset, selectedFeatures);
		String[] featureColumns = getFeatureColumns(categoricalDataset);
		Dataset<Row> assembledData = getAssembledData(categoricalDataset, featureColumns);

	    // Perform K-means clustering
	    KMeans kmeans = new KMeans()
	            .setK(numOfClusters)
	            .setFeaturesCol("features")
	            .setPredictionCol("cluster");

	    KMeansModel model = kmeans.fit(assembledData);
	    Dataset<Row> predictions = model.transform(assembledData);
	    
	    double meanSilhuette = calculateMeanSilhouetteScore(predictions, "cluster", "features");
	    
	    List<Cluster> clusters = retrieveClusters(predictions, numOfClusters, featureColumns);

	    double error = calculateError(clusters);
	    
	    ClusteringProfile result = setupClusteringProfile(ClusteringType.KMEANS, error, predictions.drop("features"), clusters, meanSilhuette);
	    datasetProfile.setClusteringProfile(result);
	    createResultCsv(predictions.drop("features"));
	    return result;
	}

}
