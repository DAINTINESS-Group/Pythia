package gr.uoi.cs.pythia.engine;

import java.io.IOException;

import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.types.StructType;

import gr.uoi.cs.pythia.labeling.RuleSet;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.patterns.dominance.DominanceAnalysisParameters;

public interface IDatasetProfiler {

  void registerDataset(String alias, String path, StructType schema) throws AnalysisException;

  void computeLabeledColumn(RuleSet ruleSet);

  // TODO: Update. For now used to specify path parameter usage
  /**
   * Computes the statistics of the profile
   * @param path The directory where the auxiliary data,
   *             for example: images of the decision trees,
   *             will be generated, to later be used by the report.
   *             If it is empty or null, it will be generated in the dataset's folder.
   * @return A DatasetProfile object that contains all the statistical info
   * @throws IOException
   */
  DatasetProfile computeProfileOfDataset(String path) throws IOException;

    /**
     * Generates the report of the statistical analysis in a file of a specific format,
     * at the designated location.
     * @param reportGeneratorType The output type of the report e.g. txt or md
     * @param path The output path of the report. If it is null or empty,
     *             the report will be generated inside the folder with the auxiliary data.
     * @throws IOException
     */
  void generateReport(String reportGeneratorType, String path) throws IOException;

  void writeDataset(String datasetWriterType, String path) throws IOException;
  
  // TODO this method should be removed from here & set to private at DatasetProfiler
  // once it is determined that the dev patterns main method is no longer required
  void identifyHighlightPatterns(DominanceAnalysisParameters dominanceAnalysisParameters) throws IOException;
}
