package gr.uoi.cs.pythia.patterns.algos.dominance;

import gr.uoi.cs.pythia.patterns.PatternConstants;

public class LowDominancePatternAlgo extends DominancePatternAlgo {

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
