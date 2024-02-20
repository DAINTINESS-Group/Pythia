package gr.uoi.cs.pythia.regression;

import gr.uoi.cs.pythia.model.DatasetProfile;

public class RegressionPerformerFactory {
	
	public IRegressionPerformer createRegressionPerformer(RegressionParameters regressionParameters, DatasetProfile datasetProfile) {
		switch (regressionParameters.getType()) {
	      case LINEAR:
				return new LinearRegressionPerformer(regressionParameters.getDependentVariable(),
						regressionParameters.getIndependentVariables().get(0), datasetProfile);
	      case MULTIPLE_LINEAR:
	    	  	return new MultipleLinearRegressionPerformer(regressionParameters.getDependentVariable(),
	    	  			regressionParameters.getIndependentVariables(), datasetProfile);
	      case POLYNOMIAL:
	    	  	return new PolynomialRegressionPerformer(regressionParameters.getDependentVariable(),
	    	  			regressionParameters.getIndependentVariables().get(0), (int)regressionParameters.getPrecision(), datasetProfile);
	      case AUTOMATED: 
	    	  	return new AutomatedRegressionPerformer(regressionParameters.getDependentVariable(),
	    	  			regressionParameters.getPrecision(), datasetProfile);
		}
		throw new IllegalArgumentException(
		        String.format("Regression %s is not a supported regression type.", regressionParameters.getType()));
	}

}
