package gr.uoi.cs.pythia.model;

import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import gr.uoi.cs.pythia.clustering.Cluster;
import gr.uoi.cs.pythia.model.clustering.ClusteringType;

public class ClusteringProfile {
	
	private ClusteringType type;
	private double error;
	private Dataset<Row> result;
	private List<Cluster> clusters;
	private double avgSilhouetteScore;
	
	
	public ClusteringProfile(ClusteringType type, double error, Dataset<Row> result, List<Cluster> clusters,
			double avgSilhouetteScore) {
		super();
		this.type = type;
		this.error = error;
		this.result = result;
		this.clusters = clusters;
		this.avgSilhouetteScore = avgSilhouetteScore;
	}


	public ClusteringType getType() {
		return type;
	}


	public double getError() {
		return error;
	}


	public Dataset<Row> getResult() {
		return result;
	}


	public List<Cluster> getClusters() {
		return clusters;
	}


	public double getAvgSilhouetteScore() {
		return avgSilhouetteScore;
	}


	public void setType(ClusteringType type) {
		this.type = type;
	}


	public void setError(double error) {
		this.error = error;
	}


	public void setResult(Dataset<Row> result) {
		this.result = result;
	}


	public void setClusters(List<Cluster> clusters) {
		this.clusters = clusters;
	}


	public void setAvgSilhouetteScore(double avgSilhouetteScore) {
		this.avgSilhouetteScore = avgSilhouetteScore;
	}


	@Override
	public String toString() {
		return "ClusteringProfile [type=" + type + ", error=" + error + ", avgSilhouetteScore=" + avgSilhouetteScore
				+ "]";
	}
	

}
