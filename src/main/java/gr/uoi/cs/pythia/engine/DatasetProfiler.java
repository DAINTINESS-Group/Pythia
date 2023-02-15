package gr.uoi.cs.pythia.engine;

import static org.apache.spark.sql.functions.expr;

import gr.uoi.cs.pythia.config.SparkConfig;
import gr.uoi.cs.pythia.correlations.CorrelationsMethod;
import gr.uoi.cs.pythia.correlations.CorrelationsCalculatorFactory;
import gr.uoi.cs.pythia.correlations.ICorrelationsCalculator;
import gr.uoi.cs.pythia.decisiontree.DecisionTreeManager;
import gr.uoi.cs.pythia.descriptivestatistics.DescriptiveStatisticsFactory;
import gr.uoi.cs.pythia.descriptivestatistics.IDescriptiveStatisticsCalculator;
import gr.uoi.cs.pythia.histogram.HistogramManager;
import gr.uoi.cs.pythia.labeling.RuleSet;
import gr.uoi.cs.pythia.model.*;
import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.reader.IDatasetReaderFactory;
import gr.uoi.cs.pythia.report.IReportGenerator;
import gr.uoi.cs.pythia.report.ReportGeneratorFactory;
import gr.uoi.cs.pythia.writer.DatasetWriterFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import gr.uoi.cs.pythia.writer.IDatasetWriter;
import org.apache.log4j.Logger;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

public class DatasetProfiler implements IDatasetProfiler {

  private final Logger logger = Logger.getLogger(DatasetProfiler.class);
  private final IDatasetReaderFactory dataFrameReaderFactory;
  private DatasetProfile datasetProfile;
  private Dataset<Row> dataset;

  public DatasetProfiler() {
    SparkConfig sparkConfig = new SparkConfig();
    this.dataFrameReaderFactory =
        new IDatasetReaderFactory(
            SparkSession.builder()
                .appName(sparkConfig.getAppName())
                .master(sparkConfig.getMaster())
                .config("spark.sql.warehouse.dir", sparkConfig.getSparkWarehouse())
                .getOrCreate());
  }

  @Override
  public void registerDataset(String alias, String path, StructType schema)
      throws AnalysisException {
    dataset = dataFrameReaderFactory.createDataframeReader(path, schema).read();


    List<String> columnNames =
        Arrays.stream(dataset.schema().fields())
            .map(StructField::name)
            .collect(Collectors.toList());
    List<String> dataTypes =
        Arrays.stream(dataset.schema().fields())
            .map(field -> field.dataType().toString())
            .collect(Collectors.toList());

    List<Column> columnProperties = new ArrayList<>();
    for (int i = 0; i < dataTypes.size(); ++i) {
      columnProperties.add(new Column(i, columnNames.get(i), dataTypes.get(i)));
    }
    datasetProfile = new DatasetProfile(alias, path, columnProperties);
    logger.info(String.format("Registered Dataset file with alias '%s' at %s", alias, path));

    // TODO: from alexxarisis - Maybe do this instead?
//    List<Column> columns = new ArrayList<>();
//    StructField[] fields = dataset.schema().fields();
//    for (int i = 0; i < fields.length; ++i) {
//      columns.add(new Column(i, fields[i].name(), fields[i].dataType().toString()));
//    }
//    datasetProfile = new DatasetProfile(alias, path, columns);
//    logger.info(String.format("Registered Dataset file with alias '%s' at %s", alias, path));
  }

  @Override
  public void computeLabeledColumn(RuleSet ruleSet) {
    // Add new column to the dataset
    String columnName = ruleSet.getNewColumnName();
    String labelingRulesAsExpression = ruleSet.generateSparkSqlExpression();
    dataset = dataset.withColumn(columnName, expr(labelingRulesAsExpression));

    // Create new LabeledColumn
    int index = (int) dataset.schema().getFieldIndex(columnName).get();
    DataType dataType = dataset.schema().fields()[index].dataType();
    datasetProfile.getColumns().add(new LabeledColumn(
            datasetProfile.getColumns().size(),
            columnName,
            dataType.toString(),
            ruleSet
    ));
    logger.info(String.format("Added labeled column: %s", columnName));
  }

  @Override
  public DatasetProfile computeProfileOfDataset(String path) throws IOException {
    createOutputFolder(path);
    computeDescriptiveStats();
    computeAllHistograms();
    computeAllPairsCorrelations();
    extractAllDecisionTrees();
    return datasetProfile;
  }

  private void createOutputFolder(String path) throws IOException {
    if (path == null || !new File(path).isDirectory()) {
      path = new File(datasetProfile.getPath()).getParent();
    }

    String currentDateTime = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss"));
    String outputDirectory = path + File.separator + datasetProfile.getAlias() +
                             "_report_" + currentDateTime;
    Files.createDirectories(Paths.get(outputDirectory));
    datasetProfile.setOutputDirectory(outputDirectory);
  }

  private void computeDescriptiveStats() {
    DescriptiveStatisticsFactory factory = new DescriptiveStatisticsFactory();
    IDescriptiveStatisticsCalculator calculator = factory.getDefaultCalculator();
    calculator.computeDescriptiveStats(dataset, datasetProfile);
    logger.info(String.format("Computed Descriptive Statistics Profile for dataset: '%s'", datasetProfile.getAlias()));
  }

  private void computeAllHistograms() throws IOException {
    HistogramManager histogramManager = new HistogramManager(datasetProfile, dataset);
    histogramManager.createAllHistograms();
    logger.info(String.format("Computed Histogram(s) for dataset: '%s'", datasetProfile.getAlias()));
  }

  private void computeAllPairsCorrelations() {
    CorrelationsCalculatorFactory factory = new CorrelationsCalculatorFactory();
    ICorrelationsCalculator calculator = factory.createCorrelationsCalculator(CorrelationsMethod.PEARSON);
    calculator.calculateAllPairsCorrelations(dataset, datasetProfile);
    logger.info(String.format("Computed Correlations Profile for dataset: '%s'", datasetProfile.getAlias()));
  }

  private void extractAllDecisionTrees() throws IOException {
    DecisionTreeManager decisionTreeManager = new DecisionTreeManager(dataset, datasetProfile);
    List<String> labeledColumnNames = decisionTreeManager.extractAllDecisionTrees();
    for (String labeledColumnName : labeledColumnNames) {
        logger.info(String.format("Computed Decision Tree(s) for labeled column: %s", labeledColumnName));
    }
  }

  @Override
  public void generateReport(String reportGeneratorType, String path) throws IOException {
    ReportGeneratorFactory factory = new ReportGeneratorFactory();
    IReportGenerator generator = factory.createReportGenerator(reportGeneratorType);
    generator.produceReport(datasetProfile, path);
    logger.info(
        String.format(
            "Generated %s report for dataset '%s': %s.",
            reportGeneratorType, datasetProfile.getAlias(), path));
  }

  @Override
  public void writeDataset(String datasetWriterType, String path) throws IOException {
    DatasetWriterFactory factory = new DatasetWriterFactory();
    IDatasetWriter datasetWriter = factory.createDatasetWriter(datasetWriterType);
    datasetWriter.write(dataset, path);
    logger.info(
        String.format("Exported dataset to %s using the %s writer.", path, datasetWriterType));
  }
}
