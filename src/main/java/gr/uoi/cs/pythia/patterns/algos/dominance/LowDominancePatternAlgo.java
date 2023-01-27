package gr.uoi.cs.pythia.patterns.algos.dominance;

import gr.uoi.cs.pythia.patterns.PatternConstants;

public class LowDominancePatternAlgo extends DominancePatternAlgo {

	@Override
	public String getPatternName() {
		return PatternConstants.LOW_DOMINANCE;
	}
	
	protected String getDominanceType() {
		return PatternConstants.LOW;
	}

	protected boolean isDominance(double valueA, double valueB) {
		return valueA < valueB;
	}

}
