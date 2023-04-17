package gr.uoi.cs.pythia.patterns.outlier;

public class OutlierResult {
	private OutlierType outlierType;
	private String columnName;
	private Double value;	
	private Double score;
	private int position;
	
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
		String str = "\n\n-----------------------------------------------" + 
				"-----------------------------------------------------\n\n" +
				"- Column: " + columnName + "\n" +
				"- Outlier value: " + value + "\n" + 
				formatScoreString() + 
				"- Position in the column: " + position + "\n";
		return str;
	}
	
	private String formatScoreString() {
		if (outlierType == OutlierType.Z_SCORE) {
			return "- Z score: " + score + "\n";
		}
		return "";
	}
	
}
