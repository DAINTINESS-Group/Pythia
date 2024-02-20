package gr.uoi.cs.pythia.clustering;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.Before;
import org.junit.Test;

import gr.uoi.cs.pythia.model.ClusteringProfile;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.clustering.ClusteringType;

public class DBSCANClusteringPerformerTests {

	private IClusteringPerformer clusteringPerformer;
	
	@Before
	public void init() {
		DatasetProfile datasetProfile = AllClusteringTests.clusteringResource.getDatasetProfile();
		clusteringPerformer = new ClusteringPerformerFactory().createClusteringPerformer(
				new ClusteringParameters(ClusteringType.DBSCAN, 4, Arrays.asList("manufacturer", "model", "fuelType")), datasetProfile);
	}
	
	@Test
	public void testPerformClustering() {
		Dataset<Row> dataset = AllClusteringTests.clusteringResource.getDataset();
		ClusteringProfile result = clusteringPerformer.performClustering(dataset);
		
		ClusteringType realType = result.getType();
		double realError = result.getError();
		Dataset<Row> realResult = result.getResult();
		List<Cluster> realClusters = result.getClusters();
		double realAvgSilhouetteScore = result.getAvgSilhouetteScore();
		
		//check if RegressionProfile is updated correctly'
		assertEquals(ClusteringType.DBSCAN, realType);
	}
}
