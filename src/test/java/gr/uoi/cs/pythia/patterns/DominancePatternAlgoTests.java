package gr.uoi.cs.pythia.patterns;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;

import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.StructType;
import org.junit.Before;
import org.junit.Test;

import gr.uoi.cs.pythia.client.Patterns;
import gr.uoi.cs.pythia.engine.DatasetProfiler;
import gr.uoi.cs.pythia.engine.IDatasetProfilerFactory;
import gr.uoi.cs.pythia.patterns.algos.DominancePatternAlgo;
import gr.uoi.cs.pythia.patterns.algos.IPatternAlgoFactory;
import gr.uoi.cs.pythia.patterns.results.DominanceHighlight;
import gr.uoi.cs.pythia.patterns.results.HighlightPatternResult;

public class DominancePatternAlgoTests {

	private DatasetProfiler datasetProfiler;
	private DominancePatternAlgo dominancePatternAlgo;
	
	@Before
	public void init() throws AnalysisException {
		// TODO is this the best way to prepare for dominance tests?
		// I assumed we need to load the test dataset into a DatasetProfiler object
		datasetProfiler = (DatasetProfiler) new IDatasetProfilerFactory().createDatasetProfiler();
		dominancePatternAlgo = (DominancePatternAlgo) 
				new IPatternAlgoFactory().createPattern(PatternConstants.DOMINANCE);
		
		StructType schema = Patterns.createInternetUsageDatasetSchema();
        String alias = "internet_usage";
		String path = String.format(
			"src%stest%sresources%sinternet_usage_100.csv", 
			File.separator, File.separator, File.separator);
		
		datasetProfiler.registerDataset(alias, path, schema);
	}
	
	@Test
	// TODO when we decide how to determine measurements and coordinates
	// change this test accordingly
	public void testDetermineColumnName() {
		dominancePatternAlgo.determineColumnNames(datasetProfiler.getDatasetProfile());
		assertEquals("download", dominancePatternAlgo.getMeasurementColumnName());
		assertEquals("name", dominancePatternAlgo.getFirstCoordinateColumnName());
		assertEquals("session_break_reason", dominancePatternAlgo.getSecondCoordinateColumnName());
	}
	
	@Test
	public void testIdentifyDominanceWithOneCoordinate() {
		dominancePatternAlgo.determineColumnNames(datasetProfiler.getDatasetProfile());
		
		HighlightPatternResult expectedHighlights = createExpectedHighlightsForOneCoordinate();
		HighlightPatternResult highlights = 
				new HighlightPatternResult(
						"Title",
						dominancePatternAlgo.getMeasurementColumnName(), 
						dominancePatternAlgo.getFirstCoordinateColumnName());
		
		dominancePatternAlgo.identifyDominanceWithOneCoordinate(
				datasetProfiler.getDataset(), highlights);
		
		assertEquals(expectedHighlights.getTitle(), highlights.getTitle());
		assertEquals(expectedHighlights.getFirstCoordinateColumnName(), highlights.getFirstCoordinateColumnName());
		
		for (Row row : highlights.getQueryResult()) {
			int indexOfRow = highlights.getQueryResult().indexOf(row);
			Row expectedRow = expectedHighlights.getQueryResult().get(indexOfRow);
			for (int i=0; i<row.length(); i++) {
				assertEquals(expectedRow.get(i), row.get(i));
			}
		}
		
		for (DominanceHighlight highlight : highlights.getHighlights()) {
			int indexOfHighlight = highlights.getHighlights().indexOf(highlight);
			DominanceHighlight expectedHighlight = 
					expectedHighlights.getHighlights().get(indexOfHighlight);
			assertEquals(expectedHighlight.getType(), highlight.getType());
			assertEquals(expectedHighlight.getDominancePercentage(), highlight.getDominancePercentage(), 0.005);
			assertEquals(expectedHighlight.getFirstCoordinateValue(), highlight.getFirstCoordinateValue());
			assertEquals(expectedHighlight.getSecondCoordinateValue(), highlight.getSecondCoordinateValue());
			assertEquals(expectedHighlight.getMeasurementAggValue(), highlight.getMeasurementAggValue(), 0.005);
			assertEquals(expectedHighlight.getAggType(), highlight.getAggType());
			assertEquals(expectedHighlight.toString(), highlight.toString());
		}
	}
	
	@Test
	public void testIdentifyDominanceWithTwoCoordinates() {
		dominancePatternAlgo.determineColumnNames(datasetProfiler.getDatasetProfile());
		
		HighlightPatternResult expectedHighlights = createExpectedHighlightsForTwoCoordinates();
		HighlightPatternResult highlights = 
				new HighlightPatternResult(
						"Title",
						dominancePatternAlgo.getMeasurementColumnName(), 
						dominancePatternAlgo.getFirstCoordinateColumnName(),
						dominancePatternAlgo.getSecondCoordinateColumnName());
		
		dominancePatternAlgo.identifyDominanceWithTwoCoordinates(
				datasetProfiler.getDataset(), highlights);
		
		assertEquals(expectedHighlights.getTitle(), highlights.getTitle());
		assertEquals(expectedHighlights.getFirstCoordinateColumnName(), highlights.getFirstCoordinateColumnName());
		assertEquals(expectedHighlights.getSecondCoordinateColumnName(), highlights.getSecondCoordinateColumnName());
		
		for (Row row : highlights.getQueryResult()) {
			int indexOfRow = highlights.getQueryResult().indexOf(row);
			Row expectedRow = expectedHighlights.getQueryResult().get(indexOfRow);
			for (int i=0; i<row.length(); i++) {
				assertEquals(expectedRow.get(i), row.get(i));
			}
		}
		
		for (DominanceHighlight highlight : highlights.getHighlights()) {
			int indexOfHighlight = highlights.getHighlights().indexOf(highlight);
			DominanceHighlight expectedHighlight = 
					expectedHighlights.getHighlights().get(indexOfHighlight);
			assertEquals(expectedHighlight.getType(), highlight.getType());
			assertEquals(expectedHighlight.getDominancePercentage(), highlight.getDominancePercentage(), 0.0005);
			assertEquals(expectedHighlight.getFirstCoordinateValue(), highlight.getFirstCoordinateValue());
			assertEquals(expectedHighlight.getSecondCoordinateValue(), highlight.getSecondCoordinateValue());
			assertEquals(expectedHighlight.getMeasurementAggValue(), highlight.getMeasurementAggValue(), 0.0005);
			assertEquals(expectedHighlight.getAggType(), highlight.getAggType());
			assertEquals(expectedHighlight.toString(), highlight.toString());
		}
	}

	private HighlightPatternResult createExpectedHighlightsForOneCoordinate() {
		HighlightPatternResult expectedHighlights = new HighlightPatternResult(
				"Title",
				dominancePatternAlgo.getMeasurementColumnName(), 
				dominancePatternAlgo.getFirstCoordinateColumnName());
		
		expectedHighlights.setQueryResult(Arrays.asList(	
				RowFactory.create("user1",	67224.064),	
				RowFactory.create("user5",	578130.9439999999),	
				RowFactory.create("user3",	279430.42949999997),	
				RowFactory.create("user2",	526239.0114999998),	
				RowFactory.create("user4",	280919.552)
				));
		expectedHighlights.addHighlight(new DominanceHighlight(
				"total low", 100.0, "user1", null, 67224.064, "(avg)"));
		expectedHighlights.addHighlight(new DominanceHighlight(
				"total high", 100.0, "user5", null, 578130.9439999999, "(avg)"));
		expectedHighlights.addHighlight(new DominanceHighlight(
				"partial low", 75.0, "user3", null, 279430.42949999997, "(avg)"));
		expectedHighlights.addHighlight(new DominanceHighlight(
				"partial high", 75.0, "user2", null, 526239.0114999998, "(avg)"));
		return expectedHighlights;
	}
	
	private HighlightPatternResult createExpectedHighlightsForTwoCoordinates() {
		HighlightPatternResult expectedHighlights = new HighlightPatternResult(
				"Title",
				dominancePatternAlgo.getMeasurementColumnName(), 
				dominancePatternAlgo.getFirstCoordinateColumnName(),
				dominancePatternAlgo.getSecondCoordinateColumnName());
		
		expectedHighlights.setQueryResult(Arrays.asList(
				RowFactory.create("user4",	"User-Request", 7751.68),
				RowFactory.create("user2",	"Lost-Carrier", 383324.16),
				RowFactory.create("user1",	"Idle-Timeout", 70335.32631578947),
				RowFactory.create("user5",	"Idle-Timeout", 592516.5511111112	),
				RowFactory.create("user3",	"Idle-Timeout", 319128.67687499995),
				RowFactory.create("user5",	"Lost-Carrier", 66119.68),
				RowFactory.create("user2",	"Idle-Timeout", 552828.9105555554	),
				RowFactory.create("user3",	"Lost-Carrier", 120637.44),
				RowFactory.create("user5",	"User-Request", 831201.28),
				RowFactory.create("user1",	"Lost-Carrier", 8110.08	),
				RowFactory.create("user4",	"Idle-Timeout", 295296.8084210526	),
				RowFactory.create("user2",	"Lost-Service"	, 190535.68)
				));
		expectedHighlights.addHighlight(new DominanceHighlight(
				"total low", 100.0, "user4", "User-Request", 7751.68, "(avg)"));
		expectedHighlights.addHighlight(new DominanceHighlight(
				"partial high", 90.9090909090909, "user5", "Idle-Timeout", 592516.5511111112, "(avg)"));
		expectedHighlights.addHighlight(new DominanceHighlight(
				"partial low", 81.81818181818183, "user5", "Lost-Carrier", 66119.68, "(avg)"));
		expectedHighlights.addHighlight(new DominanceHighlight(
				"partial high", 81.81818181818183, "user2", "Idle-Timeout", 552828.9105555554, "(avg)"));
		expectedHighlights.addHighlight(new DominanceHighlight(
				"total high", 100.0, "user5", "User-Request", 831201.28, "(avg)"));
		expectedHighlights.addHighlight(new DominanceHighlight(
				"partial low", 90.9090909090909, "user1", "Lost-Carrier", 8110.08, "(avg)"));
		return expectedHighlights;
	}
	
}
