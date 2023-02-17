package gr.uoi.cs.pythia.patterns;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import gr.uoi.cs.pythia.testshelpers.TestsUtilities;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.types.StructType;
import org.junit.Before;
import org.junit.Test;

import gr.uoi.cs.pythia.client.Patterns;
import gr.uoi.cs.pythia.engine.IDatasetProfiler;
import gr.uoi.cs.pythia.engine.IDatasetProfilerFactory;
import gr.uoi.cs.pythia.model.DatasetProfile;

public class ColumnSelectorTests {

	private  DatasetProfile datasetProfile;
	
	@Before
	public void init() throws AnalysisException, IOException {
		// TODO: move this elsewhere?
		TestsUtilities.setupResultsDir("patterns");
		// Register the cars dataset (100 records version)
		StructType schema = Patterns.createCarsDatasetSchema();
		String path = String.format(
				"src%stest%sresources%sdatasets%scars_100.csv", 
				File.separator, File.separator, File.separator, File.separator);
		IDatasetProfiler datasetProfiler = new IDatasetProfilerFactory().createDatasetProfiler();
	    datasetProfiler.registerDataset("cars", path, schema);
		datasetProfile = datasetProfiler.computeProfileOfDataset(
				TestsUtilities.getResultsDir("patterns"));
	}
	
	@Test
	public void testSelectColumnsWithExhaustiveMode() {
		ColumnSelector columnSelector = new ColumnSelector(
				ColumnSelectionMode.EXHAUSTIVE, 
				new String[] {}, new String[] {});
		
		List<String> expectedMeasureCols = Arrays.asList(
				new String[] {"price", "mileage", "tax", "mpg", "engineSize"});
		List<String> expectedCoordCols = Arrays.asList(
				new String[] {"manufacturer", "model", "year", "transmission", "fuelType"});
		
		List<String> actualMeasureCols = columnSelector.selectMeasurementColumns(datasetProfile);
		List<String> actualCoordCols = columnSelector.selectCoordinateColumns(datasetProfile);
		
		assertArrayEquals(expectedMeasureCols.toArray(), actualMeasureCols.toArray());
		assertArrayEquals(expectedCoordCols.toArray(), actualCoordCols.toArray());
	}
	
	@Test
	public void testSelectMeasureColsWithSmartMode() {
		// TODO write this when smart mode gets implemented
		assertEquals(true, true);
	}
	
	@Test
	public void testSelectColumnsWithUserSpecifiedOnlyMode() {
		ColumnSelector columnSelector = new ColumnSelector(
				ColumnSelectionMode.USER_SPECIFIED_ONLY, 
				new String[] {"mileage", "mpg", "price"}, 
				new String[] {"manufacturer", "model", "year"});
		
		List<String> expectedMeasureCols = Arrays.asList(
				new String[] {"mileage", "mpg", "price"});
		List<String> expectedCoordCols = Arrays.asList(
				new String[] {"manufacturer", "model", "year"});
		
		List<String> actualMeasureCols = columnSelector.selectMeasurementColumns(datasetProfile);
		List<String> actualCoordCols = columnSelector.selectCoordinateColumns(datasetProfile);
		
		assertArrayEquals(expectedMeasureCols.toArray(), actualMeasureCols.toArray());
		assertArrayEquals(expectedCoordCols.toArray(), actualCoordCols.toArray());
	}
	
	@Test
	public void testSelectColumnsWithUserSpecifiedOnlyModeAndNoColumnsSpecified() {
		ColumnSelector columnSelector = new ColumnSelector(
				ColumnSelectionMode.USER_SPECIFIED_ONLY, 
				new String[] {}, new String[] {});
		
		List<String> expectedMeasureCols = Arrays.asList(new String[] {});
		List<String> expectedCoordCols = Arrays.asList(new String[] {});
		
		List<String> actualMeasureCols = columnSelector.selectMeasurementColumns(datasetProfile);
		List<String> actualCoordCols = columnSelector.selectCoordinateColumns(datasetProfile);
		
		assertArrayEquals(expectedMeasureCols.toArray(), actualMeasureCols.toArray());
		assertArrayEquals(expectedCoordCols.toArray(), actualCoordCols.toArray());
	}
	
	@Test
	public void testSelectColumnsWithExhaustiveModeAndPartialUserInput() {
		ColumnSelector columnSelector = new ColumnSelector(
				ColumnSelectionMode.EXHAUSTIVE, 
				new String[] {"mileage", "mpg"}, 
				new String[] {"model"});
		
		List<String> expectedMeasureCols = Arrays.asList(
				new String[] {"mileage", "mpg", "price", "tax", "engineSize"});
		List<String> expectedCoordCols = Arrays.asList(
				new String[] {"model", "manufacturer", "year", "transmission", "fuelType"});
		
		List<String> actualMeasureCols = columnSelector.selectMeasurementColumns(datasetProfile);
		List<String> actualCoordCols = columnSelector.selectCoordinateColumns(datasetProfile);
		
		assertArrayEquals(expectedMeasureCols.toArray(), actualMeasureCols.toArray());
		assertArrayEquals(expectedCoordCols.toArray(), actualCoordCols.toArray());
	}
	
	@Test
	public void testSelectColumnsWithInvalidColumnDataTypes() {
		ColumnSelector columnSelector = new ColumnSelector(
				ColumnSelectionMode.USER_SPECIFIED_ONLY, 
				new String[] {"manufacturer", "mpg", "price"}, 
				new String[] {"mileage", "model", "year"});
		
		assertThrows(IllegalArgumentException.class, () -> {
			columnSelector.selectMeasurementColumns(datasetProfile);
		});
		assertThrows(IllegalArgumentException.class, () -> {
			columnSelector.selectCoordinateColumns(datasetProfile);
		});
	}
	
	@Test
	public void testSelectColumnsWithInvalidColumnNames() {
		ColumnSelector columnSelector = new ColumnSelector(
				ColumnSelectionMode.USER_SPECIFIED_ONLY, 
				new String[] {"manufacturer", "mpgG", "price"}, 
				new String[] {"mileage2", "model", "year"});
		
		assertThrows(IllegalArgumentException.class, () -> {
			columnSelector.selectMeasurementColumns(datasetProfile);
		});
		assertThrows(IllegalArgumentException.class, () -> {
			columnSelector.selectCoordinateColumns(datasetProfile);
		});
	}
	
	@Test
	public void testSelectColumnsWithNullColumnParams() {
		ColumnSelector columnSelector = new ColumnSelector(
				ColumnSelectionMode.EXHAUSTIVE, 
				null, null);
		
		List<String> expectedMeasureCols = Arrays.asList(
				new String[] {"price", "mileage", "tax", "mpg", "engineSize"});
		List<String> expectedCoordCols = Arrays.asList(
				new String[] {"manufacturer", "model", "year", "transmission", "fuelType"});
		
		List<String> actualMeasureCols = columnSelector.selectMeasurementColumns(datasetProfile);
		List<String> actualCoordCols = columnSelector.selectCoordinateColumns(datasetProfile);
		
		assertArrayEquals(expectedMeasureCols.toArray(), actualMeasureCols.toArray());
		assertArrayEquals(expectedCoordCols.toArray(), actualCoordCols.toArray());
	}
	
	@Test
	public void testSelectMeasureColsWithAllNullParams() {
		// TODO write this when smart mode gets implemented
		// as smart mode is the default mode.
		ColumnSelector columnSelector = new ColumnSelector(
				null, null, null);
		
		assertEquals(true, true);
	}
	
}
