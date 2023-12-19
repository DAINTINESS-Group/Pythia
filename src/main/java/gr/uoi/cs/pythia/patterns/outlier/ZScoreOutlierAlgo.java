package gr.uoi.cs.pythia.patterns.outlier;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;


import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.outlier.OutlierResult;
import gr.uoi.cs.pythia.model.outlier.OutlierType;

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
	public List<OutlierResult> identifyOutliers(
			Dataset<Row> dataset, 
			DatasetProfile datasetProfile) {
		List<OutlierResult> results = new ArrayList<OutlierResult>();
		
		// Debug print
		//System.out.println("------------------zSCORE--------------------");
		
		for (Column column : datasetProfile.getColumns()) {
			if (isNotNumericColumn(column)) continue;
			Double mean = getColumnMean(column);
			Double standardDeviation = getColumnStandardDeviation(column);
			if (standardDeviation == 0.0) continue;
			List<Double> values = getColumnValues(dataset, column);
			
			for (int index = 0; index < values.size(); index++) {
				Double value = values.get(index);
				Double zScore = (value - mean) / standardDeviation;
				
				if (Math.abs(zScore) >= Z_SCORE_THRESHOLD) {
					results.add(new OutlierResult(
							OutlierType.Z_SCORE, column.getName(), value, zScore, index+1));
				}
			}
		}
		
		return results;
		
		// Debug print
//		System.out.println(results);
	}

}
