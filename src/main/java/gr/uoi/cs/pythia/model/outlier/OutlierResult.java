package gr.uoi.cs.pythia.model.outlier;

public class OutlierResult {
	private final Double value;
	private final Double score;
	private final int position;
	
	public OutlierResult(Double value, Double score, int position) {
		this.value = value;
		this.score = score;
		this.position = position;
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
		String result = String.format("%-24s%-24s%-24s\n", value, score, position);
		return result;
	}
	
}
