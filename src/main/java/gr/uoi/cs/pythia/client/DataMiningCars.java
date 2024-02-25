package gr.uoi.cs.pythia.client;

import java.io.File;
import java.io.IOException;
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
import gr.uoi.cs.pythia.model.outlier.OutlierType;
import gr.uoi.cs.pythia.regression.RegressionParameters;
import gr.uoi.cs.pythia.regression.RegressionRequest;
import gr.uoi.cs.pythia.report.ReportGeneratorConstants;

import gr.uoi.cs.pythia.model.regression.RegressionType;

import gr.uoi.cs.pythia.util.HighlightParameters;
import gr.uoi.cs.pythia.util.HighlightParameters.HighlightExtractionMode;


// This class contains a main method specifically set up for the 'cars' dataset.
// Used to assist with development.
public class DataMiningCars {

  public static void main(String[] args) throws AnalysisException, IOException {
    IDatasetProfiler datasetProfiler = new IDatasetProfilerFactory().createDatasetProfiler();

    StructType schema = getCarsCsvSchema();
    String alias = "cars";
    String path = String.format(
            "src%stest%sresources%sdatasets%scars_100.csv",
            File.separator, File.separator, File.separator, File.separator);

    datasetProfiler.registerDataset(alias, path, schema);
    
    datasetProfiler.declareOutlierParameters(OutlierType.Z_SCORE, 1.0);
    RegressionRequest regressionRequest = new RegressionRequest();
    regressionRequest.addRegression(new RegressionParameters(
    		Arrays.asList("tax"), "price", RegressionType.LINEAR, null));
    regressionRequest.addRegression(new RegressionParameters(
    		Arrays.asList("tax", "mileage"), "price", RegressionType.MULTIPLE_LINEAR, null));
    regressionRequest.addRegression(new RegressionParameters(
    		null, "price", RegressionType.AUTOMATED, 0.05));
    regressionRequest.addRegression(new RegressionParameters(
    		Arrays.asList("tax"), "price", RegressionType.POLYNOMIAL, (double)3));
    datasetProfiler.declareRegressionRequest(regressionRequest);
    
    datasetProfiler.declareClusteringParameters(ClusteringType.KMEANS, 4,
    		Arrays.asList("manufacturer", "transmission", "fuelType"));
    //datasetProfiler.declareClusteringParameters(ClusteringType.DIVISIVE, 4,
    //		Arrays.asList("manufacturer", "transmission", "fuelType"));
    //datasetProfiler.declareClusteringParameters(ClusteringType.GRAPH_BASED, 4,
    //		Arrays.asList("manufacturer", "transmission", "fuelType"));
    
    boolean shouldRunDescriptiveStats = true;
    boolean shouldRunHistograms = false;
    boolean shouldRunAllPairsCorrelations = true;
    boolean shouldRunDecisionTrees = false;
    boolean shouldRunDominancePatterns = false;
    boolean shouldRunOutlierDetection = false;
    boolean shouldRunRegression = true;
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
  }

  public static StructType getCarsCsvSchema() {
    return new StructType(
            new StructField[]{
                    new StructField("manufacturer", DataTypes.StringType, true, Metadata.empty()),
                    new StructField("model", DataTypes.StringType, true, Metadata.empty()),
                    new StructField("year", DataTypes.IntegerType, true, Metadata.empty()),
                    new StructField("price", DataTypes.DoubleType, true, Metadata.empty()),
                    new StructField("transmission", DataTypes.StringType, true, Metadata.empty()),
                    new StructField("mileage", DataTypes.DoubleType, true, Metadata.empty()),
                    new StructField("fuelType", DataTypes.StringType, true, Metadata.empty()),
                    new StructField("tax", DataTypes.IntegerType, true, Metadata.empty()),
                    new StructField("mpg", DataTypes.DoubleType, true, Metadata.empty()),
                    new StructField("engineSize", DataTypes.DoubleType, true, Metadata.empty()),
            });
  }

}