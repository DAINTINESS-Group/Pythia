package gr.uoi.cs.pythia.engine;

import static org.apache.spark.sql.functions.expr;

import gr.uoi.cs.pythia.config.SparkConfig;
import gr.uoi.cs.pythia.correlations.CorrelationsSystemConstants;
import gr.uoi.cs.pythia.correlations.ICorrelationsCalculatorFactory;
import gr.uoi.cs.pythia.decisiontree.DecisionTreeManager;
import gr.uoi.cs.pythia.labeling.RuleSet;
import gr.uoi.cs.pythia.model.*;
import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.reader.IDatasetReaderFactory;
import gr.uoi.cs.pythia.report.IReportGeneratorFactory;
import gr.uoi.cs.pythia.writer.IDatasetWriterFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
  public DatasetProfile computeProfileOfDataset() throws IOException {
    createOutputFolder();
    computeDescriptiveStats();
    computeAllPairsCorrelations();
    extractAllDecisionTrees();
    return datasetProfile;
  }

  private void createOutputFolder() throws IOException {
    String outputDirectoryParent = new File(datasetProfile.getPath()).getParent();
    String currentDateTime = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss"));
    String outputDirectory = outputDirectoryParent + File.separator + datasetProfile.getAlias() +
                             "_report_" + currentDateTime;
    Files.createDirectories(Paths.get(outputDirectory));
    datasetProfile.setOutputDirectory(outputDirectory);
  }

  private void computeDescriptiveStats() {
    Dataset<Row> descriptiveStatistics =
        dataset.summary(
            DatasetProfilerConstants.COUNT,
            DatasetProfilerConstants.MEAN,
            DatasetProfilerConstants.STANDARD_DEVIATION,
            DatasetProfilerConstants.MEDIAN,
            DatasetProfilerConstants.MIN,
            DatasetProfilerConstants.MAX);

    List<Column> columns = datasetProfile.getColumns();
    Set<String> summaryColumns = new HashSet<>(Arrays.asList(descriptiveStatistics.columns()));
    for (Column column : columns) {
      if (summaryColumns.contains(column.getName())) {
        List<Row> columnNames = descriptiveStatistics.select(column.getName()).collectAsList();
        List<Object> descriptiveStatisticsRow =
            columnNames.stream().map(col -> col.get(0)).collect(Collectors.toList());

        String count = (String) descriptiveStatisticsRow.get(0);
        String mean = (String) descriptiveStatisticsRow.get(1);
        String standardDeviation = (String) descriptiveStatisticsRow.get(2);
        String median = (String) descriptiveStatisticsRow.get(3);
        String min = (String) descriptiveStatisticsRow.get(4);
        String max = (String) descriptiveStatisticsRow.get(5);

        DescriptiveStatisticsProfile columnDescriptiveStatisticsProfile =
            new DescriptiveStatisticsProfile(count, mean, standardDeviation, median, min, max);
        column.setDescriptiveStatisticsProfile(columnDescriptiveStatisticsProfile);
      }
      logger.info(
          String.format(
              "Computed Descriptive Statistics Profile for column: %s", column.getName()));
    }
  }

  private void computeAllPairsCorrelations() {
    new ICorrelationsCalculatorFactory()
        .createCorrelationsCalculator(CorrelationsSystemConstants.PEARSON)
        .calculateAllPairsCorrelations(dataset, datasetProfile);
    logger.info(String.format("Computed Correlations Profile for %s", datasetProfile.getPath()));
  }

  private void extractAllDecisionTrees() throws IOException {
    List<String> labeledColumnNames = new DecisionTreeManager(dataset, datasetProfile)
            .extractAllDecisionTrees();
    for (String labeledColumnName : labeledColumnNames) {
        logger.info(String.format("Computed Decision Tree(s) for labeled column: %s", labeledColumnName));
    }
  }

  @Override
  public void generateReport(String reportGeneratorType, String path) throws IOException {
    new IReportGeneratorFactory()
        .createReportGenerator(reportGeneratorType)
        .produceReport(datasetProfile, path);
    logger.info(
        String.format(
            "Generated %s report for %s: %s.",
            reportGeneratorType, datasetProfile.getAlias(), path));
  }

  @Override
  public void writeDataset(String datasetWriterType, String path) throws IOException {
    new IDatasetWriterFactory().createDatasetWriter(datasetWriterType).write(dataset, path);
    logger.info(
        String.format("Exported dataset to %s using the %s writer.", path, datasetWriterType));
  }
}
