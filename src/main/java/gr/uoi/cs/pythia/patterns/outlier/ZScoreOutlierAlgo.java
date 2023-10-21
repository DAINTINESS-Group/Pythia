package gr.uoi.cs.pythia.patterns.outlier;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.DataTypes;

import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.outlier.OutlierResult;
import gr.uoi.cs.pythia.model.outlier.OutlierType;

public class ZScoreOutlierAlgo implements IOutlierAlgo {

	private static final String Z_SCORE = "Z_Score";
	private static final double Z_SCORE_THRESHOLD = 3.0;

	@Override
	public String getOutlierType() {
		return Z_SCORE;
	}
	
	@Override
	public List<OutlierResult> identifyOutliers(
			Dataset<Row> dataset, 
			DatasetProfile datasetProfile) {
		List<OutlierResult> results = new ArrayList<OutlierResult>();
		
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

	private boolean isNotNumericColumn(Column column) {
		return !(column.getDatatype() == DataTypes.DoubleType.toString() ||
				column.getDatatype() == DataTypes.IntegerType.toString());
	}

	private List<Double> getColumnValues(Dataset<Row> dataset, Column column) {
		return dataset
				.select(column.getName())
				.collectAsList()
				.stream()
				.map(s -> parseColumnValue(s.get(0)))
				.collect(Collectors.toList());
	}
	
	private Double parseColumnValue(Object object) {
		if (object == null) return Double.NaN;
		return Double.parseDouble(object.toString());
	}

	private Double getColumnMean(Column column) {
		return Double.parseDouble(column.getDescriptiveStatisticsProfile().getMean());
	}

	private Double getColumnStandardDeviation(Column column) {
		return Double.parseDouble(column.getDescriptiveStatisticsProfile().getStandardDeviation());
	}

}
