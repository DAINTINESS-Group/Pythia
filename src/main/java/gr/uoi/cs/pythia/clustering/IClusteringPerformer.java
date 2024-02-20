package gr.uoi.cs.pythia.clustering;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import gr.uoi.cs.pythia.model.ClusteringProfile;

public interface IClusteringPerformer {

	ClusteringProfile performClustering(Dataset<Row> dataset);
	
}
