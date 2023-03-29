package gr.uoi.cs.pythia.patterns.algos.dominance;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import gr.uoi.cs.pythia.patterns.algos.PatternConstants;

public class HighDominanceAlgo extends DominanceAlgo {

	public HighDominanceAlgo(Dataset<Row> dataset) {
		super(dataset);
	}
	
	@Override
	public String getPatternName() {
		return PatternConstants.HIGH_DOMINANCE;
	}
	@Override
	protected String getDominanceType() {
		return PatternConstants.HIGH;
	}
	@Override
	protected boolean isDominant(double valueA, double valueB) {
		return valueA > valueB;
	}

}
