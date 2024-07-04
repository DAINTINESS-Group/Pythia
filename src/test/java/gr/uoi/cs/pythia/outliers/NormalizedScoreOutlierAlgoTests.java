package gr.uoi.cs.pythia.outliers;

import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.outlier.OutlierResult;
import gr.uoi.cs.pythia.model.outlier.OutlierType;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.Before;

import java.util.Arrays;
import java.util.List;


public class NormalizedScoreOutlierAlgoTests extends AbstractOutlierTest{

	private IOutlierAlgo normalizedScoreOutlierAlgo;
	
	@Before
	public void init() {
		normalizedScoreOutlierAlgo = new OutlierAlgoFactory().createOutlierAlgo(OutlierType.NORMALIZED_SCORE, 1.0);
	}

	@Override
	protected void identifyOutliers(Dataset<Row> dataset, DatasetProfile datasetProfile) {
		normalizedScoreOutlierAlgo.identifyOutliers(dataset, datasetProfile);
		System.out.println("OutliersType: " + OutlierType.NORMALIZED_SCORE.toString()+"\n");
	}

	@Override
	protected List<OutlierResult> createExpectedOutlierResults() {
		return createExpectedNormalizedScoreOutlierResults();
	}

	private List<OutlierResult> createExpectedNormalizedScoreOutlierResults() {
		return Arrays.asList(
				new OutlierResult(  928000.0, 1.0, 103),
				new OutlierResult(  97440.0, 1.0, 67),
				new OutlierResult(  205.0, 1.0, 33),
				new OutlierResult(  205.0, 1.0, 55),
				new OutlierResult( 83.1, 1.0, 59),
				new OutlierResult(  3.0, 1.0, 23),
				new OutlierResult(  3.0, 1.0, 24),
				new OutlierResult(  3.0, 1.0, 50),
				new OutlierResult(  3.0, 1.0, 93)
				);
	}

}
