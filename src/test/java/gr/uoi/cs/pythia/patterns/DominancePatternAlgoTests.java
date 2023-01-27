package gr.uoi.cs.pythia.patterns;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.File;
import java.util.Arrays;

import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;
import org.junit.Before;
import org.junit.Test;

import gr.uoi.cs.pythia.client.Patterns;
import gr.uoi.cs.pythia.config.SparkConfig;
import gr.uoi.cs.pythia.patterns.algos.DominancePatternAlgo;
import gr.uoi.cs.pythia.patterns.algos.IPatternAlgoFactory;
import gr.uoi.cs.pythia.patterns.results.DominancePatternResult;
import gr.uoi.cs.pythia.reader.IDatasetReaderFactory;

public class DominancePatternAlgoTests {

	private Dataset<Row> dataset;
	private DominancePatternAlgo dominancePatternAlgo;
	private String measurementColName;
	private String xCoordinateColName;
	private String yCoordinateColName;
	
	@Before
	public void init() throws AnalysisException {
		// Create a DominancePatternAlgo object
		dominancePatternAlgo = (DominancePatternAlgo) 
				new IPatternAlgoFactory()
				.createPattern(PatternConstants.DOMINANCE);
		
		// set column names
		measurementColName = "price";
		xCoordinateColName = "model";
		yCoordinateColName = "year";
		
		// and load the cars dataset (100 records version)
		StructType schema = Patterns.createCarsDatasetSchema();
		String path = String.format(
				"src%stest%sresources%sdatasets%scars_100.csv", 
				File.separator, File.separator, File.separator, File.separator);
		SparkConfig sparkConfig = new SparkConfig();
		
		dataset = new IDatasetReaderFactory(
			SparkSession.builder()
				.appName(sparkConfig.getAppName())
                .master(sparkConfig.getMaster())
                .config("spark.sql.warehouse.dir", sparkConfig.getSparkWarehouse())
                .getOrCreate())
			.createDataframeReader(path, schema).read();
	}
	
	@Test
	public void testIdentifyWithOneCoordinate() {
		DominancePatternResult expectedHighDominanceResults = 
				createExpectedHighDominanceResultsForOneCoordinate();
		DominancePatternResult expectedLowDominanceResults = 
				createExpectedLowDominanceResultsForOneCoordinate();
		
		dominancePatternAlgo.identifyPatternWithOneCoordinate(
				dataset, measurementColName, xCoordinateColName);
		
		DominancePatternResult actualHighDominanceResults = 
				dominancePatternAlgo.getResults().get(dominancePatternAlgo.getResults().size()-2);
		DominancePatternResult actualLowDominanceResults = 
				dominancePatternAlgo.getLatestResult();
		
		assertResultsAreEqual(expectedHighDominanceResults, actualHighDominanceResults);
		assertResultsAreEqual(expectedLowDominanceResults, actualLowDominanceResults);
	}
	
	@Test
	public void testIdentifyWithTwoCoordinates() {
		DominancePatternResult expectedHighDominanceResults = 
				createExpectedHighDominanceResultsForTwoCoordinates();
		DominancePatternResult expectedLowDominanceResults = 
				createExpectedLowDominanceResultsForTwoCoordinates();
		
		dominancePatternAlgo.identifyPatternWithTwoCoordinates(
				dataset, measurementColName, 
				xCoordinateColName, yCoordinateColName);
		
		DominancePatternResult actualHighDominanceResults = 
				dominancePatternAlgo.getResults().get(dominancePatternAlgo.getResults().size()-2);
		DominancePatternResult actualLowDominanceResults = 
				dominancePatternAlgo.getLatestResult();
		
		assertResultsAreEqual(expectedHighDominanceResults, actualHighDominanceResults);
		assertResultsAreEqual(expectedLowDominanceResults, actualLowDominanceResults);
	}
	
	@Test
	public void testIdentifyWithInvalidXCoordinate() {
		assertThrows(AnalysisException.class, () -> {
			dominancePatternAlgo.identifyPatternWithOneCoordinate(
					dataset, measurementColName, "INVALID_COORDINATE");
		});
	}
	
	@Test
	public void testIdentifyWithInvalidYCoordinate() {
		assertThrows(AnalysisException.class, () -> {
			dominancePatternAlgo.identifyPatternWithTwoCoordinates(
					dataset, measurementColName, 
					xCoordinateColName, "INVALID_COORDINATE");
		});
	}
	
	@Test
	public void testIdentifyWithInvalidMeasurement() {
		assertThrows(AnalysisException.class, () -> {
			dominancePatternAlgo.identifyPatternWithTwoCoordinates(
					dataset, "INVALID_MEASUREMENT", 
					xCoordinateColName, yCoordinateColName);
		});
	}
	
	private void assertResultsAreEqual(
			DominancePatternResult expected,
			DominancePatternResult actual) {
		assertEquals(expected.getNumOfCoordinates(), actual.getNumOfCoordinates());
		assertEquals(expected.getDominanceType(), actual.getDominanceType());
		assertEquals(expected.getAggregationMethod(), actual.getAggregationMethod());
		assertEquals(expected.getMeasurementColName(), actual.getMeasurementColName());
		assertEquals(expected.getXCoordinateColName(), actual.getXCoordinateColName());
		assertEquals(expected.getYCoordinateColName(), actual.getYCoordinateColName());
		assertArrayEquals(expected.getIdentificationResults().toArray(), actual.getIdentificationResults().toArray());
	}

	private DominancePatternResult createExpectedHighDominanceResultsForOneCoordinate() {
		DominancePatternResult expected = new DominancePatternResult(
				PatternConstants.HIGH, "sum", 
				measurementColName, xCoordinateColName);
		expected.addIdentificationResult("Q3", 9956610.0, 100.0, true, "total high");
		expected.addIdentificationResult("Q5", 2865320.0, 87.5, true, "partial high");
		expected.addIdentificationResult("A3", 1897299.0, 75.0, true, "partial high");
		expected.addIdentificationResult("A1", 197000.0, 62.5, false, "-");
		expected.addIdentificationResult("A5", 94570.0, 50.0, false, "-");
		expected.addIdentificationResult("Q2", 56499.0, 37.5, false, "-");

		return expected;
	}
	
	private DominancePatternResult createExpectedLowDominanceResultsForOneCoordinate() {
		DominancePatternResult expected = new DominancePatternResult(
				PatternConstants.LOW, "sum", 
				measurementColName, xCoordinateColName);
		
		expected.addIdentificationResult("A4", 6500.0, 100.0, true, "total low");
		expected.addIdentificationResult("S4", 23700.0, 87.5, true, "partial low");
		expected.addIdentificationResult("A6", 40465.0, 75.0, true, "partial low");
		expected.addIdentificationResult("Q2", 56499.0, 62.5, false, "-");
		expected.addIdentificationResult("A5", 94570.0, 50.0, false, "-");
		expected.addIdentificationResult("A1", 197000.0, 37.5, false, "-");
		return expected;
	}
	
	private DominancePatternResult createExpectedHighDominanceResultsForTwoCoordinates() {
		DominancePatternResult expected = new DominancePatternResult(
				PatternConstants.HIGH, "sum", 
				measurementColName, xCoordinateColName, yCoordinateColName);
		expected.addIdentificationResult(
				"Q3", Arrays.asList("A1", "A3", "A4", "A5", "A6", "Q2", "Q5", "S4"), 
				Arrays.asList("2014", "2015", "2016", "2017", "2019"),
				100.0, true, "total high", 9956610.0);
		expected.addIdentificationResult(
				"A3", Arrays.asList("A1", "A4", "A5", "A6", "Q2", "S4"), 
				Arrays.asList("2013", "2014", "2015", "2016", "2017", "2018", "2019"),
				75.0, true, "partial high", 1897299.0);
		expected.addIdentificationResult(
				"Q5", Arrays.asList("A1", "A3", "A4", "A6"), 
				Arrays.asList("2015", "2016"),
				50.0, false, "-", 2865320.0);
		expected.addIdentificationResult(
				"A1", Arrays.asList("A4", "A6", "S4"), 
				Arrays.asList("2016", "2017", "2018", "2014", "2015"),
				37.5, false, "-", 197000.0);
		expected.addIdentificationResult(
				"A5", Arrays.asList("A4", "A6", "S4"), 
				Arrays.asList("2017"),
				37.5, false, "-", 94570.0);
		expected.addIdentificationResult(
				"S4", Arrays.asList("A4", "A6"), 
				Arrays.asList("2017"),
				25.0, false, "-", 23700.0);
		return expected;
	}
	
	private DominancePatternResult createExpectedLowDominanceResultsForTwoCoordinates() {
		DominancePatternResult expected = new DominancePatternResult(
				PatternConstants.LOW, "sum", 
				measurementColName, xCoordinateColName, yCoordinateColName);
		expected.addIdentificationResult(
				"A4", Arrays.asList("A1", "A3", "A5", "A6", "Q2", "Q3", "Q5", "S4"), 
				Arrays.asList("2016", "2017", "2018", "2019"),
				100.0, true, "total low", 6500.0);
		expected.addIdentificationResult(
				"A6", Arrays.asList("A1", "A3", "A5", "Q3", "Q5", "S4"), 
				Arrays.asList("2015", "2016", "2017", "2018"),
				75.0, true, "partial low", 40465.0);
		expected.addIdentificationResult(
				"S4", Arrays.asList("A1", "A3", "A5", "Q3"), 
				Arrays.asList("2017"),
				50.0, false, "-", 23700.0);
		expected.addIdentificationResult(
				"A1", Arrays.asList("A3", "Q3", "Q5"), 
				Arrays.asList("2013", "2014", "2015", "2016", "2017", "2018"),
				37.5, false, "-", 197000.0);
		expected.addIdentificationResult(
				"A3", Arrays.asList("Q3", "Q5"), 
				Arrays.asList("2014", "2015", "2016", "2017", "2019"),
				25.0, false, "-", 1897299.0);
		expected.addIdentificationResult(
				"A5", Arrays.asList("A3", "Q3"), 
				Arrays.asList("2014", "2017"),
				25.0, false, "-", 94570.0);
		return expected;
	}
	
}
