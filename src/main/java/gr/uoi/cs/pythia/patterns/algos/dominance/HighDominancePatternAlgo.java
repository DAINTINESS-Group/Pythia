package gr.uoi.cs.pythia.patterns.algos.dominance;

import gr.uoi.cs.pythia.patterns.PatternConstants;

public class HighDominancePatternAlgo extends DominancePatternAlgo {

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
