package gr.uoi.cs.pythia.patterns;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.Before;
import org.junit.Test;

import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.patterns.dominance.DominanceColumnSelectionMode;
import gr.uoi.cs.pythia.patterns.dominance.DominanceColumnSelector;
import gr.uoi.cs.pythia.patterns.dominance.DominanceParameters;

public class DominanceColumnSelectorTests {
	
	private DatasetProfile datasetProfile;
	private Dataset<Row> dataset;
	
	@Before
	public void init() throws AnalysisException {
		datasetProfile = AllPatternTests.patternsResource.getDatasetProfile();
		dataset = AllPatternTests.patternsResource.getDataset();
	}
	
	@Test
	public void testSelectColumnsWithExhaustiveMode() {
		DominanceColumnSelector columnSelector = new DominanceColumnSelector(
				new DominanceParameters(
						DominanceColumnSelectionMode.EXHAUSTIVE, 
						new String[] {}, new String[] {})
				);
		
		List<String> expectedMeasureCols = Arrays.asList(
				new String[] {"price", "mileage", "tax", "mpg", "engineSize"});
		List<String> expectedCoordCols = Arrays.asList(
				new String[] {"manufacturer", "model", "year", "transmission", "fuelType"});
		
		List<String> actualMeasureCols = columnSelector.selectMeasurementColumns(datasetProfile);
		List<String> actualCoordCols = columnSelector.selectCoordinateColumns(datasetProfile, dataset);
		
		assertArrayEquals(expectedMeasureCols.toArray(), actualMeasureCols.toArray());
		assertArrayEquals(expectedCoordCols.toArray(), actualCoordCols.toArray());
	}
	
	@Test
	public void testSelectColumnsWithSmartMode() {
		DominanceColumnSelector columnSelector = new DominanceColumnSelector(
				new DominanceParameters(
						DominanceColumnSelectionMode.SMART, 
						new String[] {}, new String[] {})
				);
		
		List<String> expectedMeasureCols = Arrays.asList(
				new String[] {"price", "mileage", "mpg", "engineSize"});
		List<String> expectedCoordCols = Arrays.asList(
				new String[] {"model", "year", "transmission", "fuelType"});
		
		List<String> actualMeasureCols = columnSelector.selectMeasurementColumns(datasetProfile);
		List<String> actualCoordCols = columnSelector.selectCoordinateColumns(datasetProfile, dataset);
		
		assertArrayEquals(expectedMeasureCols.toArray(), actualMeasureCols.toArray());
		assertArrayEquals(expectedCoordCols.toArray(), actualCoordCols.toArray());
	}
	
	@Test
	public void testSelectColumnsWithUserSpecifiedOnlyMode() {
		DominanceColumnSelector columnSelector = new DominanceColumnSelector(
				new DominanceParameters(
						DominanceColumnSelectionMode.USER_SPECIFIED_ONLY, 
						new String[] {"mileage", "mpg", "price"}, 
						new String[] {"manufacturer", "model", "year"})
				);
		
		List<String> expectedMeasureCols = Arrays.asList(
				new String[] {"mileage", "mpg", "price"});
		List<String> expectedCoordCols = Arrays.asList(
				new String[] {"manufacturer", "model", "year"});
		
		List<String> actualMeasureCols = columnSelector.selectMeasurementColumns(datasetProfile);
		List<String> actualCoordCols = columnSelector.selectCoordinateColumns(datasetProfile, dataset);
		
		assertArrayEquals(expectedMeasureCols.toArray(), actualMeasureCols.toArray());
		assertArrayEquals(expectedCoordCols.toArray(), actualCoordCols.toArray());
	}
	
	@Test
	public void testSelectColumnsWithUserSpecifiedOnlyModeAndNoColumnsSpecified() {
		DominanceColumnSelector columnSelector = new DominanceColumnSelector(
				new DominanceParameters(
						DominanceColumnSelectionMode.USER_SPECIFIED_ONLY, 
						new String[] {}, new String[] {})
				);
		
		List<String> expectedMeasureCols = Arrays.asList(new String[] {});
		List<String> expectedCoordCols = Arrays.asList(new String[] {});
		
		List<String> actualMeasureCols = columnSelector.selectMeasurementColumns(datasetProfile);
		List<String> actualCoordCols = columnSelector.selectCoordinateColumns(datasetProfile, dataset);
		
		assertArrayEquals(expectedMeasureCols.toArray(), actualMeasureCols.toArray());
		assertArrayEquals(expectedCoordCols.toArray(), actualCoordCols.toArray());
	}
	
	@Test
	public void testSelectColumnsWithExhaustiveModeAndPartialUserInput() {
		DominanceColumnSelector columnSelector = new DominanceColumnSelector(
				new DominanceParameters(
						DominanceColumnSelectionMode.EXHAUSTIVE, 
						new String[] {"mileage", "mpg"}, 
						new String[] {"model"})
				);
		
		List<String> expectedMeasureCols = Arrays.asList(
				new String[] {"mileage", "mpg", "price", "tax", "engineSize"});
		List<String> expectedCoordCols = Arrays.asList(
				new String[] {"model", "manufacturer", "year", "transmission", "fuelType"});
		
		List<String> actualMeasureCols = columnSelector.selectMeasurementColumns(datasetProfile);
		List<String> actualCoordCols = columnSelector.selectCoordinateColumns(datasetProfile, dataset);
		
		assertArrayEquals(expectedMeasureCols.toArray(), actualMeasureCols.toArray());
		assertArrayEquals(expectedCoordCols.toArray(), actualCoordCols.toArray());
	}
	
	@Test
	public void testSelectColumnsWithInvalidColumnDataTypes() {
		DominanceColumnSelector columnSelector = new DominanceColumnSelector(
				new DominanceParameters(
						DominanceColumnSelectionMode.USER_SPECIFIED_ONLY, 
						new String[] {"manufacturer", "mpg", "price"}, 
						new String[] {"mileage", "model", "year"})
				);
		
		assertThrows(IllegalArgumentException.class, () -> {
			columnSelector.selectMeasurementColumns(datasetProfile);
		});
		assertThrows(IllegalArgumentException.class, () -> {
			columnSelector.selectCoordinateColumns(datasetProfile, dataset);
		});
	}
	
	@Test
	public void testSelectColumnsWithInvalidColumnNames() {
		DominanceColumnSelector columnSelector = new DominanceColumnSelector(
				new DominanceParameters(
						DominanceColumnSelectionMode.USER_SPECIFIED_ONLY, 
						new String[] {"manufacturer", "mpgG", "price"}, 
						new String[] {"mileage2", "model", "year"})
				);
		
		assertThrows(IllegalArgumentException.class, () -> {
			columnSelector.selectMeasurementColumns(datasetProfile);
		});
		assertThrows(IllegalArgumentException.class, () -> {
			columnSelector.selectCoordinateColumns(datasetProfile, dataset);
		});
	}
	
	@Test
	public void testSelectColumnsWithNullColumnParams() {
		DominanceColumnSelector columnSelector = new DominanceColumnSelector(
				new DominanceParameters(DominanceColumnSelectionMode.EXHAUSTIVE, null, null));
		
		List<String> expectedMeasureCols = Arrays.asList(
				new String[] {"price", "mileage", "tax", "mpg", "engineSize"});
		List<String> expectedCoordCols = Arrays.asList(
				new String[] {"manufacturer", "model", "year", "transmission", "fuelType"});
		
		List<String> actualMeasureCols = columnSelector.selectMeasurementColumns(datasetProfile);
		List<String> actualCoordCols = columnSelector.selectCoordinateColumns(datasetProfile, dataset);
		
		assertArrayEquals(expectedMeasureCols.toArray(), actualMeasureCols.toArray());
		assertArrayEquals(expectedCoordCols.toArray(), actualCoordCols.toArray());
	}
	
	@Test
	public void testSelectColumnsWithAllNullParams() {
		DominanceColumnSelector columnSelector = new DominanceColumnSelector(
				new DominanceParameters(null, null, null));
		
		List<String> expectedMeasureCols = Arrays.asList(
				new String[] {"price", "mileage", "mpg", "engineSize"});
		List<String> expectedCoordCols = Arrays.asList(
				new String[] {"model", "year", "transmission", "fuelType"});
		
		List<String> actualMeasureCols = columnSelector.selectMeasurementColumns(datasetProfile);
		List<String> actualCoordCols = columnSelector.selectCoordinateColumns(datasetProfile, dataset);
		
		assertArrayEquals(expectedMeasureCols.toArray(), actualMeasureCols.toArray());
		assertArrayEquals(expectedCoordCols.toArray(), actualCoordCols.toArray());
	}
	
}
