package gr.uoi.cs.pythia.patterns.dominance;

// TODO is this ok for input paramaters?
// TODO maybe apply effective java builder pattern
public class DominanceAnalysisParameters {
	
	// Default column selection mode is SMART.
	private DominanceColumnSelectionMode dominanceColumnSelectionMode = DominanceColumnSelectionMode.SMART;
	//private OutlierType outlierType = OutlierType.Z_SCORE;
	private String[] measurementColumns;
	private String[] coordinateColumns;
	private String outputFolderPath;

	public DominanceAnalysisParameters(
			DominanceColumnSelectionMode dominanceColumnSelectionMode,
			String[] measurementColumns, 
			String[] coordinateColumns,
			String outputFolderPath) {
		this.dominanceColumnSelectionMode = dominanceColumnSelectionMode;
		this.measurementColumns = measurementColumns;
		this.coordinateColumns = coordinateColumns;
		this.outputFolderPath = outputFolderPath;
	}

	public DominanceAnalysisParameters() {}

	public DominanceColumnSelectionMode getColumnSelectionMode() {
		return dominanceColumnSelectionMode;
	}

	public void setColumnSelectionMode(DominanceColumnSelectionMode dominanceColumnSelectionMode) {
		this.dominanceColumnSelectionMode = dominanceColumnSelectionMode;
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

//	public OutlierType getOutlierType() {
//		return outlierType;
//	}
//
//	public void setOutlierType(OutlierType outlierType) {
//		this.outlierType = outlierType;
//	}

}
