package gr.uoi.cs.pythia.clustering;

import java.util.List;

import gr.uoi.cs.pythia.model.clustering.ClusteringType;

public class ClusteringParameters {
	
	private ClusteringType type;
	private int numOfClusters;
	private List<String> selectedFeatures;
	

	public ClusteringParameters(ClusteringType type, int numOfClusters, List<String> selectedFeatures) {
		super();
		this.type = type;
		this.numOfClusters = numOfClusters;
		this.selectedFeatures = selectedFeatures;
	}
	
		
	public List<String> getSelectedFeatures() {
		return selectedFeatures;
	}
	
	public void setSelectedFeatures(List<String> selectedFeatures) {
		this.selectedFeatures = selectedFeatures;
	}

	public ClusteringType getType() {
		return type;
	}

	public int getNumOfClusters() {
		return numOfClusters;
	}

	public void setType(ClusteringType type) {
		this.type = type;
	}

	public void setNumOfClusters(int numOfClusters) {
		this.numOfClusters = numOfClusters;
	}
	
}
