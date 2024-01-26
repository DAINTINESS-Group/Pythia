package gr.uoi.cs.pythia.model;

import java.util.ArrayList;
import java.util.List;

import gr.uoi.cs.pythia.model.regression.RegressionType;

public class RegressionProfile {
	
	private static List<String> independentVariablesNames;
	private static List<List<Double>> independentVariablesValues;
	
	private static String dependentVariableName;
	private static List<Double> dependentVariableValues;
	
	private static RegressionType type;
	
	private static List<Double> slopes;
	private static double intercept;
	
	private static List<Double> correlations;
	private static List<Double> pValues;
	
	private static Double error;
	
	
	public RegressionProfile() {
		independentVariablesNames = new ArrayList<>();
		independentVariablesValues = new ArrayList<>();
		dependentVariableValues = new ArrayList<>();
		slopes = new ArrayList<>();
	}

	public static List<String> getIndependentVariablesNames() {
		return independentVariablesNames;
	}
	
	public static List<List<Double>> getIndependentVariablesValues() {
		return independentVariablesValues;
	}
	
	public static String getDependentVariableName() {
		return dependentVariableName;
	}
	
	public static List<Double> getDependentVariableValues() {
		return dependentVariableValues;
	}
	
	public static RegressionType getType() {
		return type;
	}
	
	public static List<Double> getSlopes() {
		return slopes;
	}
	
	public static double getIntercept() {
		return intercept;
	}
	
	public static void setIndependentVariablesNames(List<String> independentVariablesNames) {
		RegressionProfile.independentVariablesNames = independentVariablesNames;
	}
	
	public static void addIndependentVariablesValues(List<Double> independentVariablesValues) {
		RegressionProfile.independentVariablesValues.add(independentVariablesValues);
	}
	
	public static void setDependentVariableName(String dependentVariableName) {
		RegressionProfile.dependentVariableName = dependentVariableName;
	}
	
	public static void setDependentVariableValues(List<Double> dependentVariableValues) {
		RegressionProfile.dependentVariableValues = dependentVariableValues;
	}
	
	public static void setType(RegressionType type) {
		RegressionProfile.type = type;
	}
	
	public static void setSlopes(List<Double> slopes) {
		RegressionProfile.slopes = slopes;
	}
	
	public static void setIntercept(double intercept) {
		RegressionProfile.intercept = intercept;
	}
	
	public static List<Double> getCorrelations() {
		return correlations;
	}

	public static List<Double> getpValues() {
		return pValues;
	}

	public static Double getError() {
		return error;
	}

	public static void setIndependentVariablesValues(List<List<Double>> independentVariablesValues) {
		RegressionProfile.independentVariablesValues = independentVariablesValues;
	}

	public static void setCorrelations(List<Double> correlations) {
		RegressionProfile.correlations = correlations;
	}

	public static void setpValues(List<Double> pValues) {
		RegressionProfile.pValues = pValues;
	}

	public static void setError(Double error) {
		RegressionProfile.error = error;
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
