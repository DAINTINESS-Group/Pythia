package gr.uoi.cs.pythia.patterns;

import static gr.uoi.cs.pythia.patterns.dominance.DominanceAlgoFactory.DominanceAlgoVersion.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gr.uoi.cs.pythia.patterns.dominance.DominanceAlgoFactory;
import gr.uoi.cs.pythia.patterns.dominance.IDominanceAlgo;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.Before;
import org.junit.Test;

import gr.uoi.cs.pythia.model.dominance.DominanceResult;

public class DominanceAlgoTests {

	private Dataset<Row> dataset;
	private IDominanceAlgo highDominanceAlgoV00;
	private IDominanceAlgo lowDominanceAlgoV00;
	private IDominanceAlgo highDominanceAlgoV01;
	private IDominanceAlgo lowDominanceAlgoV01;
	private IDominanceAlgo highDominanceAlgoV02;
	private IDominanceAlgo lowDominanceAlgoV02;
	private IDominanceAlgo dominanceAlgoV02;
	private String measurement;
	private String xCoordinate;
	private String yCoordinate;
	
	@Before
	public void init() throws AnalysisException {
		// Set column names
		measurement = "price";
		xCoordinate = "model";
		yCoordinate = "year";
		
		// Get dataset
		dataset = AllPatternTests.patternsResource.getDataset();
		
		// Create dominance algo objects
		DominanceAlgoFactory factory = new DominanceAlgoFactory();
		highDominanceAlgoV00 = factory.generateDominanceAlgo(V00_HIGH, dataset);
		lowDominanceAlgoV00 = factory.generateDominanceAlgo(V00_LOW, dataset);
		highDominanceAlgoV01 = factory.generateDominanceAlgo(V01_HIGH, dataset);
		lowDominanceAlgoV01 = factory.generateDominanceAlgo(V01_LOW, dataset);
		highDominanceAlgoV02 = factory.generateDominanceAlgo(V02_HIGH, dataset);
		lowDominanceAlgoV02 = factory.generateDominanceAlgo(V02_LOW, dataset);
		dominanceAlgoV02 = factory.generateDominanceAlgo(V02_HIGH_AND_LOW, dataset);
	}
	
	@Test
	public void testIdentifyHighAndLowSingleCoordinateDominanceV02() {
		DominanceResult expectedHighDominanceResults = createExpectedSingleCoordHighDominanceResults();
		DominanceResult expectedLowDominanceResults = createExpectedSingleCoordLowDominanceResults();

		Map<String, DominanceResult> results;
		results = dominanceAlgoV02.identifySingleCoordinateDominance(measurement, xCoordinate);
		DominanceResult actualHighDominanceResults = results.get("high");
		DominanceResult actualLowDominanceResults = results.get("low");

		assertResultsAreEqual(expectedHighDominanceResults, actualHighDominanceResults);
		assertResultsAreEqual(expectedLowDominanceResults, actualLowDominanceResults);
	}

	@Test
	public void testIdentifySingleCoordinateHighDominanceV02() {
		DominanceResult expectedHighDominanceResults = createExpectedSingleCoordHighDominanceResults();

		Map<String, DominanceResult> results;
		results = highDominanceAlgoV02.identifySingleCoordinateDominance(measurement, xCoordinate);
		DominanceResult actualHighDominanceResults = results.get("high");

		assertResultsAreEqual(expectedHighDominanceResults, actualHighDominanceResults);
	}

	@Test
	public void testIdentifySingleCoordinateHighDominanceV01() {
		DominanceResult expectedHighDominanceResults = createExpectedSingleCoordHighDominanceResults();

		Map<String, DominanceResult> results;
		results = highDominanceAlgoV01.identifySingleCoordinateDominance(measurement, xCoordinate);
		DominanceResult actualHighDominanceResults = results.get("high");

		assertResultsAreEqual(expectedHighDominanceResults, actualHighDominanceResults);
	}

	@Test
	public void testIdentifySingleCoordinateHighDominanceV00() {
		DominanceResult expectedHighDominanceResults = createExpectedSingleCoordHighDominanceResults();

		Map<String, DominanceResult> results;
		results = highDominanceAlgoV00.identifySingleCoordinateDominance(measurement, xCoordinate);
		DominanceResult actualHighDominanceResults = results.get("high");

		assertResultsAreEqual(expectedHighDominanceResults, actualHighDominanceResults);
	}

	@Test
	public void testIdentifySingleCoordinateLowDominanceV02() {
		DominanceResult expectedLowDominanceResults = createExpectedSingleCoordLowDominanceResults();

		Map<String, DominanceResult> results;
		results = lowDominanceAlgoV02.identifySingleCoordinateDominance(measurement, xCoordinate);
		DominanceResult actualLowDominanceResults = results.get("low");

		assertResultsAreEqual(expectedLowDominanceResults, actualLowDominanceResults);
	}

	@Test
	public void testIdentifySingleCoordinateLowDominanceV01() {
		DominanceResult expectedLowDominanceResults = createExpectedSingleCoordLowDominanceResults();

		Map<String, DominanceResult> results;
		results = lowDominanceAlgoV01.identifySingleCoordinateDominance(measurement, xCoordinate);
		DominanceResult actualLowDominanceResults = results.get("low");

		assertResultsAreEqual(expectedLowDominanceResults, actualLowDominanceResults);
	}

	@Test
	public void testIdentifySingleCoordinateLowDominanceV00() {
		DominanceResult expectedLowDominanceResults = createExpectedSingleCoordLowDominanceResults();

		Map<String, DominanceResult> results;
		results = lowDominanceAlgoV00.identifySingleCoordinateDominance(measurement, xCoordinate);
		DominanceResult actualLowDominanceResults = results.get("low");

		assertResultsAreEqual(expectedLowDominanceResults, actualLowDominanceResults);
	}

	@Test
	public void testIdentifyHighAndLowDoubleCoordinateDominanceV02() {
		DominanceResult expectedHighDominanceResults = createExpectedDoubleCoordHighDominanceResults();
		DominanceResult expectedLowDominanceResults = createExpectedDoubleCoordLowDominanceResults();

		Map<String, DominanceResult> results;
		results = dominanceAlgoV02.identifyDoubleCoordinateDominance(measurement, xCoordinate, yCoordinate);
		DominanceResult actualHighDominanceResults = results.get("high");
		DominanceResult actualLowDominanceResults = results.get("low");

		assertResultsAreEqual(expectedHighDominanceResults, actualHighDominanceResults);
		assertResultsAreEqual(expectedLowDominanceResults, actualLowDominanceResults);
	}

	@Test
	public void testIdentifyDoubleCoordinateHighDominanceV02() {
		DominanceResult expectedHighDominanceResults = createExpectedDoubleCoordHighDominanceResults();

		Map<String, DominanceResult> results;
		results = highDominanceAlgoV02.identifyDoubleCoordinateDominance(measurement, xCoordinate, yCoordinate);
		DominanceResult actualHighDominanceResults = results.get("high");

		assertResultsAreEqual(expectedHighDominanceResults, actualHighDominanceResults);
	}

	@Test
	public void testIdentifyDoubleCoordinateHighDominanceV01() {
		DominanceResult expectedHighDominanceResults = createExpectedDoubleCoordHighDominanceResults();

		Map<String, DominanceResult> results;
		results = highDominanceAlgoV01.identifyDoubleCoordinateDominance(measurement, xCoordinate, yCoordinate);
		DominanceResult actualHighDominanceResults = results.get("high");

		assertResultsAreEqual(expectedHighDominanceResults, actualHighDominanceResults);
	}

	@Test
	public void testIdentifyDoubleCoordinateHighDominanceV00() {
		DominanceResult expectedHighDominanceResults = createExpectedDoubleCoordHighDominanceResults();

		Map<String, DominanceResult> results;
		results = highDominanceAlgoV00.identifyDoubleCoordinateDominance(measurement, xCoordinate, yCoordinate);
		DominanceResult actualHighDominanceResults = results.get("high");

		assertResultsAreEqual(expectedHighDominanceResults, actualHighDominanceResults);
	}
	
	@Test
	public void testIdentifyDoubleCoordinateLowDominanceV02() {
		DominanceResult expectedLowDominanceResults = createExpectedDoubleCoordLowDominanceResults();

		Map<String, DominanceResult> results;
		results = lowDominanceAlgoV02.identifyDoubleCoordinateDominance(measurement, xCoordinate, yCoordinate);
		DominanceResult actualLowDominanceResults = results.get("low");

		assertResultsAreEqual(expectedLowDominanceResults, actualLowDominanceResults);
	}

	@Test
	public void testIdentifyDoubleCoordinateLowDominanceV01() {
		DominanceResult expectedLowDominanceResults = createExpectedDoubleCoordLowDominanceResults();

		Map<String, DominanceResult> results;
		results = lowDominanceAlgoV01.identifyDoubleCoordinateDominance(measurement, xCoordinate, yCoordinate);
		DominanceResult actualLowDominanceResults = results.get("low");

		assertResultsAreEqual(expectedLowDominanceResults, actualLowDominanceResults);
	}

	@Test
	public void testIdentifyDoubleCoordinateLowDominanceV00() {
		DominanceResult expectedLowDominanceResults = createExpectedDoubleCoordLowDominanceResults();

		Map<String, DominanceResult> results;
		results = lowDominanceAlgoV00.identifyDoubleCoordinateDominance(measurement, xCoordinate, yCoordinate);
		DominanceResult actualLowDominanceResults = results.get("low");

		assertResultsAreEqual(expectedLowDominanceResults, actualLowDominanceResults);
	}
	
	@Test
	public void testIdentifyWithInvalidXCoordinateV02() {
		assertThrows(AnalysisException.class, () -> {
			dominanceAlgoV02.identifySingleCoordinateDominance(
					measurement, "INVALID_COORDINATE");
		});
		assertThrows(AnalysisException.class, () -> {
			highDominanceAlgoV02.identifySingleCoordinateDominance(
					measurement, "INVALID_COORDINATE");
		});
		assertThrows(AnalysisException.class, () -> {
			lowDominanceAlgoV02.identifySingleCoordinateDominance(
					measurement, "INVALID_COORDINATE");
		});
	}

	@Test
	public void testIdentifyWithInvalidXCoordinateV01() {
		assertThrows(AnalysisException.class, () -> {
			highDominanceAlgoV01.identifySingleCoordinateDominance(
					measurement, "INVALID_COORDINATE");
		});
		assertThrows(AnalysisException.class, () -> {
			lowDominanceAlgoV01.identifySingleCoordinateDominance(
					measurement, "INVALID_COORDINATE");
		});
	}

	@Test
	public void testIdentifyWithInvalidXCoordinateV00() {
		assertThrows(AnalysisException.class, () -> {
			highDominanceAlgoV00.identifySingleCoordinateDominance(
					measurement, "INVALID_COORDINATE");
		});
		assertThrows(AnalysisException.class, () -> {
			lowDominanceAlgoV00.identifySingleCoordinateDominance(
					measurement, "INVALID_COORDINATE");
		});
	}

	@Test
	public void testIdentifyWithInvalidYCoordinateV02() {
		assertThrows(AnalysisException.class, () -> {
			dominanceAlgoV02.identifyDoubleCoordinateDominance(
					measurement, xCoordinate, "INVALID_COORDINATE");
		});
		assertThrows(AnalysisException.class, () -> {
			highDominanceAlgoV02.identifyDoubleCoordinateDominance(
					measurement, xCoordinate, "INVALID_COORDINATE");
		});
		assertThrows(AnalysisException.class, () -> {
			lowDominanceAlgoV02.identifyDoubleCoordinateDominance(
					measurement, xCoordinate, "INVALID_COORDINATE");
		});
	}

	@Test
	public void testIdentifyWithInvalidYCoordinateV01() {
		assertThrows(AnalysisException.class, () -> {
			highDominanceAlgoV01.identifyDoubleCoordinateDominance(
					measurement, xCoordinate, "INVALID_COORDINATE");
		});
		assertThrows(AnalysisException.class, () -> {
			lowDominanceAlgoV01.identifyDoubleCoordinateDominance(
					measurement, xCoordinate, "INVALID_COORDINATE");
		});
	}

	@Test
	public void testIdentifyWithInvalidYCoordinateV00() {
		assertThrows(AnalysisException.class, () -> {
			highDominanceAlgoV00.identifyDoubleCoordinateDominance(
					measurement, xCoordinate, "INVALID_COORDINATE");
		});
		assertThrows(AnalysisException.class, () -> {
			lowDominanceAlgoV00.identifyDoubleCoordinateDominance(
					measurement, xCoordinate, "INVALID_COORDINATE");
		});
	}
	
	@Test
	public void testIdentifyWithInvalidMeasurementV02() {
		assertThrows(AnalysisException.class, () -> {
			dominanceAlgoV02.identifyDoubleCoordinateDominance(
					"INVALID_MEASUREMENT", xCoordinate, yCoordinate);
		});
		assertThrows(AnalysisException.class, () -> {
			highDominanceAlgoV02.identifyDoubleCoordinateDominance(
					"INVALID_MEASUREMENT", xCoordinate, yCoordinate);
		});
		assertThrows(AnalysisException.class, () -> {
			lowDominanceAlgoV02.identifyDoubleCoordinateDominance(
					"INVALID_MEASUREMENT", xCoordinate, yCoordinate);
		});
	}

	@Test
	public void testIdentifyWithInvalidMeasurementV01() {
		assertThrows(AnalysisException.class, () -> {
			highDominanceAlgoV01.identifyDoubleCoordinateDominance(
					"INVALID_MEASUREMENT", xCoordinate, yCoordinate);
		});
		assertThrows(AnalysisException.class, () -> {
			lowDominanceAlgoV01.identifyDoubleCoordinateDominance(
					"INVALID_MEASUREMENT", xCoordinate, yCoordinate);
		});
	}

	@Test
	public void testIdentifyWithInvalidMeasurementV00() {
		assertThrows(AnalysisException.class, () -> {
			highDominanceAlgoV00.identifyDoubleCoordinateDominance(
					"INVALID_MEASUREMENT", xCoordinate, yCoordinate);
		});
		assertThrows(AnalysisException.class, () -> {
			lowDominanceAlgoV00.identifyDoubleCoordinateDominance(
					"INVALID_MEASUREMENT", xCoordinate, yCoordinate);
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

	private DominanceResult createExpectedSingleCoordHighDominanceResults() {
		DominanceResult expected = new DominanceResult(
				"high", "sum",
				measurement, xCoordinate);
		expected.addIdentificationResult("Q3", 9956610.0, 100.0, true, "total high");
		expected.addIdentificationResult("Q5", 2865320.0, 87.5, true, "partial high");
		expected.addIdentificationResult("A3", 1897299.0, 75.0, true, "partial high");
		expected.addIdentificationResult("A1", 197000.0, 62.5, false, "-");
		expected.addIdentificationResult("A5", 94570.0, 50.0, false, "-");
		expected.addIdentificationResult("Q2", 56499.0, 37.5, false, "-");

		return expected;
	}
	
	private DominanceResult createExpectedSingleCoordLowDominanceResults() {
		DominanceResult expected = new DominanceResult(
				"low", "sum",
				measurement, xCoordinate);
		
		expected.addIdentificationResult("A4", 6500.0, 100.0, true, "total low");
		expected.addIdentificationResult("S4", 23700.0, 87.5, true, "partial low");
		expected.addIdentificationResult("A6", 40465.0, 75.0, true, "partial low");
		expected.addIdentificationResult("Q2", 56499.0, 62.5, false, "-");
		expected.addIdentificationResult("A5", 94570.0, 50.0, false, "-");
		expected.addIdentificationResult("A1", 197000.0, 37.5, false, "-");
		return expected;
	}
	
	@SuppressWarnings("serial")
	private DominanceResult createExpectedDoubleCoordHighDominanceResults() {
		DominanceResult expected = new DominanceResult(
				"high", "sum",
				measurement, xCoordinate, yCoordinate, null);
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
	private DominanceResult createExpectedDoubleCoordLowDominanceResults() {
		DominanceResult expected = new DominanceResult(
				"low", "sum",
				measurement, xCoordinate, yCoordinate, null);
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
