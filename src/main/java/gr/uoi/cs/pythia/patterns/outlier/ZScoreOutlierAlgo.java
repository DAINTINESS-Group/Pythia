package gr.uoi.cs.pythia.patterns.outlier;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.DataTypes;

import gr.uoi.cs.pythia.config.AnalysisParameters;
import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;

public class ZScoreOutlierAlgo implements IOutlierAlgo {

	private static final String Z_SCORE_OUTLIER = "z_score_outlier";
	private static final double Z_SCORE_THRESHOLD = 3.0;
	
	private List<OutlierResult> results;
	
	public ZScoreOutlierAlgo() {
		this.results = new ArrayList<OutlierResult>();
	}
	
	@Override
	public List<OutlierResult> getResults() {
		return results;
	}

	@Override
	public String getPatternName() {
		return Z_SCORE_OUTLIER;
	}
	
	@Override
	public void identifyOutliers(
			Dataset<Row> dataset, 
			DatasetProfile datasetProfile,
			AnalysisParameters analysisParameters) {

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
					results.add(new OutlierResult(column.getName(), value, zScore, index+1));
				}
			}
		}
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
	
	@Override
	public void exportResultsToFile(String path) throws IOException {
		String str = String.format("## Z Score Outlier Pattern Results\n\n" +
				"Total outliers found: %s\n", results.size());
		for (OutlierResult result : results) {
			str += result.toString();
		}
		writeToFile(path, str);
		
		// TODO eventually we want to write the results to the overall report
		// we probably need a PatternsProfile model class
	}

	private void writeToFile(String path, String str) throws IOException {
		PrintWriter printWriter = new PrintWriter(new FileWriter(path));
	    printWriter.write(str);
	    printWriter.flush();
	    printWriter.close();
	}

}
