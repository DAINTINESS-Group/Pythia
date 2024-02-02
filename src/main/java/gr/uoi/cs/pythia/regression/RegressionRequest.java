package gr.uoi.cs.pythia.regression;

import java.util.ArrayList;
import java.util.List;

public class RegressionRequest {
	
	private List<RegressionParameters> regressionParameters;

	public RegressionRequest() {
		super();
		this.regressionParameters = new ArrayList<RegressionParameters>();
	}
	
	public void addRegression(RegressionParameters parameters) {
		regressionParameters.add(parameters);
	}

	public List<RegressionParameters> getRegressionParameters() {
		return regressionParameters;
	}

	public void setRegressionParameters(List<RegressionParameters> regressionParameters) {
		this.regressionParameters = regressionParameters;
	}

}
