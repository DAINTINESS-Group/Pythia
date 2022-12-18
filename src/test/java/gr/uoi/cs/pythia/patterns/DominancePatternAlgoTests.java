package gr.uoi.cs.pythia.patterns;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.File;
import java.io.IOException;

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
import gr.uoi.cs.pythia.patterns.results.DominancePatternResults;
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
        String alias = "cars";
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
	public void testIdentifyWithOneCoordinate() throws IOException {		
		dominancePatternAlgo.identify(dataset, measurementColName, xCoordinateColName);
		
		DominancePatternResults expected = createExpectedHighlightsForOneCoordinate();
		DominancePatternResults actual = dominancePatternAlgo.getResults();
		
		assertResultsAreEqual(expected, actual);
	}
	
	@Test
	public void testIdentifyWithTwoCoordinates() throws IOException {
		dominancePatternAlgo.identify(dataset, measurementColName, 
				xCoordinateColName, yCoordinateColName);
		
		DominancePatternResults expected = createExpectedHighlightsForTwoCoordinates();
		DominancePatternResults actual = dominancePatternAlgo.getResults();
		
		assertResultsAreEqual(expected, actual);
	}
	
	@Test
	public void testIdentifyWithInvalidXCoordinate() throws IOException {
		assertThrows(AnalysisException.class, () -> {
			dominancePatternAlgo.identify(dataset, measurementColName, "INVALID_COORDINATE");
		});
	}
	
	@Test
	public void testIdentifyWithInvalidYCoordinate() throws IOException {
		assertThrows(AnalysisException.class, () -> {
			dominancePatternAlgo.identify(dataset, measurementColName, 
					xCoordinateColName, "INVALID_COORDINATE");
		});
	}
	
	@Test
	public void testIdentifyWithInvalidMeasurement() throws IOException {
		assertThrows(AnalysisException.class, () -> {
			dominancePatternAlgo.identify(dataset, "INVALID_MEASUREMENT", 
					xCoordinateColName, yCoordinateColName);
		});
	}
	
	private void assertResultsAreEqual(
			DominancePatternResults expected,
			DominancePatternResults actual) {
		assertEquals(expected.getTitle(), actual.getTitle());
		assertEquals(expected.getAggregationMethod(), actual.getAggregationMethod());
		assertEquals(expected.getMeasurementColumnName(), actual.getMeasurementColumnName());
		assertEquals(expected.getXCoordinateColName(), actual.getXCoordinateColName());
		assertEquals(expected.getYCoordinateColName(), actual.getYCoordinateColName());
		assertArrayEquals(expected.getHighlights().toArray(), actual.getHighlights().toArray());
		assertArrayEquals(expected.getResults().toArray(), actual.getResults().toArray());
	}

	private DominancePatternResults createExpectedHighlightsForOneCoordinate() {
		DominancePatternResults expected = new DominancePatternResults(
				"## Dominance Pattern Results With One Coordinate Column",
				"avg", measurementColName, xCoordinateColName);
		expected.addResult("A1", 27769.058, 25.0, 75.0, true, "partial low");
		expected.addResult("A3", 31739.59, 50.0, 50.0, false, "-");
		expected.addResult("A4", 31529.538, 37.5, 62.5, false, "-");
		expected.addResult("A5", 34838.333, 62.5, 37.5, false, "-");
		expected.addResult("A6", 40924.5, 87.5, 12.5, true, "partial high");
		expected.addResult("Q2", 6080.5, 0.0, 100.0, true, "total low");
		expected.addResult("Q3", 35638.0, 75.0, 25.0, true, "partial high");
		expected.addResult("Q5", 40967.8, 100.0, 0.0, true, "total high");
		expected.addResult("S4", 20278.0, 12.5, 87.5, true, "partial low");
		return expected;
	}
	
	private DominancePatternResults createExpectedHighlightsForTwoCoordinates() {
		DominancePatternResults expected = new DominancePatternResults(
				"## Dominance Pattern Results With Two Coordinate Columns",
				"avg", measurementColName, xCoordinateColName, yCoordinateColName);
		expected.addResult("A1", "2013", 76269.0, 100.0, 0.0, true, "total high");
		expected.addResult("A3", "2013", 51441.0, 0.0, 100.0, true, "total low");
		expected.addResult("A1", "2014", 31319.5, 33.333, 66.666, false, "-");
		expected.addResult("A3", "2014", 30516.0, 0.0, 100.0, true, "total low");         
		expected.addResult("A5", "2014", 83872.0, 100.0, 0.0, true, "total high");        
		expected.addResult("Q3", "2014", 38831.666, 66.666, 33.333, false, "-");                 
		expected.addResult("A1", "2015",54754.0, 50.0, 50.0, false, "-");                 
		expected.addResult("A3", "2015", 59340.666, 75.0, 25.0, true, "partial high");      
		expected.addResult("A6", "2015", 50719.0, 25.0, 75.0, true, "partial low");       
		expected.addResult("Q3", "2015", 30075.0, 0.0, 100.0, true, "total low");         
		expected.addResult("Q5", "2015", 89483.0, 100.0, 0.0, true, "total high");        
		expected.addResult("A1", "2016", 22752.142, 0.0, 100.0, true, "total low");         
		expected.addResult("A3", "2016", 41334.0, 80.0, 20.0,  true, "partial high");      
		expected.addResult("A4", "2016", 75062.0, 100.0, 0.0,  true, "total high");        
		expected.addResult("A6", "2016", 40637.6, 60.0, 40.0,  false, "-");                 
		expected.addResult("Q3", "2016", 37497.0, 40.0, 60.0, false, "-");                 
		expected.addResult("Q5", "2016", 28839.0, 20.0, 80.0, true, "partial low");       
		expected.addResult("A1", "2017", 21670.8, 16.666, 83.333, true, "partial low");       
		expected.addResult("A3", "2017", 26427.5, 83.333, 16.666, true, "partial high");      
		expected.addResult("A4", "2017", 21725.5, 33.333, 66.666, false, "-");                 
		expected.addResult("A5", "2017", 25031.6, 66.666, 33.333, false, "-");                 
		expected.addResult("A6", "2017", 22582.0, 50.0, 50.0, false, "-");                 
		expected.addResult("Q3", "2017", 37231.5, 100.0, 0.0, true, "total high");        
		expected.addResult("S4", "2017", 20278.0, 0.0, 100.0, true, "total low");         
		expected.addResult("A1", "2018", 10793.0, 0.0, 100.0, true, "total low");         
		expected.addResult("A3", "2018", 17992.0, 33.333, 66.666, false, "-");                 
		expected.addResult("A4", "2018", 20172.5, 66.666, 33.333, false, "-");                 
		expected.addResult("A6", "2018", 22958.0, 100.0, 0.0, true, "total high");        
		expected.addResult("A3", "2019", 3432.75, 0.0, 100.0, true, "total low");         
		expected.addResult("A4", "2019", 7000.0, 100.0, 0.0, true, "total high");        
		expected.addResult("Q2", "2019", 6080.5, 66.666, 33.333, false, "-");                 
		expected.addResult("Q3", "2019", 4000.0, 33.333, 66.666, false, "-");                 
		return expected;
	}
	
}
