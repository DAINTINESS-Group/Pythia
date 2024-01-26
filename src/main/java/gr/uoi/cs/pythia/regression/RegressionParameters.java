package gr.uoi.cs.pythia.regression;

import java.util.List;

import gr.uoi.cs.pythia.model.regression.RegressionType;

public class RegressionParameters {
	
	private String dependentVariable;
	private List<String> independentVariables;
	private RegressionType type;
	private Double precision;
	
	public RegressionParameters(List<String> independentVariables, String dependentVariable, RegressionType type, Double precision) {
		this.dependentVariable = dependentVariable;
		this.independentVariables = independentVariables;
		this.type = type;
		this.precision = precision;
	}
	
	public String getDependentVariable() {
		return dependentVariable;
	}
	
	public List<String> getIndependentVariables() {
		return independentVariables;
	}
	
	public void setDependentVariable(String dependentVariable) {
		this.dependentVariable = dependentVariable;
	}
	
	public void setIndependentVariable(List<String> independentVariables) {
		this.independentVariables = independentVariables;
	}

	public RegressionType getType() {
		return type;
	}

	public void setType(RegressionType type) {
		this.type = type;
	}

	public double getPrecision() {
		return precision;
	}

	public void setPrecision(double precision) {
		this.precision = precision;
	}

}
