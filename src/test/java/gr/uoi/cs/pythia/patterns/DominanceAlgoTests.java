package gr.uoi.cs.pythia.patterns;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;
import org.junit.Before;
import org.junit.Test;

import gr.uoi.cs.pythia.client.Patterns;
import gr.uoi.cs.pythia.config.SparkConfig;
import gr.uoi.cs.pythia.patterns.dominance.DominanceConstants;
import gr.uoi.cs.pythia.patterns.dominance.DominanceResult;
import gr.uoi.cs.pythia.patterns.dominance.HighDominanceAlgo;
import gr.uoi.cs.pythia.patterns.dominance.LowDominanceAlgo;
import gr.uoi.cs.pythia.reader.IDatasetReaderFactory;

public class DominanceAlgoTests {

	private Dataset<Row> dataset;
	private HighDominanceAlgo highDominanceAlgo;
	private LowDominanceAlgo lowDominanceAlgo;
	private String measurementColName;
	private String xCoordinateColName;
	private String yCoordinateColName;
	
	@Before
	public void init() throws AnalysisException {
		// Set column names
		measurementColName = "price";
		xCoordinateColName = "model";
		yCoordinateColName = "year";
		
		// Load the cars dataset (100 records version)
		StructType schema = Patterns.createCarsDatasetSchema();
		String path = String.format("src%stest%sresources%sdatasets%scars_100.csv",
				File.separator, File.separator, File.separator, File.separator);
		SparkConfig sparkConfig = new SparkConfig();
		
		dataset = new IDatasetReaderFactory(
			SparkSession.builder()
				.appName(sparkConfig.getAppName())
                .master(sparkConfig.getMaster())
                .config("spark.sql.warehouse.dir", sparkConfig.getSparkWarehouse())
                .getOrCreate())
			.createDataframeReader(path, schema).read();
		
		// Create high & low dominance pattern algo objects
		highDominanceAlgo = new HighDominanceAlgo(dataset);
		lowDominanceAlgo = new LowDominanceAlgo(dataset);
	}
	
	@Test
	public void testIdentifyHighDominanceWithOneCoordinate() {
		DominanceResult expectedHighDominanceResults = 
				createExpectedHighDominanceResultsForOneCoordinate();

		highDominanceAlgo.identifyDominanceWithOneCoordinate(
				measurementColName, xCoordinateColName);
		
		DominanceResult actualHighDominanceResults = 
				highDominanceAlgo.getLatestResult();
		
		assertResultsAreEqual(expectedHighDominanceResults, actualHighDominanceResults);
		
	}
	
	@Test
	public void testIdentifyLowDominanceWithOneCoordinate() {
		DominanceResult expectedLowDominanceResults = 
				createExpectedLowDominanceResultsForOneCoordinate();
		
		lowDominanceAlgo.identifyDominanceWithOneCoordinate(
				measurementColName, xCoordinateColName);
		
		DominanceResult actualLowDominanceResults = 
				lowDominanceAlgo.getLatestResult();
		
		assertResultsAreEqual(expectedLowDominanceResults, actualLowDominanceResults);
	}
	
	@Test
	public void testIdentifyHighDominanceWithTwoCoordinates() {
		DominanceResult expectedHighDominanceResults = 
				createExpectedHighDominanceResultsForTwoCoordinates();
		
		highDominanceAlgo.identifyDominanceWithTwoCoordinates(
				measurementColName, xCoordinateColName, yCoordinateColName);
		
		DominanceResult actualHighDominanceResults = 
				highDominanceAlgo.getLatestResult();

		assertResultsAreEqual(expectedHighDominanceResults, actualHighDominanceResults);
	}
	
	@Test
	public void testIdentifyLowDominanceWithTwoCoordinates() {
		DominanceResult expectedLowDominanceResults = 
				createExpectedLowDominanceResultsForTwoCoordinates();
		
		lowDominanceAlgo.identifyDominanceWithTwoCoordinates(
				measurementColName, xCoordinateColName, yCoordinateColName);
		
		DominanceResult actualLowDominanceResults = 
				lowDominanceAlgo.getLatestResult();
		
		assertResultsAreEqual(expectedLowDominanceResults, actualLowDominanceResults);
	}
	
	@Test
	public void testIdentifyWithInvalidXCoordinate() {
		assertThrows(AnalysisException.class, () -> {
			highDominanceAlgo.identifyDominanceWithOneCoordinate(
					measurementColName, "INVALID_COORDINATE");
		});
		assertThrows(AnalysisException.class, () -> {
			lowDominanceAlgo.identifyDominanceWithOneCoordinate(
					measurementColName, "INVALID_COORDINATE");
		});
	}
	
	@Test
	public void testIdentifyWithInvalidYCoordinate() {
		assertThrows(AnalysisException.class, () -> {
			highDominanceAlgo.identifyDominanceWithTwoCoordinates(
					measurementColName, xCoordinateColName, "INVALID_COORDINATE");
		});
		assertThrows(AnalysisException.class, () -> {
			lowDominanceAlgo.identifyDominanceWithTwoCoordinates(
					measurementColName, xCoordinateColName, "INVALID_COORDINATE");
		});
	}
	
	@Test
	public void testIdentifyWithInvalidMeasurement() {
		assertThrows(AnalysisException.class, () -> {
			highDominanceAlgo.identifyDominanceWithTwoCoordinates(
					"INVALID_MEASUREMENT", xCoordinateColName, yCoordinateColName);
		});
		assertThrows(AnalysisException.class, () -> {
			lowDominanceAlgo.identifyDominanceWithTwoCoordinates(
					"INVALID_MEASUREMENT", xCoordinateColName, yCoordinateColName);
		});
	}
	
	private void assertResultsAreEqual(
			DominanceResult expected,
			DominanceResult actual) {
		assertEquals(expected.getNumOfCoordinates(), actual.getNumOfCoordinates());
		assertEquals(expected.getDominanceType(), actual.getDominanceType());
		assertEquals(expected.getAggregationMethod(), actual.getAggregationMethod());
		assertEquals(expected.getMeasurementColName(), actual.getMeasurementColName());
		assertEquals(expected.getXCoordinateColName(), actual.getXCoordinateColName());
		assertEquals(expected.getYCoordinateColName(), actual.getYCoordinateColName());
		assertArrayEquals(expected.getIdentificationResults().toArray(), actual.getIdentificationResults().toArray());
	}

	private DominanceResult createExpectedHighDominanceResultsForOneCoordinate() {
		DominanceResult expected = new DominanceResult(
				DominanceConstants.HIGH, "sum", 
				measurementColName, xCoordinateColName);
		expected.addIdentificationResult("Q3", 9956610.0, 100.0, true, "total high");
		expected.addIdentificationResult("Q5", 2865320.0, 87.5, true, "partial high");
		expected.addIdentificationResult("A3", 1897299.0, 75.0, true, "partial high");
		expected.addIdentificationResult("A1", 197000.0, 62.5, false, "-");
		expected.addIdentificationResult("A5", 94570.0, 50.0, false, "-");
		expected.addIdentificationResult("Q2", 56499.0, 37.5, false, "-");

		return expected;
	}
	
	private DominanceResult createExpectedLowDominanceResultsForOneCoordinate() {
		DominanceResult expected = new DominanceResult(
				DominanceConstants.LOW, "sum", 
				measurementColName, xCoordinateColName);
		
		expected.addIdentificationResult("A4", 6500.0, 100.0, true, "total low");
		expected.addIdentificationResult("S4", 23700.0, 87.5, true, "partial low");
		expected.addIdentificationResult("A6", 40465.0, 75.0, true, "partial low");
		expected.addIdentificationResult("Q2", 56499.0, 62.5, false, "-");
		expected.addIdentificationResult("A5", 94570.0, 50.0, false, "-");
		expected.addIdentificationResult("A1", 197000.0, 37.5, false, "-");
		return expected;
	}
	
	@SuppressWarnings("serial")
	private DominanceResult createExpectedHighDominanceResultsForTwoCoordinates() {
		DominanceResult expected = new DominanceResult(
				DominanceConstants.HIGH, "sum", 
				measurementColName, xCoordinateColName, yCoordinateColName, null);
		expected.addIdentificationResult(
				"Q3", Arrays.asList("A1", "A3", "A4", "A5", "A6", "Q2", "Q5", "S4"), 
				new HashMap<String, List<String>>()
				{{
				     put("A1", Arrays.asList("2014", "2015", "2016", "2017"));
				     put("A3", Arrays.asList("2014", "2015", "2016", "2017", "2019"));
				     put("A4", Arrays.asList("2016", "2017", "2019"));
				     put("A5", Arrays.asList("2014", "2017"));
				     put("A6", Arrays.asList("2015", "2016", "2017"));
				     put("Q2", Arrays.asList("2019"));
				     put("Q5", Arrays.asList("2015", "2016"));
				     put("S4", Arrays.asList("2017"));
				}},
				100.0, true, "total high", 9956610.0);
		expected.addIdentificationResult(
				"A3", Arrays.asList("A1", "A4", "A5", "A6", "Q2", "S4"), 
				new HashMap<String, List<String>>()
				{{
				     put("A1", Arrays.asList("2013", "2014", "2015", "2016", "2017", "2018"));
				     put("A4", Arrays.asList("2016", "2017", "2018", "2019"));
				     put("A5", Arrays.asList("2014", "2017"));
				     put("A6", Arrays.asList("2015", "2016", "2017", "2018"));
				     put("Q2", Arrays.asList("2019"));
				     put("S4", Arrays.asList("2017"));
				}},
				75.0, true, "partial high", 1897299.0);
		expected.addIdentificationResult(
				"Q5", Arrays.asList("A1", "A3", "A4", "A6"), 
				new HashMap<String, List<String>>()
				{{
				     put("A1", Arrays.asList("2015", "2016"));
				     put("A3", Arrays.asList("2015", "2016"));
				     put("A4", Arrays.asList("2016"));
				     put("A6", Arrays.asList("2015", "2016"));
				}},
				50.0, false, "-", 2865320.0);
		expected.addIdentificationResult(
				"A1", Arrays.asList("A4", "A6", "S4"), 
				new HashMap<String, List<String>>()
				{{
				     put("A4", Arrays.asList("2016", "2017", "2018"));
				     put("A6", Arrays.asList("2015", "2016", "2017", "2018"));
				     put("S4", Arrays.asList("2017"));
				}},
				37.5, false, "-", 197000.0);
		expected.addIdentificationResult(
				"A5", Arrays.asList("A4", "A6", "S4"), 
				new HashMap<String, List<String>>()
				{{
				     put("A4", Arrays.asList("2017"));
				     put("A6", Arrays.asList("2017"));
				     put("S4", Arrays.asList("2017"));
				}},
				37.5, false, "-", 94570.0);
		expected.addIdentificationResult(
				"S4", Arrays.asList("A4", "A6"), 
				new HashMap<String, List<String>>()
				{{
				     put("A4", Arrays.asList("2017"));
				     put("A6", Arrays.asList("2017"));
				}},
				25.0, false, "-", 23700.0);
		return expected;
	}
	
	@SuppressWarnings("serial")
	private DominanceResult createExpectedLowDominanceResultsForTwoCoordinates() {
		DominanceResult expected = new DominanceResult(
				DominanceConstants.LOW, "sum", 
				measurementColName, xCoordinateColName, yCoordinateColName, null);
		expected.addIdentificationResult(
				"A4", Arrays.asList("A1", "A3", "A5", "A6", "Q2", "Q3", "Q5", "S4"), 
				new HashMap<String, List<String>>()
				{{
				     put("A1", Arrays.asList("2016", "2017", "2018"));
				     put("A3", Arrays.asList("2016", "2017", "2018", "2019"));
				     put("A5", Arrays.asList("2017"));
				     put("A6", Arrays.asList("2016", "2017", "2018"));
				     put("Q2", Arrays.asList("2019"));
				     put("Q3", Arrays.asList("2016", "2017", "2019"));
				     put("Q5", Arrays.asList("2016"));
				     put("S4", Arrays.asList("2017"));
				}},
				100.0, true, "total low", 6500.0);
		expected.addIdentificationResult(
				"A6", Arrays.asList("A1", "A3", "A5", "Q3", "Q5", "S4"), 
				new HashMap<String, List<String>>()
				{{
				     put("A1", Arrays.asList("2015", "2016", "2017", "2018"));
				     put("A3", Arrays.asList("2015", "2016", "2017", "2018"));
				     put("A5", Arrays.asList("2017"));
				     put("Q3", Arrays.asList("2015", "2016", "2017"));
				     put("Q5", Arrays.asList("2015", "2016"));
				     put("S4", Arrays.asList("2017"));
				}},
				75.0, true, "partial low", 40465.0);
		expected.addIdentificationResult(
				"S4", Arrays.asList("A1", "A3", "A5", "Q3"), 
				new HashMap<String, List<String>>()
				{{
					put("A1", Arrays.asList("2017"));
					put("A3", Arrays.asList("2017"));
				    put("A5", Arrays.asList("2017"));
				    put("Q3", Arrays.asList("2017"));
				}},
				50.0, false, "-", 23700.0);
		expected.addIdentificationResult(
				"A1", Arrays.asList("A3", "Q3", "Q5"), 
				new HashMap<String, List<String>>()
				{{
					put("A3", Arrays.asList("2013", "2014", "2015", "2016", "2017", "2018"));
				    put("Q3", Arrays.asList("2014", "2015", "2016", "2017"));
				    put("Q5", Arrays.asList("2015", "2016"));
				}},
				37.5, false, "-", 197000.0);
		expected.addIdentificationResult(
				"A3", Arrays.asList("Q3", "Q5"), 
				new HashMap<String, List<String>>()
				{{
				    put("Q3", Arrays.asList("2014", "2015", "2016", "2017", "2019"));
				    put("Q5", Arrays.asList("2015", "2016"));
				}},
				25.0, false, "-", 1897299.0);
		expected.addIdentificationResult(
				"A5", Arrays.asList("A3", "Q3"), 
				new HashMap<String, List<String>>()
				{{
				    put("A3", Arrays.asList("2014", "2017"));
				    put("Q3", Arrays.asList("2014", "2017"));
				}},
				25.0, false, "-", 94570.0);
		return expected;
	}
	
}
