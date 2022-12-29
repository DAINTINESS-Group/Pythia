package gr.uoi.cs.pythia.engine;

import java.io.IOException;

import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.types.StructType;

import gr.uoi.cs.pythia.labeling.RuleSet;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.patterns.ColumnSelectionMode;

public interface IDatasetProfiler {

  void registerDataset(String alias, String path, StructType schema) throws AnalysisException;

  void computeLabeledColumn(RuleSet ruleSet) throws AnalysisException;

  DatasetProfile computeProfileOfDataset();

  void generateReport(String reportGeneratorType, String path) throws IOException;

  void writeDataset(String datasetWriterType, String path) throws IOException;
  
  void identifyPatternHighlights(
		  ColumnSelectionMode columnSelectionMode, 
		  String[] measurementColNames,
		  String[] coordinateColNames) 
				  throws IOException;
}
