package gr.uoi.cs.pythia.regression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.RegressionProfile;
import gr.uoi.cs.pythia.model.regression.RegressionType;

//import Jama.Matrix;
//import Jama.QRDecomposition;

public class PolynomialRegressionPerformer extends GeneralRegression {

	private String dependentVariable;
	private String independentVariable;
	private final int degree;
	private double intercept;
	private List<Double> slopes;
	
	
	public PolynomialRegressionPerformer(String dependentVariable, String independentVariable, int degree, DatasetProfile datasetProfile) {
		super(datasetProfile);
		this.dependentVariable = dependentVariable;
		this.independentVariable = independentVariable;
		this.degree = degree;
	}
	
	@Override
	public RegressionType getRegressionType() {
		return RegressionType.POLYNOMIAL;
	}
	
	@Override
	public RegressionProfile performRegression(Dataset<Row> dataset) {
	    // Get the values of the relevant columns
	    List<Double> dependentVariableValues = getColumnValues(dataset, dependentVariable);
	    List<Double> independentVariableValues = getColumnValues(dataset, independentVariable);

	    // Create a weighted points set for polynomial fitting
	    WeightedObservedPoints points = new WeightedObservedPoints();
	    for (int i = 0; i < dependentVariableValues.size(); i++) {
	    	double independentValue = independentVariableValues.get(i);
		    double dependentValue = dependentVariableValues.get(i);
		    if (Double.isNaN(independentValue) || Double.isNaN(dependentValue)) {
		        continue;
		    } else {
		    	points.add(independentVariableValues.get(i), dependentVariableValues.get(i));
		    }
	    }

	    // Fit a polynomial curve to the points
	    PolynomialCurveFitter fitter = PolynomialCurveFitter.create(degree);
	    double[] fitCoefficients = fitter.fit(points.toList());

	    // Extract coefficients
	    intercept = fitCoefficients[0];
	    slopes = new ArrayList<>();
	    for (int i = 1; i < fitCoefficients.length; i++) {
	        slopes.add(fitCoefficients[i]);
	    }

	    // Calculate the sum of squared errors
	    double sumOfSquaredResiduals = 0.0;
	    for (int i = 0; i < dependentVariableValues.size(); i++) {
	    	if (Double.isNaN(dependentVariableValues.get(i)) || Double.isNaN(independentVariableValues.get(i))) {
		        continue;
		    }
	        double predictedValue = evaluatePolynomial(fitCoefficients, independentVariableValues.get(i));
	        double residual = dependentVariableValues.get(i) - predictedValue;
	        sumOfSquaredResiduals += residual * residual;
	    }

	    // Calculate MSE
	    double meanSquaredError = sumOfSquaredResiduals / dependentVariableValues.size();

	    List<Double> correlations = getCorrelations(dependentVariable, Arrays.asList(independentVariable));
	    List<Double> pValues = calculatePValues(correlations, dependentVariableValues.size());

	    // Save output to RegressionProfile
	    RegressionProfile result = this.setupRegressionProfile(Arrays.asList(independentVariable), Arrays.asList(independentVariableValues),
	            dependentVariable, dependentVariableValues, RegressionType.POLYNOMIAL,
	            slopes, intercept, correlations, pValues, meanSquaredError);

	    datasetProfile.addRegressionProfile(result);
	    return result;
	}

	//Helper method
	private double evaluatePolynomial(double[] coefficients, double x) {
	    double result = 0.0;
	    for (int i = 0; i < coefficients.length; i++) {
	        result += coefficients[i] * Math.pow(x, i);
	    }
	    return result;
	}

}
