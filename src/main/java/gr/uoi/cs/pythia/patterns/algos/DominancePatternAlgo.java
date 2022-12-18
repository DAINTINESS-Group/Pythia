package gr.uoi.cs.pythia.patterns.algos;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import gr.uoi.cs.pythia.patterns.PatternConstants;
import gr.uoi.cs.pythia.patterns.results.DominancePatternResults;

public class DominancePatternAlgo implements IPatternAlgo {

	private static final double TOTAL_DOMINANCE_THRESHOLD = 100.0;
	private static final double PARTIAL_DOMINANCE_THRESHOLD = 75.0;

	// TODO maybe keep a list of DominancePatternResults objects (?) to keep 
	// the dominance results of multiple identifications 
	// for different measurement and coordinate(s) columns
	private DominancePatternResults results;
	
	public DominancePatternResults getResults() { return results; }

	@Override
	public void identify(
			Dataset<Row> dataset, 
			String measurementColName,
			String xCoordinateColName) 
					throws IOException {
		// Initialize pattern result object for identification with one coordinate
		results = new DominancePatternResults(
				"## Dominance Pattern Results With One Coordinate Column",
				"avg", measurementColName, xCoordinateColName);

		// Identify dominance with one coordinate
		identifyDominanceWithOneCoordinate(dataset, measurementColName,
				xCoordinateColName);

		// and write results to file
		results.writeToFile(new File(String.format(
				"src%stest%sresources%sdominance_results_1.md",
				File.separator, File.separator, File.separator)).getAbsolutePath());

		// TODO eventually we want to write the results to the overall report
		// so we might need the DatasetProfile object here 
		// or alternatively maybe return results object to PatternManager instead (?)
	}

	@Override
	public void identify(
			Dataset<Row> dataset, 
			String measurementColName,
			String xCoordinateColName, 
			String yCoordinateColName) 
					throws IOException {
		// Initialize pattern result object for identification with two coordinates
		results = new DominancePatternResults(
				"## Dominance Pattern Results With Two Coordinate Columns",
				"avg", measurementColName, xCoordinateColName, yCoordinateColName);

		// Identify dominance with two coordinates
		identifyDominanceWithTwoCoordinates(dataset, measurementColName,
				xCoordinateColName, yCoordinateColName);

		// and write results to file
		results.writeToFile(new File(String.format("src%stest%sresources%sdominance_results_2.md",
				File.separator, File.separator, File.separator)).getAbsolutePath());

		// TODO eventually we want to write the results to the overall report
		// so we might need the DatasetProfile object here 
		// or alternatively maybe return results object to PatternManager instead (?)
	}

	// This method executes the query to the dataset with one coordinate
	// and actually performs the check for dominance.
	// All results, including any identified highlights, are added to the results object.
	private void identifyDominanceWithOneCoordinate(
			Dataset<Row> dataset,
			String measurementColName,
			String xCoordinateColName) {

		List<Row> queryResult = runQuery(dataset, measurementColName, xCoordinateColName);

		for (Row rowA : queryResult) {
			String xCoordinate = parseCoordinateValue(rowA, 0);
			double aggValueA = parseAggregateValue(rowA);
			if (xCoordinate.isEmpty()) continue; 
			if (isNotANumber(aggValueA)) continue;
			int totalGreaterValues = 0;
			int totalLesserValues = 0;
			double highDominancePercentage = 0.0;
			double lowDominancePercentage = 0.0;
			for (Row rowB : queryResult) {
				double aggValueB = parseAggregateValue(rowB);
				if (isNotANumber(aggValueB)) continue;
				if (isSameRow(rowA, rowB)) continue;
				if (aggValueA > aggValueB) totalGreaterValues++;
				else if (aggValueA < aggValueB) totalLesserValues++;
			}
			
			if (queryResult.size() - 1 > 0) {
				highDominancePercentage = (double) totalGreaterValues
						/ (double) (queryResult.size() - 1) * 100;
				lowDominancePercentage = (double) totalLesserValues
						/ (double) (queryResult.size() - 1) * 100;
			}
			
			String highlightType = determineHighlightType(
					highDominancePercentage, lowDominancePercentage);

			results.addResult(
					xCoordinate, 
					aggValueA, 
					highDominancePercentage,
					lowDominancePercentage, 
					isHighlight(highlightType), 
					highlightType);
		}
	}

	// This method executes the query to the dataset with two coordinates
	// and actually performs the check for dominance.
	// All results, including any identified highlights, are added to the results object.
	private void identifyDominanceWithTwoCoordinates(
			Dataset<Row> dataset,
			String measurementColName,
			String xCoordinateColName, 
			String yCoordinateColName) {

		List<Row> queryResult = runQuery(dataset, measurementColName, 
				xCoordinateColName, yCoordinateColName);
		
		for (Row rowA : queryResult) {
			String xCoordinateA = parseCoordinateValue(rowA, 0);
			String yCoordinateA = parseCoordinateValue(rowA, 1);
			double aggValueA = parseAggregateValue(rowA);
			if (xCoordinateA.isEmpty()) continue; 
			if (yCoordinateA.isEmpty()) continue; 
			if (isNotANumber(aggValueA)) continue; 
			int totalGreaterValues = 0;
			int totalLowerValues = 0;
			int totalValues = 0;
			double highDominancePercentage = 0.0;
			double lowDominancePercentage = 0.0;
			for (Row rowB : queryResult) {
				String yCoordinateB = parseCoordinateValue(rowB, 1);
				double aggValueB = parseAggregateValue(rowB);
				if (yCoordinateB.isEmpty()) continue; 
				if (isNotANumber(aggValueB)) continue;
				if (isNotSameCoordinate(yCoordinateA, yCoordinateB)) continue; 
				if (isSameRow(rowA, rowB)) continue;
				if (aggValueA > aggValueB) totalGreaterValues++;
				else if (aggValueA < aggValueB) totalLowerValues++;
				totalValues++;
			}

			if (totalValues > 0) {
				highDominancePercentage = (double) totalGreaterValues 
						/ (double) (totalValues) * 100;
				lowDominancePercentage = (double) totalLowerValues
						/ (double) (totalValues) * 100;
			}				
			
			String highlightType = determineHighlightType(
					highDominancePercentage, lowDominancePercentage);

			results.addResult(
					xCoordinateA, 
					yCoordinateA,
					aggValueA, 
					highDominancePercentage,
					lowDominancePercentage, 
					isHighlight(highlightType), 
					highlightType);
			}
	}

	private double parseAggregateValue(Row row) {
		if (row.get(row.length() - 1) == null) return Double.NaN;
		return Double.parseDouble(row.get(row.length() - 1).toString());
	}

	private String parseCoordinateValue(Row row, int indexOfCoordinate) {
		if (row.get(indexOfCoordinate) == null) return "";
		return row.get(indexOfCoordinate).toString();
	}

	private boolean isNotANumber(double value) {
		return value == Double.NaN;
	}

	private boolean isNotSameCoordinate(String yCoordinateA, String yCoordinateB) {
		return !yCoordinateA.equals(yCoordinateB);
	}

	private boolean isSameRow(Row rowA, Row rowB) {
		return rowA == rowB;
	}
	
	private boolean isHighlight(String highlightType) {
		return highlightType != PatternConstants.EMPTY;
	}
	
	// TODO is it ok to use collectAsList here?
	// Perhaps the query returns a dataset that doesn't fit on main memory for
	// very large input datasets.
	private List<Row> runQuery(
			Dataset<Row> dataset, 
			String measurementColName,
			String xCoordinateColName) {
		return dataset
				.groupBy(xCoordinateColName)
				.avg(measurementColName)
				.orderBy(xCoordinateColName)
				.collectAsList();
	}

	// TODO is it ok to use collectAsList here?
	// Is it likely that the query returns a dataset that doesn't fit on main
	// memory for very large input datasets?
	private List<Row> runQuery(
			Dataset<Row> dataset, 
			String measurementColName,
			String xCoordinateColName, 
			String yCoordinateColName) {
		return dataset
				.groupBy(xCoordinateColName, yCoordinateColName)
				.avg(measurementColName)
				.orderBy(yCoordinateColName, xCoordinateColName)
				.collectAsList();
	}

	// This method checks if the given dominance percentages
	// satisfy the partial or total thresholds and returns a string
	// that describes the type of the highlight.
	private String determineHighlightType(double highPercentage, double lowPercentage) {
		if (highPercentage >= TOTAL_DOMINANCE_THRESHOLD) return PatternConstants.TOTAL_HIGH;
		if (highPercentage >= PARTIAL_DOMINANCE_THRESHOLD) return PatternConstants.PARTIAL_HIGH;
		if (lowPercentage >= TOTAL_DOMINANCE_THRESHOLD) return PatternConstants.TOTAL_LOW;
		if (lowPercentage >= PARTIAL_DOMINANCE_THRESHOLD) return PatternConstants.PARTIAL_LOW;
		return PatternConstants.EMPTY;
	}

	public void debugPrintList(List<Row> list, String title) {
		String str = title;
		for (Row row : list) {
			for (int i = 0; i < row.length(); i++) {
				if (row.get(i) == null) continue;
				str += row.get(i).toString() + "\t";
			}
			str += "\n";
		}
		System.out.println(str);
	}

	// This is just a note. Please ignore.
	// Convert column in List<Row> to List<Double>:
	// queryResult.stream()
	// .map(s -> Double.parseDouble(s.get(1).toString()))
	// .collect(Collectors.toList());

}
