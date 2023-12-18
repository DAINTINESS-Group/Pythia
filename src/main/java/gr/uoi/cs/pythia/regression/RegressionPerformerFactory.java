package gr.uoi.cs.pythia.regression;

import java.util.List;

public class RegressionPerformerFactory {
	
	public IRegressionPerformer createRegressionPerformer(RegressionParameters regressionParameters) {
		switch (regressionParameters.getType()) {
	      case LINEAR:
				return new LinearRegressionPerformer(regressionParameters.getDependentVariable(),
						regressionParameters.getIndependentVariables().get(0));
	      case MULTIPLE_LINEAR:
	    	  	return new MultipleLinearRegressionPerformer(regressionParameters.getDependentVariable(),
	    	  			regressionParameters.getIndependentVariables());
		}
		throw new IllegalArgumentException(
		        String.format("Regression %s is not a supported regression type.", regressionParameters.getType()));
	}

}
