package gr.uoi.cs.pythia.patterns.algos;

import gr.uoi.cs.pythia.patterns.PatternConstants;
import gr.uoi.cs.pythia.patterns.algos.dominance.HighDominancePatternAlgo;
import gr.uoi.cs.pythia.patterns.algos.dominance.LowDominancePatternAlgo;

public class IPatternAlgoFactory {

	public IPatternAlgo createPattern(String type) {
		  switch (type) {
		  	case PatternConstants.HIGH_DOMINANCE:
		  		return new HighDominancePatternAlgo();
	  		case PatternConstants.LOW_DOMINANCE:
		  		return new LowDominancePatternAlgo();
	  		case PatternConstants.DISTRIBUTION:
	  			return new DistributionPatternAlgo();
		    }
		    throw new IllegalArgumentException(
		        String.format("Pattern %s is not a valid pattern type.", type));
		  }
}
