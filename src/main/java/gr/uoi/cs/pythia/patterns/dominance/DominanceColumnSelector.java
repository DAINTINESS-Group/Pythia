package gr.uoi.cs.pythia.patterns.dominance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.spark.sql.types.DataTypes;

import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;

// TODO maybe add interface-factory for the different selection modes.
public class DominanceColumnSelector {

	// Default column selection mode is SMART.
	private DominanceColumnSelectionMode dominanceColumnSelectionMode = DominanceColumnSelectionMode.SMART;
	private List<String> measurementColumns;
	private List<String> coordinateColumns;
	
	// Valid data types for measurment columns
	private final String[] measurementDataTypes = {
			DataTypes.DoubleType.toString(),
			DataTypes.IntegerType.toString()
	};
	
	// Valid data types for coordinate columns
	private final String[] coordinateDataTypes = {
			DataTypes.StringType.toString(),
			DataTypes.DateType.toString()
	};
	
	public DominanceColumnSelector(DominanceAnalysisParameters dominanceAnalysisParameters) {
		this.measurementColumns = new ArrayList<String>();
		this.coordinateColumns = new ArrayList<String>();
		
		if (dominanceAnalysisParameters.getColumnSelectionMode() != null) {
			this.dominanceColumnSelectionMode = dominanceAnalysisParameters.getColumnSelectionMode();			
		}
		if (dominanceAnalysisParameters.getMeasurementColumns() != null) {
			this.measurementColumns.addAll(Arrays.asList(
					dominanceAnalysisParameters.getMeasurementColumns()));
		}
		if (dominanceAnalysisParameters.getCoordinateColumns() != null) {
			this.coordinateColumns.addAll(Arrays.asList(
					dominanceAnalysisParameters.getCoordinateColumns()));
		}
	}

	public List<String> selectMeasurementColumns(DatasetProfile datasetProfile) {
		if (dominanceColumnSelectionMode.equals(DominanceColumnSelectionMode.EXHAUSTIVE)) {
			selectAllCandidateMeasurementColumns(datasetProfile);
		}
		else if (dominanceColumnSelectionMode.equals(DominanceColumnSelectionMode.SMART)) {
			selectInterestingMeasurementColumns(datasetProfile);
		}
		validateMeasurementColumns(datasetProfile);
		return measurementColumns;
	}
	
	public List<String> selectCoordinateColumns(DatasetProfile datasetProfile) {
		if (dominanceColumnSelectionMode.equals(DominanceColumnSelectionMode.EXHAUSTIVE)) {
			selectAllCandidateCoordinateColumns(datasetProfile);
		}
		else if (dominanceColumnSelectionMode.equals(DominanceColumnSelectionMode.SMART)) {
			selectInterestingCoordinateColumns(datasetProfile);
		}
		validateCoordinateColumns(datasetProfile);
		return coordinateColumns;
	}
	
	private void selectAllCandidateMeasurementColumns(DatasetProfile datasetProfile) {
		for (Column column : datasetProfile.getColumns()) {
			if (measurementColumns.contains(column.getName())) continue;
			if (isValidDataType(column.getDatatype(), measurementDataTypes)) {
				measurementColumns.add(column.getName());
			}
		}
	}
	
	private void selectAllCandidateCoordinateColumns(DatasetProfile datasetProfile) {
		for (Column column : datasetProfile.getColumns()) {
			if (coordinateColumns.contains(column.getName())) continue;
			if (isValidDataType(column.getDatatype(), coordinateDataTypes)) {
				coordinateColumns.add(column.getName());
			}
		}
	}

	private void selectInterestingMeasurementColumns(DatasetProfile datasetProfile) {
		// TODO figure out an algorithm to select measurement columns
		// most likely by utlizing the DescriptiveStatisticsProfile and/or CorrelationsProfile
		// of each Column		
	}
	
	private void selectInterestingCoordinateColumns(DatasetProfile datasetProfile) {
		// TODO figure out an algorithm to select coordinate columns
		// most likely by utlizing the DescriptiveStatisticsProfile and/or CorrelationsProfile
		// of each Column		
	}
	
	private void validateMeasurementColumns(DatasetProfile datasetProfile) {
		for (String colName : measurementColumns) {
			Column column = findColumnByName(datasetProfile, colName);
			validateDataType(column, measurementDataTypes);
		}
	}
	
	private void validateCoordinateColumns(DatasetProfile datasetProfile) {
		for (String colName : coordinateColumns) {
			Column column = findColumnByName(datasetProfile, colName);
			validateDataType(column, coordinateDataTypes);
		}
	}

	private Column findColumnByName(DatasetProfile datasetProfile, String colName) {
		for (Column column : datasetProfile.getColumns()) {
			if (column.getName().equals(colName)) return column;
		}
		throw new IllegalArgumentException(String.format(
				"Column \"%s\" is not a column of the registered dataset file" + 
				"with alias \"%s\" at \"%s\".", 
				colName, datasetProfile.getAlias(), datasetProfile.getPath()));
	}
	
	private void validateDataType(Column column, String[] validDataTypes) {
		if (isValidDataType(column.getDatatype(), validDataTypes)) return;
		throw new IllegalArgumentException(String.format(
				"Column \"%s\" has an invalid data type of \"%s\".\n" +
				"Valid data types are: %s", 
				column.getName(), 
				column.getDatatype(), 
				Arrays.asList(validDataTypes)));
	}
	
	private boolean isValidDataType(String dataType, String[] validDataTypes) {
		for (String validDataType : validDataTypes) {
			if (validDataType.equals(dataType)) return true;
		}
		return false;
	}
	
}
