package gr.uoi.cs.pythia.outliers;

import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.outlier.OutlierResult;
import gr.uoi.cs.pythia.model.outlier.OutlierType;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.Before;

import java.util.Arrays;
import java.util.List;


public class ZScoreOutlierAlgoTests extends AbstractOutlierTest{

	private IOutlierAlgo zScoreOutlierAlgo;
	
	@Before
	public void init() {
		zScoreOutlierAlgo = new OutlierAlgoFactory().createOutlierAlgo(OutlierType.Z_SCORE, 3.0);
	}

	@Override
	protected void identifyOutliers(Dataset<Row> dataset, DatasetProfile datasetProfile) {
		zScoreOutlierAlgo.identifyOutliers(dataset, datasetProfile);
		System.out.println("OutliersType: " + OutlierType.Z_SCORE.toString()+"\n");
	}

	@Override
	protected List<OutlierResult> createExpectedOutlierResults() {
		return createExpectedZScoreOutlierResults();
	}

	private List<OutlierResult> createExpectedZScoreOutlierResults() {
		return Arrays.asList(
				new OutlierResult( 97440.0, 3.1075171338770025, 67),
				new OutlierResult( 3.0, 3.0317626369169393, 23),
				new OutlierResult( 3.0, 3.0317626369169393, 24),
				new OutlierResult( 3.0, 3.0317626369169393, 50),
				new OutlierResult( 3.0, 3.0317626369169393, 93)
				);
	}
}
