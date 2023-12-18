package gr.uoi.cs.pythia.regression;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.regression.RegressionType;

public class LinearRegressionPerformer implements IRegressionPerformer {

	private String dependentVariable;
	private String independentVariable;
	private double intercept;
	private double slope;
	
	public LinearRegressionPerformer(String dependentVariable, String independentVariable) {
		super();
		this.dependentVariable = dependentVariable;
		this.independentVariable = independentVariable;
	}
	
	
	@Override
	public RegressionType getRegressionType() {
		return RegressionType.LINEAR;
	}
	
	@Override
	public void performRegression(Dataset<Row> dataset, DatasetProfile datasetProfile) {
		SimpleRegression regression = new SimpleRegression();
		List<Double> dependentVariableValues = getColumnValues(dataset, dependentVariable);
		List<Double> independentVariableValues = getColumnValues(dataset, independentVariable);
		
		for(int i = 0; i<dependentVariableValues.size(); i++) {
			regression.addData(independentVariableValues.get(i), dependentVariableValues.get(i));
		}
		
		//perform regression to calculate intercept and slope
		intercept = regression.getIntercept();
		slope = regression.getSlope();
		
		
		//save output
		datasetProfile.getRegressionProfile().setIndependentVariablesNames(Arrays.asList(independentVariable));
		datasetProfile.getRegressionProfile().addIndependentVariablesValues(independentVariableValues);
		datasetProfile.getRegressionProfile().setDependentVariableName(dependentVariable);
		datasetProfile.getRegressionProfile().setDependentVariableValues(dependentVariableValues);
		datasetProfile.getRegressionProfile().setType(RegressionType.LINEAR);
		datasetProfile.getRegressionProfile().setSlopes(Arrays.asList(slope));
		datasetProfile.getRegressionProfile().setIntercept(intercept);

		//DEBUG prints
		System.out.println(datasetProfile.getRegressionProfile());
	}
	
	private List<Double> getColumnValues(Dataset<Row> dataset, String columnName) {
		return dataset
				.select(columnName)
				.collectAsList()
				.stream()
				.map(s -> parseColumnValue(s.get(0)))
				.collect(Collectors.toList());
	}
	
	private Double parseColumnValue(Object object) {
		if (object == null) return Double.NaN;
		return Double.parseDouble(object.toString());
	}
	
}
