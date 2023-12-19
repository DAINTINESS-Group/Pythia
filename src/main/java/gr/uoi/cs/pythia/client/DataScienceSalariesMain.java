package gr.uoi.cs.pythia.client;

import java.io.File;
import java.io.IOException;

import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import gr.uoi.cs.pythia.engine.DatasetProfilerParameters;
import gr.uoi.cs.pythia.engine.IDatasetProfiler;
import gr.uoi.cs.pythia.engine.IDatasetProfilerFactory;
import gr.uoi.cs.pythia.patterns.dominance.DominanceColumnSelectionMode;
import gr.uoi.cs.pythia.report.ReportGeneratorConstants;
import gr.uoi.cs.pythia.util.HighlightParameters;
import gr.uoi.cs.pythia.util.HighlightParameters.HighlightExtractionMode;

// This class contains a main method specifically set up for the 'data_science_salaries' dataset.
// Used to assist with development.
public class DataScienceSalariesMain {

  public static void main(String[] args) throws AnalysisException, IOException {
    IDatasetProfiler datasetProfiler = new IDatasetProfilerFactory().createDatasetProfiler();

    StructType schema = getDataScienceSalariesCsvSchema();
    String alias = "data_science_salaries";
    String path = String.format(
            "src%stest%sresources%sdatasets%sdata_science_salaries.csv",
            File.separator, File.separator, File.separator, File.separator);

    datasetProfiler.registerDataset(alias, path, schema);
    datasetProfiler.declareDominanceParameters(
            DominanceColumnSelectionMode.SMART,
            new String[] {"salary_in_usd"},
            new String[] {"experience_level", "work_year"}
    );

    boolean shouldRunDescriptiveStats = true;
    boolean shouldRunHistograms = true;
    boolean shouldRunAllPairsCorrelations = true;
    boolean shouldRunDecisionTrees = false;
    boolean shouldRunDominancePatterns = true;
    boolean shouldRunOutlierDetection = false;
    boolean shouldRunRegression = false;
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
                    highlightParameters));

    datasetProfiler.generateReport(ReportGeneratorConstants.MD_REPORT, "");
    datasetProfiler.generateReport(ReportGeneratorConstants.TXT_REPORT, "");
  }

  public static StructType getDataScienceSalariesCsvSchema() {
    return new StructType(
            new StructField[]{
                    new StructField("work_year", DataTypes.StringType, true, Metadata.empty()),
                    new StructField("experience_level", DataTypes.StringType, true, Metadata.empty()),
                    new StructField("employment_type", DataTypes.StringType, true, Metadata.empty()),
                    new StructField("job_title", DataTypes.StringType, true, Metadata.empty()),
                    new StructField("salary", DataTypes.DoubleType, true, Metadata.empty()),
                    new StructField("salary_currency", DataTypes.StringType, true, Metadata.empty()),
                    new StructField("salary_in_usd", DataTypes.DoubleType, true, Metadata.empty()),
                    new StructField("employee_residence", DataTypes.StringType, true, Metadata.empty()),
                    new StructField("remote_ratio", DataTypes.IntegerType, true, Metadata.empty()),
                    new StructField("company_location", DataTypes.StringType, true, Metadata.empty()),
                    new StructField("company_size", DataTypes.StringType, true, Metadata.empty()),
            });
  }

}

