package gr.uoi.cs.pythia.outliers;

import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.OutlierProfile;
import gr.uoi.cs.pythia.model.outlier.OutlierResult;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import java.util.ArrayList;
import java.util.List;

import static gr.uoi.cs.pythia.util.DatatypeFilterer.isNumerical;

public class ZScoreOutlierAlgo extends OutlierAlgo implements IOutlierAlgo {

	private static final String Z_SCORE_TEXT = "Z_Score";
	private  double Z_SCORE_THRESHOLD;

	public ZScoreOutlierAlgo(double z_SCORE_THRESHOLD) {
		super();
		this.Z_SCORE_THRESHOLD = z_SCORE_THRESHOLD;
	}
	
	@Override
	public String getOutlierType() {
		return Z_SCORE_TEXT;
	}
	
	@Override
	public void identifyOutliers(Dataset<Row> dataset, DatasetProfile datasetProfile) {
		for (Column column : datasetProfile.getColumns()) {
			if (!isNumerical(column.getDatatype())) continue;
			Double mean = getColumnMean(column);
			Double standardDeviation = getColumnStandardDeviation(column);
			if (standardDeviation == 0.0) continue;
			List<Double> values = getColumnValues(dataset, column);

			List<OutlierResult> results = new ArrayList<OutlierResult>();

			for (int index = 0; index < values.size(); index++) {
				Double value = values.get(index);
				Double zScore = (value - mean) / standardDeviation;
				
				if (Math.abs(zScore) >= Z_SCORE_THRESHOLD) {
					results.add(new OutlierResult(value, zScore, index+1));
				}
			}
			//TODO Extract Method ??
			//createOutlierResult(results,Z_SCORE_TEXT);
			OutlierProfile outlierProfile = new OutlierProfile(results,Z_SCORE_TEXT);
			column.setOutlierProfile(outlierProfile);

		}
	}

}
