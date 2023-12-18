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
	
	@Override
	public String toString() {
		String independentPart = "";
		for(int i=0; i<independentVariablesNames.size();i++) {
			independentPart += " " + slopes.get(i) + "*" + independentVariablesNames.get(i) + " +";
		}
		return dependentVariableName + " =" + independentPart + " " + intercept;
	}

}
