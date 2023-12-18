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
	
	private RegressionProfile regressionProfile;
	private IRegressionPerformer regressionPerformer;
	
	@Before
	public void init() {
		regressionProfile = new RegressionProfile();
		regressionPerformer = new RegressionPerformerFactory().createRegressionPerformer(
				new RegressionParameters(Arrays.asList("year", "tax", "mileage"), "price", RegressionType.MULTIPLE_LINEAR));
	}
	
	@Test
	public void testPerformRegression() {
		Dataset<Row> dataset = AllRegressionTests.regressionResource.getDataset();
		DatasetProfile datasetProfile = AllRegressionTests.regressionResource.getDatasetProfile();
		DecimalFormat decimalFormat = new DecimalFormat("#.###");
		regressionPerformer.performRegression(dataset, datasetProfile);
		
		
		List<String> independentVariableNames = datasetProfile.getRegressionProfile().getIndependentVariablesNames();
		String dependentVariableName = datasetProfile.getRegressionProfile().getDependentVariableName();
		RegressionType regressionType = datasetProfile.getRegressionProfile().getType();
		List<Double> slopes = datasetProfile.getRegressionProfile().getSlopes();
		double intercept = datasetProfile.getRegressionProfile().getIntercept();
		List<List<Double>> independentVariablesValues = datasetProfile.getRegressionProfile().getIndependentVariablesValues();
		List<Double> dependentVariablesValues = datasetProfile.getRegressionProfile().getDependentVariableValues();
		
		
		List<String> expectedIndependentVariableNames = Arrays.asList("year", "tax", "mileage");
		List<Double> expectedSlopes = Arrays.asList(-69138.58425259145, 2165.5852049481555, -0.3803118575022645);
		List<List<Double>> expectedIndependentVariableValues = new ArrayList<>();
		for(String var : expectedIndependentVariableNames)
			expectedIndependentVariableValues.add(getColumnValues(dataset, var));
		
		//check if RegressionProfile is updated correctly
		assertEquals(expectedIndependentVariableNames, independentVariableNames);
		assertEquals("price", dependentVariableName);
		assertEquals(RegressionType.MULTIPLE_LINEAR, regressionType);
		assertEquals(expectedSlopes, slopes);
		assertEquals(1.3934254823305547E8, intercept, 0.000001);
		assertEquals(expectedIndependentVariableValues, independentVariablesValues);
		assertEquals(this.getColumnValues(dataset, "price"), dependentVariablesValues);
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
