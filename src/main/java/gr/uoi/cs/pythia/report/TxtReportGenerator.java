package gr.uoi.cs.pythia.report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import gr.uoi.cs.pythia.clustering.Cluster;
import gr.uoi.cs.pythia.model.ClusteringProfile;
import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.PatternsProfile;
import gr.uoi.cs.pythia.model.RegressionProfile;
import gr.uoi.cs.pythia.model.clustering.ClusteringType;
import gr.uoi.cs.pythia.model.dominance.DominanceResult;
import gr.uoi.cs.pythia.model.outlier.OutlierResult;
import gr.uoi.cs.pythia.model.regression.RegressionType;

public class TxtReportGenerator implements IReportGenerator {

	private static final String horizontalLine =  "\n\n===================================" +
			"==========================================\n\n";
	private static final String statisticalReportFileName = "statistical_report.txt";
	private static final String highDominanceReportFileName = "high_dominance_report.txt";
	private static final String lowDominanceReportFileName = "low_dominance_report.txt";
	private static final String outliersReportFileName = "outliers_report.txt";
	private static final String regressionReportFileName = "regression_report.txt";
	private static final String clusteringReportFileName = "clustering_report.txt";

	private static final boolean isExtensiveReport = true;
	
	public void produceReport(DatasetProfile datasetProfile, String outputDirectoryPath)
			throws IOException {
		produceStatisticalProfileReport(datasetProfile, outputDirectoryPath);
		producePatternsProfileReports(datasetProfile, outputDirectoryPath);
		produceRegressionProfileReport(datasetProfile, outputDirectoryPath);
		produceClusteringProfileReport(datasetProfile, outputDirectoryPath);
	}
	
	private void produceClusteringProfileReport(DatasetProfile datasetProfile, String outputDirectoryPath) throws IOException {
		String content = "";
		if(datasetProfile.getClusteringProfile()!=null) {
			//first section (with title)
			content += getClusteringTitle(datasetProfile.getClusteringProfile()) + "\n\n";
			content += getClusteringMethodOverview(datasetProfile.getClusteringProfile()) + "\n\n";
			
			//second section (general information)
			content += "Clustering Information\n";		
			content += "-Number of Clusters: " + datasetProfile.getClusteringProfile().getClusters().size() + "\n";
			content += "-Error: " + datasetProfile.getClusteringProfile().getError() + "\n";
			content += "-Average Silhouette Score: " + datasetProfile.getClusteringProfile().getAvgSilhouetteScore() + "\n\n\n";
			
			//third section (table per cluster)
			for(Cluster cluster : datasetProfile.getClusteringProfile().getClusters()) {
				content += getClusterTable(cluster, datasetProfile) + "\n\n";
			}
			writeToFile(outputDirectoryPath, clusteringReportFileName, content);
		}
		return;
	}

	private void produceRegressionProfileReport(DatasetProfile datasetProfile, String outputDirectoryPath) throws IOException{
		String content = "";
		if(datasetProfile.getRegressionProfiles().size()>0) {
			for(int i=0; i<datasetProfile.getRegressionProfiles().size(); i++) {
				RegressionProfile currentProfile = datasetProfile.getRegressionProfiles().get(i);
				int currentRegressionId = i+1;
				content += currentRegressionId + ". " + this.getTitle(currentProfile) + "\n\n";
				content += "Dependent Variable: " + currentProfile.getDependentVariable().getName() + "\n";
				if (currentProfile.getIndependentVariables().size() > 0) {
				    content += "Independent Variables: " + currentProfile.getIndependentVariables().get(0).getName();

				    for (int j = 1; j < currentProfile.getIndependentVariables().size(); j++) {
				        content += ", " + currentProfile.getIndependentVariables().get(j).getName();
				    }
				}
				content += "\n\n\n-> Results\n\n" + "-Information about Independent Variables\n";
				content += this.getTable(currentProfile);
				content += "\n\n\n-General Information\n";
				content += "Intercept: " + currentProfile.getIntercept() + "\n";
				content += "Error (MSE): " + currentProfile.getError() + "\n";	
				content += "Regression Type: " + this.getTitle(currentProfile)  + "\n";	
				content += "Formula: " + this.getFormula(currentProfile);
				content += "\n\n\n\n\n";
			}writeToFile(outputDirectoryPath, regressionReportFileName, content);
		}
		return;
	}
	
	private void produceStatisticalProfileReport(DatasetProfile datasetProfile,
			String outputDirectoryPath) throws IOException {
		writeToFile(outputDirectoryPath, statisticalReportFileName, datasetProfile.toString());
	}
	
	private void producePatternsProfileReports(DatasetProfile datasetProfile,
			String outputDirectoryPath) throws IOException {
		PatternsProfile patternsProfile = datasetProfile.getPatternsProfile();
		
		List<DominanceResult> highDominanceResults = patternsProfile.getHighDominanceResults();
		produceHighDominanceReport(highDominanceResults, outputDirectoryPath);
		
		List<DominanceResult> lowDominanceResults = patternsProfile.getLowDominanceResults();
		produceLowDominanceReport(lowDominanceResults, outputDirectoryPath);
		
		produceOutliersReport(datasetProfile, outputDirectoryPath);
	}
	
	private void produceHighDominanceReport(List<DominanceResult> highDominanceResults,
			String outputDirectoryPath) throws IOException {
		StringBuilder str = new StringBuilder("High Dominance Pattern Extensive Report\n");
		for (DominanceResult result : highDominanceResults) {
			str.append(buildDominanceResultString(result));
		}
		writeToFile(outputDirectoryPath, highDominanceReportFileName, String.valueOf(str));
	}

	private void produceLowDominanceReport(List<DominanceResult> lowDominanceResults,
		 	String outputDirectoryPath) throws IOException {
		StringBuilder str = new StringBuilder("Low Dominance Pattern Extensive Report\n");
		for (DominanceResult result : lowDominanceResults) {
			str.append(buildDominanceResultString(result));
		}
		writeToFile(outputDirectoryPath, lowDominanceReportFileName, String.valueOf(str));
	}
	
	private String buildDominanceResultString(DominanceResult dominanceResult) {
		String queryResultToString = "";
		
		if (dominanceResult.hasTwoCoordinates()) {
			queryResultToString = "\nQuery Results:\n" + dominanceResult.queryResultToString();
		}
		
		return horizontalLine + 
				"\n" + dominanceResult.titleToString() +
				"\nMetadata:\n" +
				dominanceResult.metadataToString() +
				"\nDetailed Results:\n" +
				dominanceResult.identificationResultsToString(isExtensiveReport) +
				"\nIdentified Dominance Features:\n" +
				dominanceResult.dominanceToString(isExtensiveReport) +
				queryResultToString;
	}
	
	private void produceOutliersReport(DatasetProfile datasetProfile,
			String outputDirectoryPath) throws IOException {
		PatternsProfile patternsProfile = datasetProfile.getPatternsProfile();
		List<OutlierResult> outlierResults = datasetProfile.getPatternsProfile().getOutlierResults();

		StringBuilder str = new StringBuilder(String.format(
				patternsProfile.getOutlierType() + " Outlier Pattern Results\n\n"
						+ "Total outliers found: %s\n",
				outlierResults.size()));

		for (Column column : datasetProfile.getColumns()) {
			int outliersInColumn = patternsProfile.countOutliersInColumn(column.getName());
			str.append(String.format(horizontalLine + "- Outliers in %s column\n"
					+ "Outliers found: %s\n", column.getName(), outliersInColumn));
			if (outliersInColumn > 0) {
				str.append(String.format("%-24s%-24s%-24s\n",
						"Outlier value", patternsProfile.getOutlierType(),
						"Position in the column"));
			}
			for (OutlierResult result : outlierResults) {
				if (!Objects.equals(result.getColumnName(), column.getName())) continue;
				str.append(result.toString());
			}
		}
		writeToFile(outputDirectoryPath, outliersReportFileName, String.valueOf(str));
	}

	private void writeToFile(String outputDirectoryPath, String fileName, String contents)
			throws IOException {
		String absoluteFileName = new File(String.format("%s%s%s",
    			outputDirectoryPath, File.separator, fileName)).getAbsolutePath();
		try (FileWriter fileWriter = new FileWriter(absoluteFileName)) {
			fileWriter.write(contents);
		}
	}
	
	private String getTitle(RegressionProfile profile) {
		if(profile.getType() == RegressionType.AUTOMATED)	return "Automated Regression";
		else if(profile.getType() == RegressionType.LINEAR)	return "Linear Regression";
		else if(profile.getType() == RegressionType.MULTIPLE_LINEAR)	return "Multiple Linear Regression";
		else if(profile.getType() == RegressionType.POLYNOMIAL)	return "Polynomial Regression";
		else	return null;
	}
	
	private String getFormula(RegressionProfile profile) {
		String independentPart = "";
		if(profile.getType() == RegressionType.LINEAR ||
				profile.getType() == RegressionType.MULTIPLE_LINEAR || profile.getType() == RegressionType.AUTOMATED) {
			for(int i=0; i<profile.getIndependentVariables().size();i++) {
				independentPart += " + " + profile.getSlopes().get(i) + "*" + profile.getIndependentVariables().get(i).getName();
			}
		}
		else if(profile.getType() == RegressionType.POLYNOMIAL) {
			for(int i=0; i<profile.getSlopes().size();i++) {
				independentPart += " + " + profile.getSlopes().get(i) +
						"*" + profile.getIndependentVariables().get(0).getName() + "^(" + (i+1) + ")";
			}
		}
		return profile.getDependentVariable().getName() + " = " + profile.getIntercept() + independentPart;
	}
	
	private String getTable(RegressionProfile profile) {
		String content = "";
		if(profile.getType()!= RegressionType.POLYNOMIAL) {
			content += "Column	|	Slope	|	Correlation	|	p-value (Null hypothesis for p-value of each column X -> Dependent variable and X are not correlated)\n";
			content += "-------------------------------------------------\n";
			for(int i=0; i<profile.getIndependentVariables().size(); i++) {
				content += profile.getIndependentVariables().get(i).getName() + "	|	";
				content += profile.getSlopes().get(i) + "	|	";
				content += profile.getCorrelations().get(i) + "	|	";
				content += profile.getpValues().get(i) + "\n";
			}
		}
		else {
			content += "Column	|	Correlation	|	p-value (Null hypothesis for p-value of each column X -> Dependent variable and X are not correlated)\n";
			content += "-------------------------------------------------\n";
			for(int i=0; i<profile.getIndependentVariables().size(); i++) {
				content += profile.getIndependentVariables().get(i).getName() + "	|	";
				content += profile.getCorrelations().get(i) + "	|	";
				content += profile.getpValues().get(i) + "\n";
			}
		}
		return content;
	}
	
	private String getClusteringTitle(ClusteringProfile clusteringProfile) {
		if(clusteringProfile.getType() == ClusteringType.KMEANS)	return "K-Means Clustering";
		if(clusteringProfile.getType() == ClusteringType.DIVISIVE)	return "Divisive Clustering";
		if(clusteringProfile.getType() == ClusteringType.GRAPH_BASED)	return "Graph Based Clustering";
		else	return "DBSCAN Clustering";
	}
	
	private String getClusteringMethodOverview(ClusteringProfile clusteringProfile) {
		if(clusteringProfile.getType() == ClusteringType.KMEANS) {
			return "K-means clustering is a popular unsupervised machine learning algorithm used" +"\n"
					+ " for partitioning a dataset into a predetermined number of clusters. The algorithm" +"\n"
					+ " aims to minimize the variance within clusters by iteratively assigning data points to" +"\n"
					+ " the nearest centroid and updating the centroids based on the mean of the data points assigned" +"\n"
					+ " to each cluster. K-means is widely used for data exploration, pattern recognition, and" +"\n"
					+ " segmentation tasks, offering simplicity, scalability, and efficiency in handling large datasets.";
		}
		if(clusteringProfile.getType() == ClusteringType.DIVISIVE) {
			return "Divisive clustering is a hierarchical clustering technique" +"\n"
					+ "that starts with all data points in a single cluster and iteratively" +"\n"
					+ "divides the dataset into smaller clusters until each data point is in its own cluster" +"\n"
					+ "or until a stopping criterion is met. At each step, divisive clustering recursively" +"\n"
					+ "splits clusters based on a chosen criterion, such as maximizing inter-cluster" +"\n"
					+ "dissimilarity or minimizing intra-cluster variance. Divisive clustering produces" +"\n"
					+ "a hierarchical tree structure known as a dendrogram, which can be used to explore" +"\n"
					+ "different levels of granularity in the clustering solution. This approach is valuable" +"\n"
					+ "for uncovering nested clusters and understanding the hierarchical organization of the data.";
		}
		if(clusteringProfile.getType() == ClusteringType.GRAPH_BASED) {
			return "Graph-based clustering leverages the concept of similarity between data points" +"\n"
					+ "to construct a graph, where nodes represent data points and edges represent" +"\n"
					+ "pairwise similarity or affinity between nodes. Commonly used similarity measures" +"\n"
					+ "include Euclidean distance, cosine similarity, or correlation coefficients." +"\n"
					+ "Once the graph is constructed, graph clustering algorithms aim to partition the graph" +"\n"
					+ "into cohesive clusters, where nodes within each cluster are densely connected while nodes" +"\n"
					+ "between clusters have sparse connections. Graph-based clustering methods, such as spectral" +"\n"
					+ "clustering and modularity optimization, offer flexibility in handling complex data structures" +"\n"
					+ "and can effectively capture non-linear relationships and community structures in the data.";
		}
		else	return "DBSCAN, short for Density-Based Spatial Clustering of Applications with Noise," +"\n"
				+ "is a density-based clustering algorithm designed to discover clusters of arbitrary shape in spatial data." +"\n"
				+ "Unlike centroid-based methods like k-means, DBSCAN does not require specifying the number of clusters" +"\n"
				+ "in advance. Instead, it groups together closely packed points based on two parameters: epsilon (ε)," +"\n"
				+ "which defines the radius of neighborhood around each point, and minPoints, which specifies the" +"\n"
				+ "minimum number of points within the ε-neighborhood to form a dense region. DBSCAN identifies core points," +"\n"
				+ "border points, and noise points, allowing it to handle outliers and discover clusters of varying shapes" +"\n"
				+ "and sizes. This algorithm is particularly effective for datasets with non-uniform density and complex" +"\n"
				+ "geometric structures.";
	}
	
	private String getClusterTable(Cluster cluster, DatasetProfile datasetProfile) {
		String content = "Cluster " + (cluster.getId()+1) + " (with "+ cluster.getNumOfPoints() +" points)\n";
		content += "Column	|	Mean	|	Standard Deviation	|	Median	|	Min	|	Max	\n";
		String[] columnNames = datasetProfile.getClusteringProfile().getResult().columns();	
		for(int i=0; i<columnNames.length; i++) {
			if(columnNames[i].equals("cluster"))	continue;
			content += columnNames[i];
			content += "	|	" + cluster.getMean().get(i);
			content += "	|	" + cluster.getStandardDeviations().get(i);
			content += "	|	" + cluster.getMedian().get(i);
			content += "	|	" + cluster.getMin().get(i);
			content += "	|	" + cluster.getMax().get(i);
			content += "\n";
		}
		return content;
	}
}
