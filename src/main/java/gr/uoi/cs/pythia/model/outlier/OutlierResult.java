package gr.uoi.cs.pythia.model.outlier;

public class OutlierResult {
	private final OutlierType outlierType;
	private final String columnName;
	private final Double value;
	private final Double score;
	private final int position;
	
	public OutlierResult(OutlierType outlierType, String columnName, 
			Double value, Double score, int position) {
		this.outlierType = outlierType;
		this.columnName = columnName;
		this.value = value;
		this.score = score;
		this.position = position;
	}

	public String getColumnName() {
		return columnName;
	}

	public Double getValue() {
		return value;
	}

	public Double getScore() {
		return score;
	}

	public int getPosition() {
		return position;
	}

	@Override
	public String toString() {
		return String.format("%-24s%-24s%-24s\n",
						value, score, position);
	}
	
}
