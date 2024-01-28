package gr.uoi.cs.pythia.model;

import java.util.ArrayList;
import java.util.List;

import gr.uoi.cs.pythia.model.regression.RegressionType;

public class RegressionProfile {
	
	private List<String> independentVariablesNames;
	private List<List<Double>> independentVariablesValues;
	
	private String dependentVariableName;
	private List<Double> dependentVariableValues;
	
	private RegressionType type;
	
	private List<Double> slopes;
	private double intercept;
	
	private List<Double> correlations;
	private List<Double> pValues;
	
	private Double error;
		
	public RegressionProfile() {
		independentVariablesNames = new ArrayList<>();
		independentVariablesValues = new ArrayList<>();
		dependentVariableValues = new ArrayList<>();
		slopes = new ArrayList<>();
		correlations = new ArrayList<>();
		pValues = new ArrayList<>();
	}

	public List<String> getIndependentVariablesNames() {
		return independentVariablesNames;
	}


	public List<List<Double>> getIndependentVariablesValues() {
		return independentVariablesValues;
	}


	public String getDependentVariableName() {
		return dependentVariableName;
	}


	public List<Double> getDependentVariableValues() {
		return dependentVariableValues;
	}


	public RegressionType getType() {
		return type;
	}


	public List<Double> getSlopes() {
		return slopes;
	}


	public double getIntercept() {
		return intercept;
	}


	public List<Double> getCorrelations() {
		return correlations;
	}


	public List<Double> getpValues() {
		return pValues;
	}


	public Double getError() {
		return error;
	}


	public void setIndependentVariablesNames(List<String> independentVariablesNames) {
		this.independentVariablesNames = independentVariablesNames;
	}


	public void setIndependentVariablesValues(List<List<Double>> independentVariablesValues) {
		this.independentVariablesValues = independentVariablesValues;
	}


	public void setDependentVariableName(String dependentVariableName) {
		this.dependentVariableName = dependentVariableName;
	}


	public void setDependentVariableValues(List<Double> dependentVariableValues) {
		this.dependentVariableValues = dependentVariableValues;
	}


	public void setType(RegressionType type) {
		this.type = type;
	}


	public void setSlopes(List<Double> slopes) {
		this.slopes = slopes;
	}


	public void setIntercept(double intercept) {
		this.intercept = intercept;
	}


	public void setCorrelations(List<Double> correlations) {
		this.correlations = correlations;
	}


	public void setpValues(List<Double> pValues) {
		this.pValues = pValues;
	}


	public void setError(Double error) {
		this.error = error;
	}


	@Override
	public String toString() {
		String independentPart = "";
		if(type == RegressionType.LINEAR || type == RegressionType.MULTIPLE_LINEAR || type == RegressionType.AUTOMATED) {
			for(int i=0; i<independentVariablesNames.size();i++) {
				independentPart += " + " + slopes.get(i) + "*" + independentVariablesNames.get(i);
			}
		}
		else if(type == RegressionType.POLYNOMIAL) {
			for(int i=0; i<slopes.size();i++) {
				independentPart += " + " + slopes.get(i) + "*" + independentVariablesNames.get(0) + "^(" + (i+1) + ")";
			}
		}
		return dependentVariableName + " = " + intercept + independentPart;
	}

}
