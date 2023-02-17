package gr.uoi.cs.pythia.engine;

import java.io.IOException;

import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.types.StructType;

import gr.uoi.cs.pythia.labeling.RuleSet;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.patterns.ColumnSelectionMode;

public interface IDatasetProfiler {

  void registerDataset(String alias, String path, StructType schema) throws AnalysisException;

  void computeLabeledColumn(RuleSet ruleSet);

  // TODO: Update. For now used to specify path parameter usage
  /**
   * Computes the statistics of the profile
   * @param path The directory where the auxiliary data,
   *             for example: images of the decision trees,
   *             will be generated, to later be used by the report system
   * @return A DatasetProfile object that contains all the statistical info
   * @throws IOException
   */
  DatasetProfile computeProfileOfDataset(String path) throws IOException;

  void generateReport(String reportGeneratorType, String path) throws IOException;

  void writeDataset(String datasetWriterType, String path) throws IOException;
  
  void identifyPatternHighlights(
		  ColumnSelectionMode columnSelectionMode, 
		  String[] measurementColNames,
		  String[] coordinateColNames) 
				  throws IOException;
}
