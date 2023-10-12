package gr.uoi.cs.pythia.engine;

import java.io.IOException;

import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.types.StructType;

import gr.uoi.cs.pythia.labeling.RuleSet;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.patterns.dominance.DominanceColumnSelectionMode;

public interface IDatasetProfiler {

  /**
   * Registers a dataset into the system such that profiling can be performed.
   *
   * @param alias  - Alias of the dataset within Pythia.
   * @param path   - Path of the dataset file.
   * @param schema - The names of each column of the dataset along with their data type.
   * @throws AnalysisException
   */
  void registerDataset(String alias, String path, StructType schema) throws AnalysisException;

  /**
   * Computes a new labeled column for the registered dataset.
   *
   * @param ruleSet - A rule set based on which the labeled column is computed.
   */
  void computeLabeledColumn(RuleSet ruleSet);

  /**
   * Declares parameters for dominance highlight pattern identification. This method is required
   * for overall highlight pattern identification to proceed.
   *
   * @param dominanceColumnSelectionMode - Enum which corresponds to the mode
   *                                     based on which columns involved in dominance pattern identification will be selected
   *                                     (e.g. smart mode, exhaustive mode). This parameter is optional. If null is given, smart mode
   *                                     will be picked by default.
   * @param measurementColumns           - Names of specific measurement columns that should be
   *                                     involved in dominance identification.
   * @param coordinateColumns            - Names of specific coordinate columns that should be involved
   *                                     in dominance identification.
   */
  void declareDominanceParameters(
          DominanceColumnSelectionMode dominanceColumnSelectionMode,
          String[] measurementColumns,
          String[] coordinateColumns);

  /**
   * This is the main method that automatically computes the profile of a registered dataset.
   *
   * @param parameters - Parameters that specify which dataset analysis parts should be executed,
   *                   as well as the directory path where the auxiliary data (e.g. images of the decision trees) will be generated,
   *                   such that they can later be used in the report.
   * @return A DatasetProfile object that contains all the analysis results.
   * @throws IOException
   */
  DatasetProfile computeProfileOfDataset(DatasetProfilerParameters parameters)
          throws IOException;

  /**
   * Generates a report with all the generated analysis results in a file of a specific format,
   * at the designated path.
   *
   * @param reportGeneratorType - The output type of the report (e.g. txt or md).
   * @param outputDirectoryPath - The output path of the directory where report files will be generated.
   *                            If it is null or empty, the report will be generated inside the directory with the auxiliary data.
   * @throws IOException
   */
  void generateReport(String reportGeneratorType, String outputDirectoryPath) throws IOException;

  /**
   * Writes a registered dataset back to the drive at the designated path.
   *
   * @param datasetWriterType - A string that declares the type of the dataset writer that should
   *                          be used (e.g. naive dataset writer or hadoop dataset writer).
   * @param path              - The output path were the dataset will be exported.
   * @throws IOException
   */
  void writeDataset(String datasetWriterType, String path) throws IOException;
  
  
  /**
   * Extracts the highlights of the produced models for storytelling purposes.
   * 
   * @param descriptiveStats - A boolean that informs whether descriptive stats are produced for the dataset
   * @param histograms - A boolean that informs whether histograms are produced for the dataset
   * @param allPairsCorrelations - A boolean that informs whether correlations are produced for the dataset
   * @param decisionTrees - A boolean that informs whether decision trees are produced for the dataset
   * @param highlightPatterns - A boolean that informs whether highlight patterns are produced for the dataset
   * 
   * 
   */
  void extractHighlightsForStorytelling(boolean descriptiveStats, boolean histograms,
		  boolean allPairsCorrelations, boolean decisionTrees, boolean highlightPatterns);

}
