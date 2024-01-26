package gr.uoi.cs.pythia.regression;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.RegressionProfile;
import gr.uoi.cs.pythia.model.regression.RegressionType;

public class LinearRegressionPerformer extends GeneralRegression implements IRegressionPerformer {

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
		
		for (int i = 0; i < dependentVariableValues.size(); i++) {
		    double independentValue = independentVariableValues.get(i);
		    double dependentValue = dependentVariableValues.get(i);

		    if (Double.isNaN(independentValue) || Double.isNaN(dependentValue)) {
		        continue;
		    } else {
		        regression.addData(independentValue, dependentValue);
		    }
		}

		
		//perform regression
		intercept = regression.getIntercept();
		slope = regression.getSlope();
		List<Double> correlations = getCorrelations(datasetProfile, dependentVariable, Arrays.asList(independentVariable));
		List<Double> pValues = calculatePValues(correlations, dependentVariableValues.size());
		Double error = regression.getMeanSquareError();
		
		//System.out.println("correlation = " + correlations.get(0));
		//System.out.println("pvalue = " + pValues.get(0));
		//System.out.println("error = " + error);

		// Save output to RegressionProfile
		this.setupRegressionProfile(Arrays.asList(independentVariable), Arrays.asList(independentVariableValues),
				dependentVariable, dependentVariableValues, RegressionType.LINEAR,
				Arrays.asList(slope), intercept, correlations, pValues, error);
		
		//DEBUG print
	    System.out.println(datasetProfile.getRegressionProfile());
	}
	
}