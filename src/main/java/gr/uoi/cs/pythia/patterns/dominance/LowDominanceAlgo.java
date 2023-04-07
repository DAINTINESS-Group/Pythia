package gr.uoi.cs.pythia.patterns.dominance;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class LowDominanceAlgo extends DominanceAlgo {

	private static final String LOW_DOMINANCE = "low_dominance";
	
	public LowDominanceAlgo(Dataset<Row> dataset) {
		super(dataset);
	}
	
	@Override
	public String getPatternName() {
		return LOW_DOMINANCE;
	}
	@Override
	protected String getDominanceType() {
		return DominanceConstants.LOW;
	}
	@Override
	protected boolean isDominant(double valueA, double valueB) {
		return valueA < valueB;
	}

}
