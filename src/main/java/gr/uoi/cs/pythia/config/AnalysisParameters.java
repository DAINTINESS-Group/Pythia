package gr.uoi.cs.pythia.config;

import gr.uoi.cs.pythia.patterns.ColumnSelectionMode;
import gr.uoi.cs.pythia.patterns.outlier.OutlierType;

// TODO is this ok for input paramaters?
// TODO maybe apply effective java builder pattern
public class AnalysisParameters {
	
	// Default column selection mode is SMART.
	private ColumnSelectionMode columnSelectionMode = ColumnSelectionMode.SMART;
	private OutlierType outlierType = OutlierType.Z_SCORE;
	private String[] measurementColumns;
	private String[] coordinateColumns;
	private String outputFolderPath;

	public AnalysisParameters(
			ColumnSelectionMode columnSelectionMode,
			String[] measurementColumns, 
			String[] coordinateColumns,
			String outputFolderPath) {
		this.columnSelectionMode = columnSelectionMode;
		this.measurementColumns = measurementColumns;
		this.coordinateColumns = coordinateColumns;
		this.outputFolderPath = outputFolderPath;
	}

	public AnalysisParameters() {}

	public ColumnSelectionMode getColumnSelectionMode() {
		return columnSelectionMode;
	}

	public void setColumnSelectionMode(ColumnSelectionMode columnSelectionMode) {
		this.columnSelectionMode = columnSelectionMode;
	}

	public String[] getMeasurementColumns() {
		return measurementColumns;
	}

	public void setMeasurementColumns(String[] measurementColumns) {
		this.measurementColumns = measurementColumns;
	}

	public String[] getCoordinateColumns() {
		return coordinateColumns;
	}

	public void setCoordinateColumns(String[] coordinateColumns) {
		this.coordinateColumns = coordinateColumns;
	}

	public String getOutputFolderPath() {
		return outputFolderPath;
	}

	public void setOutputFolderPath(String outputFolderPath) {
		this.outputFolderPath = outputFolderPath;
	}

	public OutlierType getOutlierType() {
		return outlierType;
	}

	public void setOutlierType(OutlierType outlierType) {
		this.outlierType = outlierType;
	}

}
