package gr.uoi.cs.pythia.clustering;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.Before;
import org.junit.Test;

import gr.uoi.cs.pythia.model.ClusteringProfile;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.clustering.ClusteringType;

public class GraphBasedClusteringPerformerTests {
	
	private IClusteringPerformer clusteringPerformer;

	@Before
	public void init() {
		DatasetProfile datasetProfile = AllClusteringTests.clusteringResource.getDatasetProfile();
		clusteringPerformer = new ClusteringPerformerFactory().createClusteringPerformer(
				new ClusteringParameters(ClusteringType.GRAPH_BASED, 2, Arrays.asList("manufacturer")), datasetProfile);
	}
	
	@Test
	public void testPerformClustering() {
		Dataset<Row> dataset = AllClusteringTests.clusteringResource.getDataset();
		ClusteringProfile result = clusteringPerformer.performClustering(dataset);
		
		
		ClusteringType realType = result.getType();
		double realError = result.getError();
		List<Integer> realResult = result.getResult().select("cluster").as(org.apache.spark.sql.Encoders.INT()).collectAsList();
		List<Cluster> realClusters = result.getClusters();
		double realAvgSilhouetteScore = result.getAvgSilhouetteScore();
		
		ClusteringType expectedType = ClusteringType.GRAPH_BASED;
		double expectedError = 4.1725798743399997E9;
		List<Integer> expectedResult = setupResult();
		List<Cluster> expectedClusters = setupClusters();
		double expectedAvgSilhouetteScore = 0.18591317226254186;
		
		//check if RegressionProfile is updated correctly'
		assertEquals(realType, expectedType);
		assertEquals(realError, expectedError, 0.0001);
		assertEquals(realResult, expectedResult);
		
		//check clusters
		for(int i=0; i<expectedClusters.size(); i++) {
			assertEquals(realClusters.get(i).getId(), expectedClusters.get(i).getId());
			assertEquals(realClusters.get(i).getError(), expectedClusters.get(i).getError(), 0.0001);
			assertEquals(realClusters.get(i).getNumOfPoints(), expectedClusters.get(i).getNumOfPoints());
			for(int j=0; j<realClusters.get(0).getMax().size(); j++) {
				assertEquals(realClusters.get(i).getMax(), expectedClusters.get(i).getMax());
				assertEquals(realClusters.get(i).getMin(), expectedClusters.get(i).getMin());
				assertEquals(realClusters.get(i).getMean(), expectedClusters.get(i).getMean());
				assertEquals(realClusters.get(i).getStandardDeviations(), expectedClusters.get(i).getStandardDeviations());
				assertEquals(realClusters.get(i).getMedian(), expectedClusters.get(i).getMedian());
			}
		}
		
		
		assertEquals(realAvgSilhouetteScore, expectedAvgSilhouetteScore, 0.0001);
	}
	
	private List<Integer> setupResult(){
		List<Integer> clusterResults = new ArrayList<Integer>();
		clusterResults.add(0);clusterResults.add(0);clusterResults.add(0);clusterResults.add(0);clusterResults.add(1);
		clusterResults.add(0);clusterResults.add(0);clusterResults.add(0);clusterResults.add(0);clusterResults.add(0);
		return clusterResults;
	}
	
	private List<Cluster> setupClusters() {
		//setup 1st cluster
		int id1 = 0;
		int numOfPoints1 = 9;
		List<Double> allMean1 = setupMetric(7063.888888888889, 40070.22222222222, 52.77777777777778,
				60.96666666666666, 1.6666666666666667, 0.0);
		List<Double> allStamdardDeviation1 = setupMetric(5856.528289960795, 22074.207198548367, 53.91608706540604,
				5.494770240874498, 0.316227766016838, 0.0);
		List<Double> allMedian1 = setupMetric(10200.0, 32260.0, 30.0,
				60.1, 1.4, 0.0);
		List<Double> allMin1 = setupMetric(500.0, 15735.0, 20.0,
				55.4, 1.4, 0.0);
		List<Double> allMax1 = setupMetric(13900.0, 76788.0, 150.0,
				70.6, 2.0, 0.0);
		double error1 = 4.1725798743399997E9;
		Cluster cluster1 =  new Cluster(id1, numOfPoints1, allMean1, allStamdardDeviation1, allMedian1, allMin1, allMax1, error1);
		
		//setup 2nd cluster
		int id2 = 1;
		int numOfPoints2 = 1;
		List<Double> allMean2 = setupMetric(17300.0, 1998.0, 145.0,
				49.6, 1.0, 0.0);
		List<Double> allStamdardDeviation2 = setupMetric(0.0, 0.0, 0.0,
				0.0, 0.0, 0.0);
		List<Double> allMedian2 = setupMetric(17300.0, 1998.0, 145.0,
				49.6, 1.0, 0.0);
		List<Double> allMin2 = setupMetric(17300.0, 1998.0, 145.0,
				49.6, 1.0, 0.0);
		List<Double> allMax2 = setupMetric(17300.0, 1998.0, 145.0,
				49.6, 1.0, 0.0);
		double error2 = 0.0;
		Cluster cluster2 =  new Cluster(id2, numOfPoints2, allMean2, allStamdardDeviation2, allMedian2, allMin2, allMax2, error2);
		
		List<Cluster> clusters = new ArrayList<Cluster>();
		clusters.add(cluster1);clusters.add(cluster2);
		return clusters;
	}
	
	private List<Double> setupMetric(double m1, double m2, double m3, double m4, double m5, double m6) {
		List<Double> allMetric = new ArrayList<Double>();
		allMetric.add(m1);allMetric.add(m2);
		allMetric.add(m3);allMetric.add(m4);
		allMetric.add(m5);allMetric.add(m6);
		return allMetric;
	}

}
