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
	
	private RegressionProfile regressionProfile;
	private IRegressionPerformer regressionPerformer;
	
	@Before
	public void init() {
		regressionProfile = new RegressionProfile();
		regressionPerformer = new RegressionPerformerFactory().createRegressionPerformer(
				new RegressionParameters(Arrays.asList("year"), "price", RegressionType.LINEAR));
	}

	@Test
	public void testPerformRegression() {
		Dataset<Row> dataset = AllRegressionTests.regressionResource.getDataset();
		DatasetProfile datasetProfile = AllRegressionTests.regressionResource.getDatasetProfile();
		DecimalFormat decimalFormat = new DecimalFormat("#.###", new DecimalFormatSymbols(Locale.ENGLISH));
		regressionPerformer.performRegression(dataset, datasetProfile);
		
		
		String independentVariableName = datasetProfile.getRegressionProfile().getIndependentVariablesNames().get(0);
		String dependentVariableName = datasetProfile.getRegressionProfile().getDependentVariableName();
		RegressionType regressionType = datasetProfile.getRegressionProfile().getType();
		double slope = Double.parseDouble(decimalFormat.format(datasetProfile.getRegressionProfile().getSlopes().get(0)));
		double intercept = Double.parseDouble(decimalFormat.format(datasetProfile.getRegressionProfile().getIntercept()));
		List<Double> independentVariablesValues = datasetProfile.getRegressionProfile().getIndependentVariablesValues().get(0);
		List<Double> dependentVariablesValues = datasetProfile.getRegressionProfile().getDependentVariableValues();
		
		//check if RegressionProfile is updated correctly
		assertEquals("year", independentVariableName);
		assertEquals("price", dependentVariableName);
		assertEquals(RegressionType.LINEAR, regressionType);
		assertEquals(-36795.875, slope, 0.000001);
		assertEquals(7.4341387186E7, intercept, 0.000001);
		assertEquals(this.getColumnValues(dataset, "year"), independentVariablesValues);
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
