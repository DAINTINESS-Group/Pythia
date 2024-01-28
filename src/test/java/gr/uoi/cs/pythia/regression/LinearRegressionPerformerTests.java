package gr.uoi.cs.pythia.regression;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.text.DecimalFormat;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.Before;
import org.junit.Test;

import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.RegressionProfile;
import gr.uoi.cs.pythia.model.regression.RegressionType;

public class LinearRegressionPerformerTests {
	
	private IRegressionPerformer regressionPerformer;
	
	@Before
	public void init() {
		regressionPerformer = new RegressionPerformerFactory().createRegressionPerformer(
				new RegressionParameters(Arrays.asList("tax"), "price", RegressionType.LINEAR, null));
	}

	@Test
	public void testPerformRegression() {
		Dataset<Row> dataset = AllRegressionTests.regressionResource.getDataset();
		DatasetProfile datasetProfile = AllRegressionTests.regressionResource.getDatasetProfile();
		DecimalFormat decimalFormat = new DecimalFormat("#.###");
		RegressionProfile result = regressionPerformer.performRegression(dataset, datasetProfile);

		String independentVariableName = result.getIndependentVariablesNames().get(0);
		String dependentVariableName = result.getDependentVariableName();
		RegressionType regressionType = result.getType();
		double slope = Double.parseDouble(decimalFormat.format(result.getSlopes().get(0)));
		double intercept = Double.parseDouble(decimalFormat.format(result.getIntercept()));
		List<Double> independentVariablesValues = result.getIndependentVariablesValues().get(0);
		List<Double> dependentVariablesValues = result.getDependentVariableValues();
		List<Double> correlations = result.getCorrelations();
		List<Double> pValues = result.getpValues();
		Double error = result.getError();
		
		//check if RegressionProfile is updated correctly
		assertEquals("tax", independentVariableName);
		assertEquals("price", dependentVariableName);
		assertEquals(RegressionType.LINEAR, regressionType);
		assertEquals(1810.461, slope, 0.000001);
		assertEquals(-42512.683, intercept, 0.000001);
		assertEquals(this.getColumnValues(dataset, "tax"), independentVariablesValues);
		assertEquals(this.getColumnValues(dataset, "price"), dependentVariablesValues);
		assertEquals(Arrays.asList(0.38856998491405487), correlations);
		assertEquals(Arrays.asList(4.991871864756803E-5), pValues);
		assertEquals(7.618177535494662E10, error, 0.000001);
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
