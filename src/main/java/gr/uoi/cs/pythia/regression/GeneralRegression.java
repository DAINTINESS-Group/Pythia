package gr.uoi.cs.pythia.regression;

import java.util.ArrayList;
import java.util.List;
//import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.RegressionProfile;
import gr.uoi.cs.pythia.model.regression.RegressionType;

public abstract class GeneralRegression implements IRegressionPerformer{
	
	protected DatasetProfile datasetProfile;
	
	public GeneralRegression(DatasetProfile datasetProfile) {
		this.datasetProfile = datasetProfile;
	}

	@Override
	public abstract RegressionType getRegressionType();

	@Override
	public abstract RegressionProfile performRegression(Dataset<Row> dataset) ;

	
	//this method saves the information of the output of the regression
	protected RegressionProfile setupRegressionProfile(List<String> independentVariablesNames,
            List<List<Double>> independentVariablesValues,
            String dependentVariableName,
            List<Double> dependentVariableValues,
            RegressionType regressionType,
            List<Double> slopes,
            Double intercept,
            List<Double> correlations,
            List<Double> pValues,
            Double error) {
		
			RegressionProfile newRegression = new RegressionProfile();
			
			List<Column> independentVariables = new ArrayList<Column>();
			for(String columnName : independentVariablesNames)	independentVariables.add(datasetProfile.getColumn(columnName));
			
			newRegression.setIndependentVariables(independentVariables);
			newRegression.setIndependentVariablesValues(independentVariablesValues);		
			newRegression.setDependentVariable(datasetProfile.getColumn(dependentVariableName));
			newRegression.setDependentVariableValues(dependentVariableValues);
			newRegression.setType(regressionType);
			newRegression.setSlopes(slopes);
			newRegression.setIntercept(intercept);
			newRegression.setCorrelations(correlations);
			newRegression.setpValues(pValues);
			newRegression.setError(error);
			
			return newRegression;
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
	
	protected List<Double> calculatePValues(List<Double> correlations, int sampleSize) {
        List<Double> pValues = new ArrayList<>();

        int degreesOfFreedom = sampleSize - 2;

        for (Double correlation : correlations) {
        	double tStatistic = correlation / Math.sqrt((1 - correlation * correlation) / (sampleSize - 2));
            double pValue = 2 * (1.0 - getTDistribution(degreesOfFreedom).cumulativeProbability(Math.abs(tStatistic)));
            pValues.add(pValue);
        }

        return pValues;
    }
	
	protected List<Double> getCorrelations(String dependentName, List<String> independentNames){
		List<Double> correlations = new ArrayList<>();
		for(String independentName : independentNames) {
			correlations.add(getCorrelation(datasetProfile, dependentName, independentName));
		}
		return correlations;
	}
	
	private Double getCorrelation(DatasetProfile datasetProfile, String dependent, String independent) {
		return datasetProfile.getColumn(dependent).getCorrelationsProfile().getAllCorrelations().get(independent);
	}

    private static TDistribution getTDistribution(int degreesOfFreedom) {
        return new TDistribution(degreesOfFreedom);
    }
	
}
