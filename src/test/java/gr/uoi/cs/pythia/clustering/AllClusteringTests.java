package gr.uoi.cs.pythia.clustering;

import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;



@RunWith(Suite.class)
@SuiteClasses({ 
	KmeansClusteringPerformerTests.class,
	GraphBasedClusteringPerformerTests.class,
	DivisiveClusteringPerformerTests.class
})
public class AllClusteringTests {
	
	@ClassRule
    public static ClusteringResource clusteringResource = new ClusteringResource();
}
