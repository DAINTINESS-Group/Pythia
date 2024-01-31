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

import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.RegressionProfile;
import gr.uoi.cs.pythia.model.regression.RegressionType;

public class MultipleLinearRegressionPerformerTests {

	private IRegressionPerformer regressionPerformer;
	
	@Before
	public void init() {
		DatasetProfile datasetProfile = AllRegressionTests.regressionResource.getDatasetProfile();
		regressionPerformer = new RegressionPerformerFactory().createRegressionPerformer(
				new RegressionParameters(Arrays.asList("tax", "mileage"), "price", RegressionType.MULTIPLE_LINEAR, null), datasetProfile);
	}
	
	@Test
	public void testPerformRegression() {
		Dataset<Row> dataset = AllRegressionTests.regressionResource.getDataset();
		RegressionProfile result = regressionPerformer.performRegression(dataset);		
		
		List<String> independentVariableNames = new ArrayList<String>();
		for(Column column : result.getIndependentVariables()) independentVariableNames.add(column.getName());
		String dependentVariableName = result.getDependentVariable().getName();
		RegressionType regressionType = result.getType();
		List<Double> actualSlopes = result.getSlopes();
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
		//assertEquals(expectedSlopes, actualSlopes);
		assertEquals(expectedSlopes.get(0), actualSlopes.get(0), 10E-4);
		assertEquals(expectedSlopes.get(1), actualSlopes.get(1), 10E-4);
		assertEquals(-128401.44652839263, intercept, 0.000001);
		assertEquals(expectedIndependentVariableValues, independentVariablesValues);
		assertEquals(this.getColumnValues(dataset, "price"), dependentVariablesValues);
		//assertEquals(expectedCorrelations, correlations);
		assertEquals(expectedCorrelations.get(0), correlations.get(0), 10E-4);
		assertEquals(expectedCorrelations.get(1), correlations.get(1), 10E-4);
		//assertEquals(expectedPValues, pValues);
		assertEquals(expectedPValues.get(0), pValues.get(0), 10E-2);
		assertEquals(expectedPValues.get(1), pValues.get(1), 10E-2);
		assertEquals(expectedError, error, 10E-4);		
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
