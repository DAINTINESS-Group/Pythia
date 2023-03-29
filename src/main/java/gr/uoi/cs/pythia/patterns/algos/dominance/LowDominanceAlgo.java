package gr.uoi.cs.pythia.patterns.algos.dominance;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import gr.uoi.cs.pythia.patterns.algos.PatternConstants;

public class LowDominanceAlgo extends DominanceAlgo {

	public LowDominanceAlgo(Dataset<Row> dataset) {
		super(dataset);
	}
	
	@Override
	public String getPatternName() {
		return PatternConstants.LOW_DOMINANCE;
	}
	@Override
	protected String getDominanceType() {
		return PatternConstants.LOW;
	}
	@Override
	protected boolean isDominant(double valueA, double valueB) {
		return valueA < valueB;
	}

}
