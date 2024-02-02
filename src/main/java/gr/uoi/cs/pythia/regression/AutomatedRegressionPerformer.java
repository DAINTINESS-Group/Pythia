package gr.uoi.cs.pythia.regression;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.inference.TestUtils;

import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.RegressionProfile;
import gr.uoi.cs.pythia.model.regression.RegressionType;

public class AutomatedRegressionPerformer extends GeneralRegression  {
	
	private String dependentVariable;
	private List<String> independentVariables;
	private double dependenceDegree;
	
	public AutomatedRegressionPerformer(String dependentVariable, double dependenceDegree, DatasetProfile datasetProfile) {
		super(datasetProfile);
		this.dependentVariable = dependentVariable;
		this.dependenceDegree = dependenceDegree;
		this.independentVariables = new ArrayList<>();
	}
	
	@Override
	public RegressionType getRegressionType() {
		return RegressionType.AUTOMATED;
	}
	
	@Override
	public RegressionProfile performRegression(Dataset<Row> dataset) {
		//setup all independent candidates and their correlations
		List<String> independentCandidatesNames = new ArrayList<>();
		List<Double> independentCandidatesCorValues = new ArrayList<>();
		for(Map.Entry<String, Double> entry : datasetProfile.getColumn(dependentVariable)
				.getCorrelationsProfile().getAllCorrelations().entrySet()) {
			independentCandidatesNames.add(entry.getKey());
			independentCandidatesCorValues.add(entry.getValue());
		}
		
		//calculate pValue for each candidate
		List<Double> dependentVariableValues = getColumnValues(dataset, dependentVariable);
		List<Double> independentCandidatesPvalues = calculatePValues(independentCandidatesCorValues, dependentVariableValues.size());
		
		for(int i=0; i<independentCandidatesNames.size(); i++) {
			if(independentCandidatesPvalues.get(i) < dependenceDegree) {
				independentVariables.add(independentCandidatesNames.get(i));
			}
		}
		RegressionProfile result = new MultipleLinearRegressionPerformer(dependentVariable, independentVariables, datasetProfile)
			.performRegression(dataset);
		
		result.setType(RegressionType.AUTOMATED);
		return result;
	}
	
}
