package gr.uoi.cs.pythia.client;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import gr.uoi.cs.pythia.engine.DatasetProfilerParameters;
import gr.uoi.cs.pythia.engine.IDatasetProfiler;
import gr.uoi.cs.pythia.engine.IDatasetProfilerFactory;
import gr.uoi.cs.pythia.model.clustering.ClusteringType;
import gr.uoi.cs.pythia.model.regression.RegressionType;
import gr.uoi.cs.pythia.regression.RegressionParameters;
import gr.uoi.cs.pythia.regression.RegressionRequest;
import gr.uoi.cs.pythia.report.ReportGeneratorConstants;
import gr.uoi.cs.pythia.util.HighlightParameters;
import gr.uoi.cs.pythia.util.HighlightParameters.HighlightExtractionMode;


public class BottlesMain {

public static void main(String[] args) throws AnalysisException, IOException {
	 IDatasetProfiler datasetProfiler = new IDatasetProfilerFactory().createDatasetProfiler();
	
	 Instant startTime = Instant.now();
	 StructType schema = getBottlesCsvSchema();
	 String alias = "bottles";
	 String path = String.format(
	         "src%stest%sresources%sdatasets%sbottles2_clustering.csv",
	         File.separator, File.separator, File.separator, File.separator);
	
	 datasetProfiler.registerDataset(alias, path, schema);
	 
	 //datasetProfiler.declareOutlierParameters(OutlierType.Z_SCORE, 1.0);
	 //RegressionRequest regressionRequest = new RegressionRequest();
	 //regressionRequest.addRegression(new RegressionParameters(
	 //		Arrays.asList("T_degC"), "Salnty", RegressionType.LINEAR, null));
	 //regressionRequest.addRegression(new RegressionParameters(
	//		 Arrays.asList("T_degC", "STheta", "R_SVA"), "Salnty", RegressionType.MULTIPLE_LINEAR, null));
	 //regressionRequest.addRegression(new RegressionParameters(
	 //		null, "Salnty", RegressionType.AUTOMATED, 0.05));
	 //regressionRequest.addRegression(new RegressionParameters(
	 //		Arrays.asList("T_degC"), "Salnty", RegressionType.POLYNOMIAL, (double)6));
	// datasetProfiler.declareRegressionRequest(regressionRequest);
	 
	 datasetProfiler.declareClusteringParameters(ClusteringType.KMEANS, 4,
	 		Arrays.asList("EXIST"));
	 //datasetProfiler.declareClusteringParameters(ClusteringType.DIVISIVE, 4,
	 //		Arrays.asList("EXIST"));
	 //datasetProfiler.declareClusteringParameters(ClusteringType.GRAPH_BASED, 4,
	 //		Arrays.asList("EXIST"));
	 
	 boolean shouldRunDescriptiveStats = false;
	 boolean shouldRunHistograms = false;
	 boolean shouldRunAllPairsCorrelations = false;
	 boolean shouldRunDecisionTrees = false;
	 boolean shouldRunDominancePatterns = false;
	 boolean shouldRunOutlierDetection = false;
	 boolean shouldRunRegression = false;
	 boolean shouldRunClustering = true;
	 HighlightParameters highlightParameters = new HighlightParameters(HighlightExtractionMode.ALL, Double.MIN_VALUE);
	
	
	 datasetProfiler.computeProfileOfDataset(
	         new DatasetProfilerParameters(
	                 "results",
	                 shouldRunDescriptiveStats,
	                 shouldRunHistograms,
	                 shouldRunAllPairsCorrelations,
	                 shouldRunDecisionTrees,
	                 shouldRunDominancePatterns,
	                 shouldRunOutlierDetection,
	                 shouldRunRegression,
	                 shouldRunClustering,
	                 highlightParameters));
	
	
	 datasetProfiler.generateReport(ReportGeneratorConstants.TXT_REPORT, "");
	 datasetProfiler.generateReport(ReportGeneratorConstants.MD_REPORT, "");
	 
	 Instant endTime = Instant.now();
	 Duration duration = Duration.between(startTime, endTime);

     // Print the elapsed time in milliseconds and seconds
     long millis = duration.toMillis();
     double seconds = duration.toMillis() / 1000.0;
     
     // Print the elapsed time
     System.out.println("Elapsed Time (Milliseconds): " + millis);
     System.out.println("Elapsed Time (Seconds): " + seconds);
	}
	
	public static StructType getBottlesCsvSchema() {
	 return new StructType(
	         new StructField[]{
	                 new StructField("EXIST", DataTypes.StringType, true, Metadata.empty()),
	                 new StructField("scalar", DataTypes.DoubleType, true, Metadata.empty()),
	         });
	}

}
