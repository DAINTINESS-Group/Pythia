package gr.uoi.cs.pythia.clustering;

import gr.uoi.cs.pythia.model.DatasetProfile;

public class ClusteringPerformerFactory {
	
	public IClusteringPerformer createClusteringPerformer(ClusteringParameters clusteringParameters, DatasetProfile datasetProfile) {
		switch(clusteringParameters.getType()) {
			case KMEANS:
				return new KmeansClusteringPerformer(clusteringParameters.getNumOfClusters(), datasetProfile, clusteringParameters.getSelectedFeatures());
			case DIVISIVE:
				return new DivisiveClusteringPerformer(clusteringParameters.getNumOfClusters(), datasetProfile, clusteringParameters.getSelectedFeatures());
			case GRAPH_BASED:
				return new GraphBasedClusteringPerformer(clusteringParameters.getNumOfClusters(), datasetProfile, clusteringParameters.getSelectedFeatures());
		}		
		throw new IllegalArgumentException(
			String.format("%s clustering is not a supported clustering type.", clusteringParameters.getType()));
	}
}
