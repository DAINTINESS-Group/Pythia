package gr.uoi.cs.pythia.patterns.dominance;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class HighDominanceAlgo extends DominanceAlgo {

	private static final String HIGH_DOMINANCE = "high_dominance";
	
	public HighDominanceAlgo(Dataset<Row> dataset) {
		super(dataset);
	}
	
	@Override
	public String getPatternName() {
		return HIGH_DOMINANCE;
	}
	@Override
	protected String getDominanceType() {
		return DominanceConstants.HIGH;
	}
	@Override
	protected boolean isDominant(double valueA, double valueB) {
		return valueA > valueB;
	}

}
