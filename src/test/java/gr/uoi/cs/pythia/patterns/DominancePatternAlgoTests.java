package gr.uoi.cs.pythia.patterns;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.File;

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
		measurementColName = "mileage";
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
		dominancePatternAlgo.identifyPatternWithOneCoordinate(
				dataset, measurementColName, xCoordinateColName);
		
		DominancePatternResult expected = createExpectedHighlightsForOneCoordinate();
		DominancePatternResult actual = dominancePatternAlgo.getLatestResult();
		
		assertResultsAreEqual(expected, actual);
	}
	
	@Test
	public void testIdentifyWithTwoCoordinates() {
		dominancePatternAlgo.identifyPatternWithTwoCoordinates(
				dataset, measurementColName, 
				xCoordinateColName, yCoordinateColName);
		
		DominancePatternResult expected = createExpectedHighlightsForTwoCoordinates();
		DominancePatternResult actual = dominancePatternAlgo.getLatestResult();
		
		assertResultsAreEqual(expected, actual);
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
		assertEquals(expected.getAggregationMethod(), actual.getAggregationMethod());
		assertEquals(expected.getMeasurementColName(), actual.getMeasurementColName());
		assertEquals(expected.getXCoordinateColName(), actual.getXCoordinateColName());
		assertEquals(expected.getYCoordinateColName(), actual.getYCoordinateColName());
		assertArrayEquals(expected.getHighlights().toArray(), actual.getHighlights().toArray());
		assertArrayEquals(expected.getIdentificationResults().toArray(), actual.getIdentificationResults().toArray());
	}

	private DominancePatternResult createExpectedHighlightsForOneCoordinate() {
		DominancePatternResult expected = new DominancePatternResult(
				"avg", measurementColName, xCoordinateColName);
		expected.addIdentificationResult("A1", 27769.058, 25.0, 75.0, true, "partial low");
		expected.addIdentificationResult("A3", 31739.59, 50.0, 50.0, false, "-");
		expected.addIdentificationResult("A4", 31529.538, 37.5, 62.5, false, "-");
		expected.addIdentificationResult("A5", 34838.333, 62.5, 37.5, false, "-");
		expected.addIdentificationResult("A6", 40924.5, 87.5, 12.5, true, "partial high");
		expected.addIdentificationResult("Q2", 6080.5, 0.0, 100.0, true, "total low");
		expected.addIdentificationResult("Q3", 35638.0, 75.0, 25.0, true, "partial high");
		expected.addIdentificationResult("Q5", 40967.8, 100.0, 0.0, true, "total high");
		expected.addIdentificationResult("S4", 20278.0, 12.5, 87.5, true, "partial low");
		return expected;
	}
	
	private DominancePatternResult createExpectedHighlightsForTwoCoordinates() {
		DominancePatternResult expected = new DominancePatternResult(
				"avg", measurementColName, xCoordinateColName, yCoordinateColName);
		expected.addIdentificationResult("A1", "2013", 76269.0, 100.0, 0.0, true, "total high");
		expected.addIdentificationResult("A3", "2013", 51441.0, 0.0, 100.0, true, "total low");
		expected.addIdentificationResult("A1", "2014", 31319.5, 33.333, 66.666, false, "-");
		expected.addIdentificationResult("A3", "2014", 30516.0, 0.0, 100.0, true, "total low");         
		expected.addIdentificationResult("A5", "2014", 83872.0, 100.0, 0.0, true, "total high");        
		expected.addIdentificationResult("Q3", "2014", 38831.666, 66.666, 33.333, false, "-");                 
		expected.addIdentificationResult("A1", "2015",54754.0, 50.0, 50.0, false, "-");                 
		expected.addIdentificationResult("A3", "2015", 59340.666, 75.0, 25.0, true, "partial high");      
		expected.addIdentificationResult("A6", "2015", 50719.0, 25.0, 75.0, true, "partial low");       
		expected.addIdentificationResult("Q3", "2015", 30075.0, 0.0, 100.0, true, "total low");         
		expected.addIdentificationResult("Q5", "2015", 89483.0, 100.0, 0.0, true, "total high");        
		expected.addIdentificationResult("A1", "2016", 22752.142, 0.0, 100.0, true, "total low");         
		expected.addIdentificationResult("A3", "2016", 41334.0, 80.0, 20.0,  true, "partial high");      
		expected.addIdentificationResult("A4", "2016", 75062.0, 100.0, 0.0,  true, "total high");        
		expected.addIdentificationResult("A6", "2016", 40637.6, 60.0, 40.0,  false, "-");                 
		expected.addIdentificationResult("Q3", "2016", 37497.0, 40.0, 60.0, false, "-");                 
		expected.addIdentificationResult("Q5", "2016", 28839.0, 20.0, 80.0, true, "partial low");       
		expected.addIdentificationResult("A1", "2017", 21670.8, 16.666, 83.333, true, "partial low");       
		expected.addIdentificationResult("A3", "2017", 26427.5, 83.333, 16.666, true, "partial high");      
		expected.addIdentificationResult("A4", "2017", 21725.5, 33.333, 66.666, false, "-");                 
		expected.addIdentificationResult("A5", "2017", 25031.6, 66.666, 33.333, false, "-");                 
		expected.addIdentificationResult("A6", "2017", 22582.0, 50.0, 50.0, false, "-");                 
		expected.addIdentificationResult("Q3", "2017", 37231.5, 100.0, 0.0, true, "total high");        
		expected.addIdentificationResult("S4", "2017", 20278.0, 0.0, 100.0, true, "total low");         
		expected.addIdentificationResult("A1", "2018", 10793.0, 0.0, 100.0, true, "total low");         
		expected.addIdentificationResult("A3", "2018", 17992.0, 33.333, 66.666, false, "-");                 
		expected.addIdentificationResult("A4", "2018", 20172.5, 66.666, 33.333, false, "-");                 
		expected.addIdentificationResult("A6", "2018", 22958.0, 100.0, 0.0, true, "total high");        
		expected.addIdentificationResult("A3", "2019", 3432.75, 0.0, 100.0, true, "total low");         
		expected.addIdentificationResult("A4", "2019", 7000.0, 100.0, 0.0, true, "total high");        
		expected.addIdentificationResult("Q2", "2019", 6080.5, 66.666, 33.333, false, "-");                 
		expected.addIdentificationResult("Q3", "2019", 4000.0, 33.333, 66.666, false, "-");                 
		return expected;
	}
	
}
