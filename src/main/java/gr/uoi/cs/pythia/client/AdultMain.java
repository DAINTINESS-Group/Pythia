package gr.uoi.cs.pythia.client;

import gr.uoi.cs.pythia.engine.DatasetProfilerParameters;
import gr.uoi.cs.pythia.engine.IDatasetProfiler;
import gr.uoi.cs.pythia.engine.IDatasetProfilerFactory;
import gr.uoi.cs.pythia.patterns.dominance.DominanceColumnSelectionMode;
import gr.uoi.cs.pythia.report.ReportGeneratorConstants;
import gr.uoi.cs.pythia.util.HighlightParameters;
import gr.uoi.cs.pythia.util.HighlightParameters.HighlightExtractionMode;
import org.apache.log4j.Logger;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

//This class contains a main method specifically set up for the 'adult' dataset.
//Used to assist with development and experimental evaluation.
public class AdultMain {

	private static final Logger logger = Logger.getLogger(AdultMain.class);
	
	public static void main(String[] args) throws AnalysisException, IOException {
		Instant start = Instant.now();
		
		IDatasetProfiler datasetProfiler = new IDatasetProfilerFactory().createDatasetProfiler();

	    StructType schema = getAdultCsvSchema();
	    String alias = "adult";
	    String path = String.format(
	            "src%stest%sresources%sdatasets%sAdult100K.csv",
	            File.separator, File.separator, File.separator, File.separator);

	    datasetProfiler.registerDataset(alias, path, schema);
	    datasetProfiler.declareDominanceParameters(
	            DominanceColumnSelectionMode.USER_SPECIFIED_ONLY,
	            new String[] {"hours_per_week"},
	            new String[] {"native_country", "occupation", "gender"}
	    );
		/**
		 * Missing
		 */
		//Danger!!
		datasetProfiler.declareOutlierParameters(null,0.3); // Null pointer exception  threashold !!

	    boolean shouldRunDescriptiveStats = true;
	    boolean shouldRunHistograms = true;
	    boolean shouldRunAllPairsCorrelations = true;
	    boolean shouldRunDecisionTrees = true;
	    boolean shouldRunDominancePatterns = true;
	    boolean shouldRunOutlierDetection = false;
	    boolean shouldRunRegression = false;
	    boolean shouldRunClustering = false;
	    HighlightParameters highlightParameters = new HighlightParameters(HighlightExtractionMode.NONE, Double.MAX_VALUE);
	    
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
	    
	    Instant end = Instant.now();
		Duration duration = Duration.between(start, end);
		logger.info(String.format("Total executiontime: %s / %sms", duration, duration.toMillis()));
	}
	
	public static StructType getAdultCsvSchema() {
	    return new StructType(
	            new StructField[]{
	            		new StructField("id", DataTypes.IntegerType, true, Metadata.empty()),
	            		new StructField("age", DataTypes.IntegerType, true, Metadata.empty()),
	            		new StructField("work_class", DataTypes.StringType, true, Metadata.empty()),
	            		new StructField("education", DataTypes.StringType, true, Metadata.empty()),
	            		new StructField("marital_status", DataTypes.StringType, true, Metadata.empty()),
	            		new StructField("occupation", DataTypes.StringType, true, Metadata.empty()),
	            		new StructField("race", DataTypes.StringType, true, Metadata.empty()),
	            		new StructField("gender", DataTypes.StringType, true, Metadata.empty()),
	            		new StructField("hours_per_week", DataTypes.IntegerType, true, Metadata.empty()),
	            		new StructField("native_country", DataTypes.StringType, true, Metadata.empty()),
	            		new StructField("salary", DataTypes.StringType, true, Metadata.empty()),
	            });
	}

}

