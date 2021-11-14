package gr.uoi.cs.pythia.engine;

import gr.uoi.cs.pythia.engine.labeling.RuleSet;
import gr.uoi.cs.pythia.model.DatasetProfile;
import java.util.LinkedHashMap;

public interface IDatasetProfiler {

  void registerDataset(String alias, String path, LinkedHashMap<String, String> schema);

  void computeLabeledColumn(RuleSet ruleSet);

  DatasetProfile computeProfileOfDataset();

  void generateReport(String reportGeneratorType, String path);

  void writeDataset(String datasetWriterType, String path);
}
