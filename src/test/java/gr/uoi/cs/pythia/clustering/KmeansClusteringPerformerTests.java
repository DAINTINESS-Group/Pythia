package gr.uoi.cs.pythia.clustering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.Before;
import org.junit.Test;

import gr.uoi.cs.pythia.model.ClusteringProfile;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.clustering.ClusteringType;

public class KmeansClusteringPerformerTests {

	private IClusteringPerformer clusteringPerformer;
	
	@Before
	public void init() {
		DatasetProfile datasetProfile = AllClusteringTests.clusteringResource.getDatasetProfile();
		clusteringPerformer = new ClusteringPerformerFactory().createClusteringPerformer(
				new ClusteringParameters(ClusteringType.KMEANS, 2, Arrays.asList("manufacturer")), datasetProfile);
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
		
		ClusteringType expectedType = ClusteringType.KMEANS;
		double expectedError = 1.49783630232375E9;
		List<Integer> expectedResult = setupResult();
		List<Cluster> expectedClusters = setupClusters();
		double expectedAvgSilhouetteScore = 0.8370856361838825;
		
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
		clusterResults.add(0);clusterResults.add(0);clusterResults.add(0);clusterResults.add(0);clusterResults.add(0);
		clusterResults.add(0);clusterResults.add(1);clusterResults.add(1);clusterResults.add(0);clusterResults.add(0);
		return clusterResults;
	}
	
	private List<Cluster> setupClusters() {
		//setup 1st cluster
		int id1 = 0;
		int numOfPoints1 = 8;
		List<Double> allMean1 = setupMetric(9881.25, 26332.125, 71.25,
				58.2875, 1.5, 0.0);
		List<Double> allStamdardDeviation1 = setupMetric(5851.125502230929, 13397.940923221853, 62.607051177506015,
				5.610306205037806, 0.33806170189140666, 0.0);
		List<Double> allMedian1 = setupMetric(11000.0, 25952.0, 30.0,
				55.4, 1.4, 0.0);
		List<Double> allMin1 = setupMetric(500.0, 1998.0, 20.0,
				49.6, 1.0, 0.0);
		List<Double> allMax1 = setupMetric(17300.0, 46112.0, 150.0,
				67.3, 2.0, 0.0);
		double error1 = 1.49621109300375E9;
		Cluster cluster1 =  new Cluster(id1, numOfPoints1, allMean1, allStamdardDeviation1, allMedian1, allMin1, allMax1, error1);
		
		//setup 2nd cluster
		int id2 = 1;
		int numOfPoints2 = 2;
		List<Double> allMean2 = setupMetric(912.5, 75986.5, 25.0,
				66.0, 2.0, 0.0);
		List<Double> allStamdardDeviation2 = setupMetric(583.3630944789018, 1133.4921702420356, 7.0710678118654755,
				6.505382386916234, 0.0, 0.0);
		List<Double> allMedian2 = setupMetric(500.0, 75185.0, 20.0,
				61.4, 2.0, 0.0);
		List<Double> allMin2 = setupMetric(500.0, 75185.0, 20.0,
				61.4, 2.0, 0.0);
		List<Double> allMax2 = setupMetric(1325.0, 76788.0, 30.0,
				70.6, 2.0, 0.0);
		double error2 = 1625209.3199999998;
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
