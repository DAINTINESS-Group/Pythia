package gr.uoi.cs.pythia.regression;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.RegressionProfile;
import gr.uoi.cs.pythia.model.regression.RegressionType;

public class GeneralRegression {
	
	//this method saves the information of the output of the regression
	protected void setupRegressionProfile(List<String> independentVariablesNames,
			List<List<Double>> independentVariablesValues,
			String dependentVariableName,
			List<Double> dependentVariablesValues,
			RegressionType regressionTYpe,
			List<Double> slopes,
			Double intercept,
			List<Double> correlations,
			List<Double> pValues,
			Double error) {
		RegressionProfile.setIndependentVariablesNames(independentVariablesNames);
	    RegressionProfile.setIndependentVariablesValues(independentVariablesValues);
	    RegressionProfile.setDependentVariableName(dependentVariableName);
	    RegressionProfile.setDependentVariableValues(dependentVariablesValues);
	    RegressionProfile.setType(regressionTYpe);
	    RegressionProfile.setSlopes(slopes);
	    RegressionProfile.setIntercept(intercept);
	    RegressionProfile.setCorrelations(correlations);
		RegressionProfile.setpValues(pValues);
		RegressionProfile.setError(error);
	}
	
	protected List<Double> getColumnValues(Dataset<Row> dataset, String columnName) {
		return dataset
				.select(columnName)
				.collectAsList()
				.stream()
				.map(s -> parseColumnValue(s.get(0)))
				.collect(Collectors.toList());
	}
	
	protected Double parseColumnValue(Object object) {
		if (object == null) return Double.NaN;
		return Double.parseDouble(object.toString());
	}
	
	protected static List<Double> calculatePValues(List<Double> correlations, int sampleSize) {
        List<Double> pValues = new ArrayList<>();

        int degreesOfFreedom = sampleSize - 2;

        for (Double correlation : correlations) {
        	double tStatistic = correlation / Math.sqrt((1 - correlation * correlation) / (sampleSize - 2));
            double pValue = 2 * (1.0 - getTDistribution(degreesOfFreedom).cumulativeProbability(Math.abs(tStatistic)));
            pValues.add(pValue);
        }

        return pValues;
    }
	
	protected static List<Double> getCorrelations(DatasetProfile datasetProfile, String dependentName, List<String> independentNames){
		List<Double> correlations = new ArrayList<>();
		for(String independentName : independentNames) {
			correlations.add(getCorrelation(datasetProfile, dependentName, independentName));
		}
		return correlations;
	}
	
	private static Double getCorrelation(DatasetProfile datasetProfile, String dependent, String independent) {
		return datasetProfile.getColumn(dependent).getCorrelationsProfile().getAllCorrelations().get(independent);
	}

    private static TDistribution getTDistribution(int degreesOfFreedom) {
        return new TDistribution(degreesOfFreedom);
    }
	
}
