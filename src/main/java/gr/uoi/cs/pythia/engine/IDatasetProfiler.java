package gr.uoi.cs.pythia.engine;

import gr.uoi.cs.pythia.labeling.RuleSet;
import gr.uoi.cs.pythia.model.DatasetProfile;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.types.StructType;
import java.io.IOException;

public interface IDatasetProfiler {

  void registerDataset(String alias, String path, StructType schema) throws AnalysisException;

  void computeLabeledColumn(RuleSet ruleSet) throws AnalysisException;

  DatasetProfile computeProfileOfDataset();

  void generateReport(String reportGeneratorType, String path) throws IOException;

  void writeDataset(String datasetWriterType, String path) throws IOException;
  
  void identifyPatternHighlights();
}
