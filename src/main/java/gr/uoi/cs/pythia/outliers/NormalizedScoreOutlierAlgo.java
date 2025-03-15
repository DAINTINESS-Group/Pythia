package gr.uoi.cs.pythia.outliers;

import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.OutlierProfile;
import gr.uoi.cs.pythia.model.outlier.OutlierResult;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static gr.uoi.cs.pythia.util.DatatypeFilterer.isNumerical;

public class NormalizedScoreOutlierAlgo extends OutlierAlgo implements IOutlierAlgo {

	private static final String NORMALIZED_SCORE_TEXT = "Normalized_Score";
	private final double NORMALIZED_SCORE_THRESHOLD;

	
	public NormalizedScoreOutlierAlgo(double NORMALIZED_SCORE_THRESHOLD) {
		super();
		this.NORMALIZED_SCORE_THRESHOLD = NORMALIZED_SCORE_THRESHOLD;
	}

	@Override
	public String getOutlierType() {
		return NORMALIZED_SCORE_TEXT;
	}
	
	@Override
	public void  identifyOutliers(Dataset<Row> dataset,DatasetProfile datasetProfile){
		
		for (Column column : datasetProfile.getColumns()) {
			if (!isNumerical(column.getDatatype())) continue;
			Double mean = getColumnMean(column);
			Double standardDeviation = getColumnStandardDeviation(column);
			if (standardDeviation == 0.0)		//outlierness is 0 for all, will never exceed the THRESHOLD
				continue;
			List<Double> values = getColumnValues(dataset, column);
			
			List<Double> zScores = getColumnZScores(values, mean, standardDeviation);

			Double currentMaxZScore = Collections.max(zScores);
			Double currentMinZScore = Collections.min(zScores);

			List<OutlierResult> results = new ArrayList<>();

			for (int index = 0; index < values.size(); index++) {
				Double value = values.get(index);
				Double zScore = (value - mean) / standardDeviation;
				double normalizedZScore = (zScore - currentMinZScore)/(currentMaxZScore - currentMinZScore);
				
				if (Math.abs(normalizedZScore) >= NORMALIZED_SCORE_THRESHOLD) {
					results.add(new OutlierResult(value, normalizedZScore, index+1));
				}
			}

			OutlierProfile outlierProfile = new OutlierProfile(results,NORMALIZED_SCORE_TEXT);
			column.setOutlierProfile(outlierProfile);
		}
	}
	
	private List<Double> getColumnZScores(List<Double> values, Double mean, Double standardDeviation){
		List<Double> zScores = new ArrayList<>();

        for (Double value : values) {
            Double zScore = (value - mean) / standardDeviation;
            zScores.add(zScore);
        }
		
		return zScores;
	}
	
}
