package gr.uoi.cs.pythia.regression;

import static org.junit.Assert.*;

import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
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
		DatasetProfile datasetProfile = AllRegressionTests.regressionResource.getDatasetProfile();
		regressionPerformer = new RegressionPerformerFactory().createRegressionPerformer(
				new RegressionParameters(Arrays.asList("tax"), "price", RegressionType.LINEAR, null), datasetProfile);
	}

	@Test
	public void testPerformRegression() {
		Dataset<Row> dataset = AllRegressionTests.regressionResource.getDataset();
		DecimalFormat decimalFormat = new DecimalFormat("#.###", new DecimalFormatSymbols(Locale.ENGLISH));
		RegressionProfile result = regressionPerformer.performRegression(dataset);

		String independentVariableName = result.getIndependentVariables().get(0).getName();
		String dependentVariableName = result.getDependentVariable().getName();
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
		assertEquals(0.38856998491405487, correlations.get(0), 10E-4);
		assertEquals(4.991871864756803E-5, pValues.get(0), 10E-4);
		assertEquals(7.618177535494662E10, error, 10E-4);
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
