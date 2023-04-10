package gr.uoi.cs.pythia.patterns;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.Before;
import org.junit.Test;

import gr.uoi.cs.pythia.config.AnalysisParameters;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.patterns.outlier.IOutlierAlgo;
import gr.uoi.cs.pythia.patterns.outlier.OutlierAlgoFactory;
import gr.uoi.cs.pythia.patterns.outlier.OutlierResult;
import gr.uoi.cs.pythia.patterns.outlier.OutlierType;

public class ZScoreOutlierAlgoTests {

	private IOutlierAlgo zScoreOutlierAlgo;
	
	// TODO not sure if we need this - to be discussed
	private AnalysisParameters analysisParameters;
	
	@Before
	public void init() {
		zScoreOutlierAlgo = new OutlierAlgoFactory().createOutlierAlgo(OutlierType.Z_SCORE);
	}
	
	@Test
	public void testIdentifyOutliers() {
		Dataset<Row> dataset = AllPatternTests.patternsResource.getDataset();
		DatasetProfile datasetProfile = AllPatternTests.patternsResource.getDatasetProfile();
		List<OutlierResult> expected =  createExpectedZScoreOutlierResults();
		zScoreOutlierAlgo.identifyOutliers(dataset, datasetProfile, analysisParameters);
		List<OutlierResult> actual = zScoreOutlierAlgo.getResults();
		
		for (int i=0; i<actual.size(); i++) {
			assertEquals(expected.get(i).getColumnName(), actual.get(i).getColumnName());
			assertEquals(expected.get(i).getValue(), actual.get(i).getValue());
			assertEquals(expected.get(i).getScore(), actual.get(i).getScore());
			assertEquals(expected.get(i).getPosition(), actual.get(i).getPosition());
		}
	}

	private List<OutlierResult> createExpectedZScoreOutlierResults() {
		return Arrays.asList(
				new OutlierResult("mileage", 97440.0, 3.1075171338770025, 67),
				new OutlierResult("engineSize", 3.0, 3.0317626369169393, 23),
				new OutlierResult("engineSize", 3.0, 3.0317626369169393, 24),
				new OutlierResult("engineSize", 3.0, 3.0317626369169393, 50),
				new OutlierResult("engineSize", 3.0, 3.0317626369169393, 93)
				);
	}
}
