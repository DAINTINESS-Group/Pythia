package gr.uoi.cs.pythia.clustering;

import java.util.List;

import org.apache.spark.ml.clustering.BisectingKMeans;
import org.apache.spark.ml.clustering.BisectingKMeansModel;
import org.apache.spark.ml.clustering.BisectingKMeansSummary;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import gr.uoi.cs.pythia.model.ClusteringProfile;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.clustering.ClusteringType;

public class DivisiveClusteringPerformer extends GeneralClusteringPerformer{
	
	private final int numOfClusters;
	private List<String> selectedFeatures;
	
	public DivisiveClusteringPerformer(int numOfClusters, DatasetProfile datasetProfile, List<String> selectedFeatures) {
		super(datasetProfile);
		this.numOfClusters = numOfClusters;
		this.selectedFeatures = selectedFeatures;
	}
	
	@Override
	public ClusteringProfile performClustering(Dataset<Row> dataset) {
		Dataset<Row> categoricalDataset = getCategoricalDataset(dataset, selectedFeatures);
		String[] featureColumns = getFeatureColumns(categoricalDataset);
		Dataset<Row> assembledData = getAssembledData(categoricalDataset, featureColumns);

	    // Perform Divisive clustering
	    BisectingKMeans  bkm = new BisectingKMeans ()
	            .setK(numOfClusters)
	            .setFeaturesCol("features")
	            .setPredictionCol("cluster");

	    BisectingKMeansModel model = bkm.fit(assembledData);
	    Dataset<Row> predictions = model.transform(assembledData);

	    double meanSilhuette = calculateMeanSilhouetteScore(predictions, "cluster", "features");

	    List<Cluster> clusters = retrieveClusters(predictions, numOfClusters, featureColumns);

	    double error = calculateError(clusters);
	    
	    ClusteringProfile result = setupClusteringProfile(ClusteringType.DIVISIVE, error, predictions.drop("features"), clusters, meanSilhuette);
	    datasetProfile.setClusteringProfile(result);
	    createResultCsv(predictions.drop("features"));
	    return result;
	}
}
