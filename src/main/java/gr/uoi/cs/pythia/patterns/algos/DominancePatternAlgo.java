package gr.uoi.cs.pythia.patterns.algos;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.patterns.results.DominanceHighlight;
import gr.uoi.cs.pythia.patterns.results.HighlightPatternResult;

public class DominancePatternAlgo implements IPatternAlgo {

	private static final double TOTAL_DOMINANCE_THRESHOLD = 100.0;
	private static final double PARTIAL_DOMINANCE_THRESHOLD = 75.0;
	
	private String measurementColumnName;
	private String firstCoordinateColumnName;
	private String secondCoordinateColumnName;
	
	public String getMeasurementColumnName() { return measurementColumnName; }
	public String getFirstCoordinateColumnName() { return firstCoordinateColumnName; }
	public String getSecondCoordinateColumnName() { return secondCoordinateColumnName; }

	@Override
	public void identify(Dataset<Row> dataset, DatasetProfile datasetProfile) throws IOException {
		determineColumnNames(datasetProfile);
		
		// Initialize highlight result objects for one and two coordinates respectively
		// TODO decide what to do with the result & maybe move to PatternManager class
		HighlightPatternResult highlightsWithOneCoordinate = 
				new HighlightPatternResult(
						"Dominance Pattern Highlights With One Coordinate Column",
						measurementColumnName + " (Aggregate)", 
						firstCoordinateColumnName + " (Group By)");
		HighlightPatternResult highlightsWithTwoCoordinates = 
				new HighlightPatternResult(
						"Dominance Pattern Highlights With Two Coordinate Columns",
						measurementColumnName + " (Aggregate)", 
						firstCoordinateColumnName + " (Group By)",
						secondCoordinateColumnName + " (Group By)");
		
		// Identifying dominance with one coordinate and write results to file
		identifyDominanceWithOneCoordinate(dataset, highlightsWithOneCoordinate);
		highlightsWithOneCoordinate.writeResultToFile(new File(String.format(
                "src%stest%sresources%sdominance_results_1.md",
                File.separator, File.separator, File.separator)).getAbsolutePath());
		
		// Identifying dominance with two coordinates and write results to file
		identifyDominanceWithTwoCoordinates(dataset, highlightsWithTwoCoordinates);
		highlightsWithTwoCoordinates.writeResultToFile(new File(String.format(
                "src%stest%sresources%sdominance_results_2.md",
                File.separator, File.separator, File.separator)).getAbsolutePath());
	}

	// TODO maybe move the column selection responsibility to the PatternManager class
	public void determineColumnNames(DatasetProfile datasetProfile) {
		// TODO how do we determine the measurement column?
		// Currently "download" column of the "internet_usage" dataset is hard-coded.
		measurementColumnName = datasetProfile.getColumns().get(6).getName();
		
		// TODO how do we determine the coordinate X column?
		// Currently "name" column of the "internet_usage" dataset is hard-coded.
		firstCoordinateColumnName = datasetProfile.getColumns().get(0).getName();
		
		// TODO how do we determine the coordinate Y column?
		// Currently "session_break_reason" column of the "internet_usage" dataset is hard-coded.
		secondCoordinateColumnName = datasetProfile.getColumns().get(8).getName();
	}	
	
	// This method executes the query to the dataset with one coordinate
	// and actually calculates the check for dominance
	public void identifyDominanceWithOneCoordinate(
			Dataset<Row> dataset, HighlightPatternResult highlights) {
		List<Row> queryResult = runQueryWithOneCoordinate(dataset);
		highlights.setQueryResult(queryResult);
		
		for (Row queryResultRow : queryResult) {
			double aggValue = Double.parseDouble(queryResultRow.get(1).toString());
			int higherValuesCounter = 0;
			int lowerValuesCounter = 0;
			for (Row otherQueryResultRow : queryResult) {
				double otherAggValue = Double.parseDouble(otherQueryResultRow.get(1).toString());
				if (aggValue == otherAggValue) continue;
				if (aggValue > otherAggValue) higherValuesCounter++;				
				else if (aggValue < otherAggValue) lowerValuesCounter++;
			}
			// TODO these are complementary, so maybe refactor to keep one
			double highDominancePercentage = (double) higherValuesCounter / 
					(double) (queryResult.size()-1) * 100;
			double lowDominancePercentage = (double) lowerValuesCounter / 
					(double) (queryResult.size()-1) * 100;
			
			determineDominancePatternHighlight(
					highDominancePercentage, 
					lowDominancePercentage, 
					queryResultRow.get(0).toString(),
					null,
					aggValue,
					highlights);
		}
	}
	
	// This method executes the query to the dataset with two coordinates
	// and actually calculates the check for dominance
	public void identifyDominanceWithTwoCoordinates(
			Dataset<Row> dataset, HighlightPatternResult highlights) {
		List<Row> queryResult = runQueryWithTwoCoordinates(dataset);
		highlights.setQueryResult(queryResult);
		
		for (Row queryResultRow : queryResult) {
			double aggValue = Double.parseDouble(queryResultRow.get(2).toString());
			int higherValuesCounter = 0;
			int lowerValuesCounter = 0;
			for (Row otherQueryResultRow : queryResult) {
				double otherAggValue = Double.parseDouble(otherQueryResultRow.get(2).toString());
				if (aggValue == otherAggValue) continue;
				if (aggValue > otherAggValue) higherValuesCounter++;				
				else if (aggValue < otherAggValue) lowerValuesCounter++;
			}
			double highDominancePercentage = (double) higherValuesCounter / 
					(double) (queryResult.size()-1) * 100;
			double lowDominancePercentage = (double) lowerValuesCounter / 
					(double) (queryResult.size()-1) * 100;
			
			determineDominancePatternHighlight(
					highDominancePercentage, 
					lowDominancePercentage, 
					queryResultRow.get(0) == null ? "" : queryResultRow.get(0).toString(),
					queryResultRow.get(1) == null ? "" : queryResultRow.get(1).toString(),
					aggValue,
					highlights);
		}
	}

	private List<Row> runQueryWithOneCoordinate(Dataset<Row> dataset) {
		return dataset
				.groupBy(firstCoordinateColumnName)
				.avg(measurementColumnName)
				.collectAsList();
	}
	
	private List<Row> runQueryWithTwoCoordinates(Dataset<Row> dataset) {
		return dataset
				.groupBy(firstCoordinateColumnName, secondCoordinateColumnName)
				.avg(measurementColumnName)
				.collectAsList();
	}
	
	// This method checks if the given dominance percentages
	// satisfy the partial or total thresholds and adds them as highlights to the result if so
	private void determineDominancePatternHighlight(
			double highDominancePercentage, 
			double lowDominancePercentage, 
			String firstCoordinateValue, 
			String secondCoordinateValue, 
			double measurementAggValue,
			HighlightPatternResult highlights) {
		String highlightType = "";
		double dominancePercentage = 0;
		
		if (highDominancePercentage >= TOTAL_DOMINANCE_THRESHOLD) {
			highlightType = "total high";
			dominancePercentage = highDominancePercentage;
		}
		else if (highDominancePercentage >= PARTIAL_DOMINANCE_THRESHOLD) {
			highlightType = "partial high";
			dominancePercentage = highDominancePercentage;
		}
		else if (lowDominancePercentage >= TOTAL_DOMINANCE_THRESHOLD) {
			highlightType = "total low";
			dominancePercentage = lowDominancePercentage;
		}
		else if (lowDominancePercentage >= PARTIAL_DOMINANCE_THRESHOLD) {
			highlightType = "partial low";
			dominancePercentage = lowDominancePercentage;
		}
		
		if (!highlightType.equals("")) {
			highlights.addHighlight(new DominanceHighlight(
					highlightType, 
					dominancePercentage, 
					firstCoordinateValue, 
					secondCoordinateValue, 
					measurementAggValue, 
					"(avg)"));
		}
	}
		
	public void debugPrintList(List<Row> list, String title) {
		String str = title;
		for (Row row : list) {
			for (int i=0; i<row.length(); i++) {
				if (row.get(i) == null) continue;
				str += row.get(i).toString() + "\t";
			}
			str += "\n";
		}
		System.out.println(str);
	}
	
	// This is just a note. Please ignore.
	// Convert column in List<Row> to List<Double>:
//		queryResult.stream()
//				.map(s -> Double.parseDouble(s.get(1).toString()))
//				.collect(Collectors.toList());

}
