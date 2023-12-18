package gr.uoi.cs.pythia.engine;

import static org.apache.spark.sql.functions.expr;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import gr.uoi.cs.pythia.config.SparkConfig;
import gr.uoi.cs.pythia.correlations.CorrelationsCalculatorFactory;
import gr.uoi.cs.pythia.correlations.CorrelationsMethod;
import gr.uoi.cs.pythia.correlations.ICorrelationsCalculator;
import gr.uoi.cs.pythia.decisiontree.DecisionTreeManager;
import gr.uoi.cs.pythia.descriptivestatistics.DescriptiveStatisticsFactory;
import gr.uoi.cs.pythia.descriptivestatistics.IDescriptiveStatisticsCalculator;
import gr.uoi.cs.pythia.highlights.HighlightsManager;
import gr.uoi.cs.pythia.histogram.HistogramManager;
import gr.uoi.cs.pythia.labeling.RuleSet;
import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.LabeledColumn;
import gr.uoi.cs.pythia.model.outlier.OutlierType;
import gr.uoi.cs.pythia.model.regression.RegressionType;
import gr.uoi.cs.pythia.patterns.IPatternManager;
import gr.uoi.cs.pythia.patterns.IPatternManagerFactory;
import gr.uoi.cs.pythia.patterns.dominance.DominanceColumnSelectionMode;
import gr.uoi.cs.pythia.patterns.dominance.DominanceParameters;
import gr.uoi.cs.pythia.reader.IDatasetReaderFactory;
import gr.uoi.cs.pythia.regression.IRegressionPerformer;
import gr.uoi.cs.pythia.regression.RegressionParameters;
import gr.uoi.cs.pythia.regression.RegressionPerformerFactory;
import gr.uoi.cs.pythia.report.IReportGenerator;
import gr.uoi.cs.pythia.report.ReportGeneratorFactory;
import gr.uoi.cs.pythia.writer.DatasetWriterFactory;
import gr.uoi.cs.pythia.writer.IDatasetWriter;

public class DatasetProfiler implements IDatasetProfiler {

	private final Logger logger = Logger.getLogger(DatasetProfiler.class);
	private final IDatasetReaderFactory dataFrameReaderFactory;
	private DatasetProfile datasetProfile;
	private Dataset<Row> dataset;
	private DominanceParameters dominanceParameters;
	private boolean hasComputedDescriptiveStats;
	private boolean hasComputedAllPairsCorrelations;
	private OutlierType outlierType;
	private double outlierThreshold;
	private RegressionParameters regressionParameters;

	private HighlightsManager highlightsManager;
	
	public DatasetProfiler() {
		SparkConfig sparkConfig = new SparkConfig();
		this.dataFrameReaderFactory = new IDatasetReaderFactory(
				SparkSession.builder().appName(sparkConfig.getAppName()).master(sparkConfig.getMaster())
						.config("spark.sql.warehouse.dir", sparkConfig.getSparkWarehouse()).getOrCreate());
		hasComputedDescriptiveStats = false;
		hasComputedAllPairsCorrelations = false;
	}

	@Override
	public void registerDataset(String alias, String path, StructType schema) throws AnalysisException {
		Instant start = Instant.now();
		
		dataset = dataFrameReaderFactory.createDataframeReader(path, schema).read();

		List<Column> columns = new ArrayList<>();
		StructField[] fields = dataset.schema().fields();
		for (int i = 0; i < fields.length; ++i) {
			columns.add(new Column(i, fields[i].name(), fields[i].dataType().toString()));
		}
		datasetProfile = new DatasetProfile(alias, path, columns);
		logger.info(String.format("Registered Dataset file with alias '%s' at %s", alias, path));
		
		Instant end = Instant.now();
		Duration duration = Duration.between(start, end);
		logger.info(String.format("Duration of registerDataset: %s / %sms", duration, duration.toMillis()));
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
		datasetProfile.getColumns()
				.add(new LabeledColumn(datasetProfile.getColumns().size(), columnName, dataType.toString(), ruleSet));
		logger.info(String.format("Added labeled column: %s", columnName));
	}

	@Override
	public void declareDominanceParameters(
			DominanceColumnSelectionMode dominanceColumnSelectionMode,
			String[] measurementColumns, String[] coordinateColumns) {
		this.dominanceParameters = new DominanceParameters(
				dominanceColumnSelectionMode,
				measurementColumns, coordinateColumns);
		logger.info(String.format("Declared dominance parameters with \"%s\" column selection mode", 
				dominanceColumnSelectionMode.toString()));
	}
	
	
	@Override
	public DatasetProfile getDatasetProfile() {
		//TODO why is this needed? Apparently, only at tests?
		//TODO if needed:
		//TODO add copy constructor at DataSetProfile and return a copy?
		return datasetProfile;
	}
	
	@Override
	public void declareOutlierParameters(OutlierType outlierType, double outlierThreshold) {
		if(null == outlierType) {
			this.outlierType = OutlierType.Z_SCORE;
		}
		if(outlierThreshold < 0) {
			this.outlierThreshold = 3.0;
		}
		this.outlierType = outlierType;
		this.outlierThreshold = outlierThreshold;	
	}
	
	@Override
	public void declareRegressionParameters(List<String> independentVariables, String dependentVariable, RegressionType regressionType) {
		this.regressionParameters = new RegressionParameters(independentVariables, dependentVariable, regressionType);
	}
	
	@Override
	public DatasetProfile computeProfileOfDataset(DatasetProfilerParameters parameters) 
			throws IOException {
		String path = parameters.getAuxiliaryDataOutputDirectory();
		createOutputFolder(path);
				
		if (parameters.shouldRunDescriptiveStats()) computeDescriptiveStats();
		if (parameters.shouldRunHistograms()) computeAllHistograms();
		if (parameters.shouldRunAllPairsCorrelations()) computeAllPairsCorrelations();
		if (parameters.shouldRunDecisionTrees()) extractAllDecisionTrees();
		if (parameters.shouldRunDominancePatterns()) identifyDominancePatterns();
		if (parameters.shouldRunOutlierDetection()) identifyOutliers();
		if (parameters.shouldRunRegression()) performRegression();
		
		this.extractHighlightsForStorytelling(parameters.shouldRunDescriptiveStats(),
				parameters.shouldRunHistograms(),
				parameters.shouldRunAllPairsCorrelations(),
				parameters.shouldRunDecisionTrees(),
				parameters.shouldRunDominancePatterns(),
				parameters.shouldRunOutlierDetection());
		
		return datasetProfile;
	}

	private void createOutputFolder(String path) throws IOException {
		if (isInvalidPath(path)) {
			path = new File(datasetProfile.getPath()).getParent();
		} else {
			// TODO: Maybe try and catch, and if exception -> set to default
			Files.createDirectories(Paths.get(path));
		}

		String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss"));
		String outputDirectory = path + File.separator + datasetProfile.getAlias() + "_results_" + currentDateTime;
		Files.createDirectories(Paths.get(outputDirectory));
		datasetProfile.setAuxiliaryDataOutputDirectory(outputDirectory);
	}

	private void computeDescriptiveStats() {
		Instant start = Instant.now();
		
		DescriptiveStatisticsFactory factory = new DescriptiveStatisticsFactory();
		IDescriptiveStatisticsCalculator calculator = factory.getDefaultCalculator();
		calculator.computeDescriptiveStats(dataset, datasetProfile);
		logger.info(
				String.format("Computed Descriptive Statistics Profile for dataset: '%s'", datasetProfile.getAlias()));
		hasComputedDescriptiveStats = true;
		
		Instant end = Instant.now();
		Duration duration = Duration.between(start, end);
		logger.info(String.format("Duration of computeDescriptiveStats: %s / %sms", duration, duration.toMillis()));
		
	}

	private void computeAllHistograms() throws IOException {
		Instant start = Instant.now();
		
		HistogramManager histogramManager = new HistogramManager(datasetProfile, dataset);
		histogramManager.createAllHistograms();
		logger.info(String.format("Computed Histogram(s) for dataset: '%s'", datasetProfile.getAlias()));
		
		Instant end = Instant.now();
		Duration duration = Duration.between(start, end);
		logger.info(String.format("Duration of computeAllHistograms: %s / %sms", duration, duration.toMillis()));
	}

	private void computeAllPairsCorrelations() {
		Instant start = Instant.now();
		
		CorrelationsCalculatorFactory factory = new CorrelationsCalculatorFactory();
		ICorrelationsCalculator calculator = factory.createCorrelationsCalculator(CorrelationsMethod.PEARSON);
		calculator.calculateAllPairsCorrelations(dataset, datasetProfile);
		logger.info(String.format("Computed Correlations Profile for dataset: '%s'", datasetProfile.getAlias()));
		hasComputedAllPairsCorrelations = true;
		
		Instant end = Instant.now();
		Duration duration = Duration.between(start, end);
		logger.info(String.format("Duration of computeAllPairsCorrelations: %s / %sms", duration, duration.toMillis()));
	}

	private void extractAllDecisionTrees() throws IOException {
		Instant start = Instant.now();
		
		DecisionTreeManager decisionTreeManager = new DecisionTreeManager(dataset, datasetProfile);
		List<String> labeledColumnNames = decisionTreeManager.extractAllDecisionTrees();
		for (String labeledColumnName : labeledColumnNames) {
			logger.info(String.format("Computed Decision Tree(s) for labeled column: %s", labeledColumnName));
		}
		
		Instant end = Instant.now();
		Duration duration = Duration.between(start, end);
		logger.info(String.format("Duration of extractAllDecisionTrees: %s / %sms", duration, duration.toMillis()));
	}

	private void identifyDominancePatterns() throws IOException {
		Instant start = Instant.now();
		
		if (!hasDeclaredDominanceParameters()) {
			logger.info(String.format(
					"Dominance parameters not declared. " +
					"Skipped highlight pattern identification for dataset %s", datasetProfile.getAlias()));
			return;
		}
		if (!hasComputedDescriptiveStats) computeDescriptiveStats();
		if (!hasComputedAllPairsCorrelations) computeAllPairsCorrelations();
		IPatternManagerFactory factory = new IPatternManagerFactory();
		IPatternManager patternManager = factory.createPatternManager(
				dataset, datasetProfile, dominanceParameters, outlierType, outlierThreshold);
		patternManager.identifyDominancePatterns();
		logger.info(String.format("Identified highlight patterns for dataset %s", datasetProfile.getAlias()));
		
		Instant end = Instant.now();
		Duration duration = Duration.between(start, end);
		logger.info(String.format("Duration of identifyHighlightPatterns: %s / %sms", duration, duration.toMillis()));
	}
	
	private void identifyOutliers() throws IOException {
		Instant start = Instant.now();

		if (!hasComputedDescriptiveStats) computeDescriptiveStats();
		if (!hasComputedAllPairsCorrelations) computeAllPairsCorrelations();
		IPatternManagerFactory factory = new IPatternManagerFactory();
		IPatternManager patternManager = factory.createPatternManager(
				dataset, datasetProfile, dominanceParameters, outlierType, outlierThreshold);
		patternManager.identifyOutliers();
		logger.info(String.format("Identified outliers for dataset %s", datasetProfile.getAlias()));
		
		Instant end = Instant.now();		
		Duration duration = Duration.between(start, end);
		logger.info(String.format("Duration of identifyOutliers: %s / %sms", duration, duration.toMillis()));
	}
	
	private void performRegression() throws IOException {
		Instant start = Instant.now();
		if (!hasComputedAllPairsCorrelations) computeAllPairsCorrelations();
		
		logger.info(String.format("Performed regression for dataset %s", datasetProfile.getAlias()));
		
		RegressionPerformerFactory factory = new RegressionPerformerFactory();
		IRegressionPerformer regressionPerformer = factory.createRegressionPerformer(regressionParameters);
		regressionPerformer.performRegression(dataset, datasetProfile);
		
		Instant end = Instant.now();
		Duration duration = Duration.between(start, end);
		logger.info(String.format("Duration of perfomRegression: %s / %sms", duration, duration.toMillis()));
	}

	@Override
	public void generateReport(String reportGeneratorType, String outputDirectoryPath) 
			throws IOException {
		Instant start = Instant.now();
		
		if (isInvalidPath(outputDirectoryPath)) {
			outputDirectoryPath = datasetProfile.getAuxiliaryDataOutputDirectory();
		}
		ReportGeneratorFactory factory = new ReportGeneratorFactory();
		IReportGenerator generator = factory.createReportGenerator(reportGeneratorType);
		generator.produceReport(datasetProfile, outputDirectoryPath);
		logger.info(String.format("Generated %s report for dataset '%s' under the directory: %s.", 
				reportGeneratorType, datasetProfile.getAlias(), outputDirectoryPath));
		
		Instant end = Instant.now();
		Duration duration = Duration.between(start, end);
		logger.info(String.format("Duration of generateReport (%s): %s / %sms", reportGeneratorType, duration, duration.toMillis()));
	}

	@Override
	public void writeDataset(String datasetWriterType, String path) throws IOException {
		DatasetWriterFactory factory = new DatasetWriterFactory();
		IDatasetWriter datasetWriter = factory.createDatasetWriter(datasetWriterType);
		datasetWriter.write(dataset, path);
		logger.info(String.format("Exported dataset to %s using the %s writer.", path, datasetWriterType));
	}
	
	
	private void extractHighlightsForStorytelling(boolean descriptiveStats, boolean histograms, 
									boolean allPairsCorrelations, boolean decisionTrees, boolean highlightPatterns, boolean outlierDetection) {
		highlightsManager = new HighlightsManager(datasetProfile);
		highlightsManager.extractHighlightsForStorytelling(descriptiveStats, histograms, allPairsCorrelations, decisionTrees,
														highlightPatterns, outlierDetection);
		
	}

	private boolean isInvalidPath(String path) {
		return path == null || path.isEmpty();
	}
	
	private boolean hasDeclaredDominanceParameters() {
		return dominanceParameters != null;
	}
	

}
