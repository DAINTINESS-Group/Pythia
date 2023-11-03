package gr.uoi.cs.pythia.patterns.outlier;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.DataTypes;

//import org.apache.commons.math3.special.Erf;

import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.outlier.OutlierResult;
import gr.uoi.cs.pythia.model.outlier.OutlierType;

public class NormalizedScoreOutlierAlgo extends OutlierAlgo implements IOutlierAlgo {
	
	private static final String NORMALIZED_SCORE = "Normalized_Score";
	private static final double NORMALIZED_SCORE_THRESHOLD = 1.0;
	
	@Override
	public String getOutlierType() {
		return NORMALIZED_SCORE;
	}
	
	@Override
	public List<OutlierResult> identifyOutliers(Dataset<Row> dataset,
			DatasetProfile datasetProfile){
		
		List<OutlierResult> results = new ArrayList<OutlierResult>();
		
		// Debug print
		//System.out.println("-------------NORMALIZED--------------------");
		
		for (Column column : datasetProfile.getColumns()) {
			if (isNotNumericColumn(column)) continue;
			Double mean = getColumnMean(column);
			Double standardDeviation = getColumnStandardDeviation(column);
			if (standardDeviation == 0.0) continue;
			List<Double> values = getColumnValues(dataset, column);
			
			List<Double> zScores = getColumnZScores(values, mean, standardDeviation);			
			Double currentMaxZScore = Collections.max(zScores);
			Double currentMinZScore = Collections.min(zScores);
			
			for (int index = 0; index < values.size(); index++) {
				Double value = values.get(index);
				Double zScore = (value - mean) / standardDeviation;
				Double normalizedZScore = (zScore - currentMinZScore)/(currentMaxZScore - currentMinZScore);
				
				if (Math.abs(normalizedZScore) >= NORMALIZED_SCORE_THRESHOLD) {
					results.add(new OutlierResult(
							OutlierType.NORMALIZED_SCORE, column.getName(), value, normalizedZScore, index+1));
				}
			}
		}
		return results;
		
		// Debug print
//		System.out.println(results);
	}
	
	private List<Double> getColumnZScores(List<Double> values, Double mean, Double standardDeviation){
		List<Double> zScores = new ArrayList<Double>();
		
		for (int index = 0; index < values.size(); index++) {
			Double value = values.get(index);
			Double zScore = (value - mean) / standardDeviation;
			zScores.add(zScore);
		}
		
		return zScores;
	}
	
}
