package gr.uoi.cs.pythia.patterns;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.Before;
import org.junit.Test;

import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.outlier.OutlierResult;
import gr.uoi.cs.pythia.model.outlier.OutlierType;
import gr.uoi.cs.pythia.patterns.outlier.IOutlierAlgo;
import gr.uoi.cs.pythia.patterns.outlier.OutlierAlgoFactory;

public class NormalizedScoreOutlierAlgoTests {

	private IOutlierAlgo normalizedScoreOutlierAlgo;
	
	@Before
	public void init() {
		normalizedScoreOutlierAlgo = new OutlierAlgoFactory().createOutlierAlgo(OutlierType.NORMALIZED_SCORE, 1.0);
	}
	
	@Test
	public void testIdentifyOutliers() {
		Dataset<Row> dataset = AllPatternTests.patternsResource.getDataset();
		DatasetProfile datasetProfile = AllPatternTests.patternsResource.getDatasetProfile();
		List<OutlierResult> expected =  createExpectedZScoreOutlierResults();
		List<OutlierResult> actual = normalizedScoreOutlierAlgo.identifyOutliers(dataset, datasetProfile);
		
		for (int i=0; i<expected.size(); i++) {
			System.out.println(expected.get(i));
		}
		System.out.println("----------");
		for (int i=0; i<actual.size(); i++) {
			System.out.println(actual.get(i));
		}
		
		for (int i=0; i<actual.size(); i++) {
			assertEquals(expected.get(i).getColumnName(), actual.get(i).getColumnName());
			assertEquals(expected.get(i).getValue(), actual.get(i).getValue());
			assertEquals(expected.get(i).getScore(), actual.get(i).getScore());
			assertEquals(expected.get(i).getPosition(), actual.get(i).getPosition());
		}
	}

	private List<OutlierResult> createExpectedZScoreOutlierResults() {
		return Arrays.asList(
				new OutlierResult(OutlierType.NORMALIZED_SCORE, "price", 928000.0, 1.0, 103),
				new OutlierResult(OutlierType.NORMALIZED_SCORE, "mileage", 97440.0, 1.0, 67),
				new OutlierResult(OutlierType.NORMALIZED_SCORE, "tax", 205.0, 1.0, 33),
				new OutlierResult(OutlierType.NORMALIZED_SCORE, "tax", 205.0, 1.0, 55),
				new OutlierResult(OutlierType.NORMALIZED_SCORE, "mpg", 83.1, 1.0, 59),
				new OutlierResult(OutlierType.NORMALIZED_SCORE, "engineSize", 3.0, 1.0, 23),
				new OutlierResult(OutlierType.NORMALIZED_SCORE, "engineSize", 3.0, 1.0, 24),
				new OutlierResult(OutlierType.NORMALIZED_SCORE, "engineSize", 3.0, 1.0, 50),
				new OutlierResult(OutlierType.NORMALIZED_SCORE, "engineSize", 3.0, 1.0, 93)
				);
	}

}
