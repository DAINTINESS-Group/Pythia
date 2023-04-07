package gr.uoi.cs.pythia.patterns.dominance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.spark.sql.types.DataTypes;

import gr.uoi.cs.pythia.config.AnalysisParameters;
import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.patterns.ColumnSelectionMode;

// TODO maybe add interface-factory for the different selection modes.
public class DominanceColumnSelector {

	// Default column selection mode is SMART.
	private ColumnSelectionMode columnSelectionMode = ColumnSelectionMode.SMART;
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
	
	public DominanceColumnSelector(AnalysisParameters analysisParameters) {
		this.measurementColumns = new ArrayList<String>();
		this.coordinateColumns = new ArrayList<String>();
		
		if (analysisParameters.getColumnSelectionMode() != null) {
			this.columnSelectionMode = analysisParameters.getColumnSelectionMode();			
		}
		if (analysisParameters.getMeasurementColumns() != null) {
			this.measurementColumns.addAll(Arrays.asList(
					analysisParameters.getMeasurementColumns()));
		}
		if (analysisParameters.getCoordinateColumns() != null) {
			this.coordinateColumns.addAll(Arrays.asList(
					analysisParameters.getCoordinateColumns()));
		}
	}

	public List<String> selectMeasurementColumns(DatasetProfile datasetProfile) {
		if (columnSelectionMode.equals(ColumnSelectionMode.EXHAUSTIVE)) {
			selectAllCandidateMeasurementColumns(datasetProfile);
		}
		else if (columnSelectionMode.equals(ColumnSelectionMode.SMART)) {
			selectInterestingMeasurementColumns(datasetProfile);
		}
		validateMeasurementColumns(datasetProfile);
		return measurementColumns;
	}
	
	public List<String> selectCoordinateColumns(DatasetProfile datasetProfile) {
		if (columnSelectionMode.equals(ColumnSelectionMode.EXHAUSTIVE)) {
			selectAllCandidateCoordinateColumns(datasetProfile);
		}
		else if (columnSelectionMode.equals(ColumnSelectionMode.SMART)) {
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
