package gr.uoi.cs.pythia.patterns.algos;

import gr.uoi.cs.pythia.patterns.PatternConstants;

public class IPatternAlgoFactory {
	
	  public IPatternAlgo createPattern(String type) {
		  switch (type) {
		  	case PatternConstants.DOMINANCE:
		  		return new DominancePatternAlgo();
	  		case PatternConstants.DISTRIBUTION:
	  			return new DistributionPatternAlgo();
		    }
		    throw new IllegalArgumentException(
		        String.format("Pattern %s is not a valid pattern type.", type));
		  }
}
