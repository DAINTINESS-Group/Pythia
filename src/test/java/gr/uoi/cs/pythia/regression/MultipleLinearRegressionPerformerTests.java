package gr.uoi.cs.pythia.regression;

import static org.junit.Assert.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.Before;
import org.junit.Test;

import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.RegressionProfile;
import gr.uoi.cs.pythia.model.regression.RegressionType;

public class MultipleLinearRegressionPerformerTests {

	private IRegressionPerformer regressionPerformer;
	
	@Before
	public void init() {
		regressionPerformer = new RegressionPerformerFactory().createRegressionPerformer(
				new RegressionParameters(Arrays.asList("tax", "mileage"), "price", RegressionType.MULTIPLE_LINEAR, null));
	}
	
	@Test
	public void testPerformRegression() {
		Dataset<Row> dataset = AllRegressionTests.regressionResource.getDataset();
		DatasetProfile datasetProfile = AllRegressionTests.regressionResource.getDatasetProfile();
		RegressionProfile result = regressionPerformer.performRegression(dataset, datasetProfile);
		
		
		List<String> independentVariableNames = result.getIndependentVariablesNames();
		String dependentVariableName = result.getDependentVariableName();
		RegressionType regressionType = result.getType();
		List<Double> slopes = result.getSlopes();
		double intercept = result.getIntercept();
		List<List<Double>> independentVariablesValues = result.getIndependentVariablesValues();
		List<Double> dependentVariablesValues = result.getDependentVariableValues();
		List<Double> correlations = result.getCorrelations();
		List<Double> pValues = result.getpValues();
		Double error = result.getError();
		
		
		List<String> expectedIndependentVariableNames = Arrays.asList("tax", "mileage");
		List<Double> expectedSlopes = Arrays.asList(1964.621239301185, 2.0948300062243033);
		List<List<Double>> expectedIndependentVariableValues = new ArrayList<>();
		for(String var : expectedIndependentVariableNames)
			expectedIndependentVariableValues.add(getColumnValues(dataset, var));
		List<Double> expectedCorrelations = Arrays.asList(0.38856998491405487, 0.048884109708816625);
		List<Double> expectedPValues = Arrays.asList(4.991871864756803E-5, 0.6238805188017977);
		Double expectedError = 7.294738058981244E10;
		
		//check if RegressionProfile is updated correctly
		assertEquals(expectedIndependentVariableNames, independentVariableNames);
		assertEquals("price", dependentVariableName);
		assertEquals(RegressionType.MULTIPLE_LINEAR, regressionType);
		assertEquals(expectedSlopes, slopes);
		assertEquals(-128401.44652839263, intercept, 0.000001);
		assertEquals(expectedIndependentVariableValues, independentVariablesValues);
		assertEquals(this.getColumnValues(dataset, "price"), dependentVariablesValues);
		assertEquals(expectedCorrelations, correlations);
		assertEquals(expectedPValues, pValues);
		assertEquals(expectedError, error, 0.000001);
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
