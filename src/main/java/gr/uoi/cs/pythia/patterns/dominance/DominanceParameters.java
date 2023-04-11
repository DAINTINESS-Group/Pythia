package gr.uoi.cs.pythia.patterns.dominance;

public class DominanceParameters {
	
	private DominanceColumnSelectionMode dominanceColumnSelectionMode;
	private String[] measurementColumns;
	private String[] coordinateColumns;

	public DominanceParameters(
			DominanceColumnSelectionMode dominanceColumnSelectionMode,
			String[] measurementColumns, 
			String[] coordinateColumns) {
		this.dominanceColumnSelectionMode = dominanceColumnSelectionMode;
		this.measurementColumns = measurementColumns;
		this.coordinateColumns = coordinateColumns;
	}

	public DominanceParameters() {}

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

}
