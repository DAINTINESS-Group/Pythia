package gr.uoi.cs.pythia.patterns.algos;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import gr.uoi.cs.pythia.patterns.PatternConstants;
import gr.uoi.cs.pythia.patterns.results.DominancePatternResult;

public class DominancePatternAlgo implements IPatternAlgo {

	private static final double TOTAL_DOMINANCE_THRESHOLD = 100.0;
	private static final double PARTIAL_DOMINANCE_THRESHOLD = 75.0;

	private List<DominancePatternResult> results;
	
	public DominancePatternAlgo() {
		this.results = new ArrayList<DominancePatternResult>();
	}
		
	public DominancePatternResult getLatestResult() {
		return results.get(results.size()-1);
	}
	
	@Override
	public String getPatternName() {
		return PatternConstants.DOMINANCE;
	}
	
	@Override
	public void identifyPatternWithOneCoordinate(
			Dataset<Row> dataset, 
			String measurementColName,
			String xCoordinateColName) {
		// Add a new pattern result object to the results list
		results.add(new DominancePatternResult(
				"avg", measurementColName, xCoordinateColName));

		// query the dataset
		List<Row> queryResult = runQuery(dataset, measurementColName, xCoordinateColName);
				
		// and actually check for dominance
		identifyDominanceWithOneCoordinate(queryResult);
	}

	@Override
	public void identifyPatternWithTwoCoordinates(
			Dataset<Row> dataset, 
			String measurementColName,
			String xCoordinateColName, 
			String yCoordinateColName) {
		// Add a new pattern result object to the results list
		results.add(new DominancePatternResult(
				"avg", measurementColName, xCoordinateColName, yCoordinateColName));
		
		// query the dataset
		List<Row> queryResult = runQuery(dataset, measurementColName, 
				xCoordinateColName, yCoordinateColName);
		
		// and actually check for dominance
		identifyDominanceWithTwoCoordinates(queryResult);
	}
	
	// This method actually performs the check for dominance.
	// All results, including any identified highlights, are added to the results list.
	private void identifyDominanceWithOneCoordinate(List<Row> queryResult) {
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

			getLatestResult().addIdentificationResult(
					xCoordinate, 
					aggValueA, 
					highDominancePercentage,
					lowDominancePercentage, 
					isHighlight(highlightType), 
					highlightType);
		}
		// TODO filter results such that we only keep top-K highlights.
		// Results for big datasets and/or multiple measurement/coordinates
		// are very likey to be way too long to read.
	}

	// This method actually performs the check for dominance.
	// All results, including any identified highlights, are added to the results object.
	private void identifyDominanceWithTwoCoordinates(List<Row> queryResult) {
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

			getLatestResult().addIdentificationResult(
					xCoordinateA, 
					yCoordinateA,
					aggValueA, 
					highDominancePercentage,
					lowDominancePercentage, 
					isHighlight(highlightType), 
					highlightType);
			}
			// TODO filter results such that we only keep top-K highlights.
			// Results for big datasets and/or multiple measurement/coordinates
			// are very likey to be way too long to read,
			// esp. for 2 coordinates.
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
	// Is it likely that the query returns a dataset that doesn't fit on main
	// memory for very large input datasets?
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

	@Override
	public void exportResultsToFile(String path) throws IOException {
		String str = "## Dominance Pattern Results\n";
		for (DominancePatternResult result : results) {
			str += result.toString();
		}
		writeToFile(path, str);
		
		// TODO eventually we want to write the results to the overall report
		// so we might need the DatasetProfile object here 
		// or alternatively maybe return results object to PatternManager instead (?)
	}
	
	private void writeToFile(String path, String str) throws IOException {
		PrintWriter printWriter = new PrintWriter(new FileWriter(path));
	    printWriter.write(str);
	    printWriter.flush();
	    printWriter.close();
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
