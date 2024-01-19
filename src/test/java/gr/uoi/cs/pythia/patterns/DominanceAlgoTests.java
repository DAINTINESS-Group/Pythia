package gr.uoi.cs.pythia.patterns;

import static gr.uoi.cs.pythia.patterns.dominance.DominanceAlgoFactory.DominanceAlgoVersion.OPTIMIZED_HIGH;
import static gr.uoi.cs.pythia.patterns.dominance.DominanceAlgoFactory.DominanceAlgoVersion.OPTIMIZED_LOW;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import gr.uoi.cs.pythia.patterns.dominance.DominanceAlgoFactory;
import gr.uoi.cs.pythia.patterns.dominance.IDominanceAlgo;
import gr.uoi.cs.pythia.patterns.dominance.OptimizedHighDominanceAlgo;
import gr.uoi.cs.pythia.patterns.dominance.OptimizedLowDominanceAlgo;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.Before;
import org.junit.Test;

import gr.uoi.cs.pythia.model.dominance.DominanceResult;

public class DominanceAlgoTests {

	private Dataset<Row> dataset;
	private IDominanceAlgo highDominanceAlgo;
	private IDominanceAlgo lowDominanceAlgo;
	private String measurementColName;
	private String xCoordinateColName;
	private String yCoordinateColName;
	
	@Before
	public void init() throws AnalysisException {
		// Set column names
		measurementColName = "price";
		xCoordinateColName = "model";
		yCoordinateColName = "year";
		
		// Get dataset
		dataset = AllPatternTests.patternsResource.getDataset();
		
		// Create high & low dominance pattern algo objects
		// Default to the optimized dominance algo versions
		DominanceAlgoFactory factory = new DominanceAlgoFactory();
		highDominanceAlgo = factory.generateDominanceAlgo(OPTIMIZED_HIGH, dataset);
		lowDominanceAlgo = factory.generateDominanceAlgo(OPTIMIZED_LOW, dataset);
	}
	
	@Test
	public void testIdentifyHighDominanceWithOneCoordinate() {
		DominanceResult expectedHighDominanceResults = 
				createExpectedHighDominanceResultsForOneCoordinate();

		DominanceResult actualHighDominanceResults = highDominanceAlgo
				.identifyDominanceWithOneCoordinate(measurementColName, xCoordinateColName);
		
		assertResultsAreEqual(expectedHighDominanceResults, actualHighDominanceResults);
		
	}
	
	@Test
	public void testIdentifyLowDominanceWithOneCoordinate() {
		DominanceResult expectedLowDominanceResults = 
				createExpectedLowDominanceResultsForOneCoordinate();
		
		DominanceResult actualLowDominanceResults = lowDominanceAlgo
				.identifyDominanceWithOneCoordinate(
						measurementColName, xCoordinateColName);
		
		assertResultsAreEqual(expectedLowDominanceResults, actualLowDominanceResults);
	}
	
	@Test
	public void testIdentifyHighDominanceWithTwoCoordinates() {
		DominanceResult expectedHighDominanceResults = 
				createExpectedHighDominanceResultsForTwoCoordinates();
		
		DominanceResult actualHighDominanceResults = highDominanceAlgo
				.identifyDominanceWithTwoCoordinates(
						measurementColName, xCoordinateColName, yCoordinateColName);
		
		assertResultsAreEqual(expectedHighDominanceResults, actualHighDominanceResults);
	}
	
	@Test
	public void testIdentifyLowDominanceWithTwoCoordinates() {
		DominanceResult expectedLowDominanceResults = 
				createExpectedLowDominanceResultsForTwoCoordinates();
		
		DominanceResult actualLowDominanceResults = lowDominanceAlgo
				.identifyDominanceWithTwoCoordinates(
						measurementColName, xCoordinateColName, yCoordinateColName);
		
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
				"high", "sum", 
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
				"low", "sum", 
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
				"high", "sum", 
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
				"low", "sum", 
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
