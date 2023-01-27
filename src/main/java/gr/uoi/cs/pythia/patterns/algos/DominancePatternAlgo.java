package gr.uoi.cs.pythia.patterns.algos;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import gr.uoi.cs.pythia.patterns.PatternConstants;
import gr.uoi.cs.pythia.patterns.results.DominancePatternResult;

public class DominancePatternAlgo implements IPatternAlgo {

	private static final double TOTAL_DOMINANCE_THRESHOLD = 100.0;
	private static final double PARTIAL_DOMINANCE_THRESHOLD = 75.0;
	private static final int TOP_K_FILTERING_AMOUNT = 6;
	
	private List<DominancePatternResult> results;
	
	public DominancePatternAlgo() {
		this.results = new ArrayList<DominancePatternResult>();
	}
		
	public DominancePatternResult getLatestResult() {
		return results.get(results.size()-1);
	}
	
	public List<DominancePatternResult> getResults() {
		return results;
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

		// Run aggregate measurement query on the dataset
		List<Row> queryResult = runAggregateQuery(dataset, measurementColName, xCoordinateColName);
				
		// Add a new pattern result object to the results list for high dominance
		results.add(new DominancePatternResult(
				PatternConstants.HIGH, "sum", 
				measurementColName, xCoordinateColName));
		
		// Check for high dominance
		identifyHighDominanceWithOneCoordinate(queryResult);
		
		// Add a new pattern result object to the results list for low dominance
		results.add(new DominancePatternResult(
				PatternConstants.LOW, "sum", 
				measurementColName, xCoordinateColName));
		
		// Check for low dominance
		identifyLowDominanceWithOneCoordinate(queryResult);
	}

	@Override
	public void identifyPatternWithTwoCoordinates(
			Dataset<Row> dataset, 
			String measurementColName,
			String xCoordinateColName, 
			String yCoordinateColName) {
		
		// Find the distinct values of X and Y coordinates
		List<String> xCoordinates = runGetDistinctValuesQuery(dataset, xCoordinateColName);
		List<String> yCoordinates = runGetDistinctValuesQuery(dataset, yCoordinateColName);
		
		// Run aggregate measurement query on the dataset
		List<Row> queryResult = runAggregateQuery(dataset, measurementColName, 
				xCoordinateColName, yCoordinateColName);
		
		// Add a new pattern result object to the results list for high dominance
		results.add(new DominancePatternResult(
				PatternConstants.HIGH, "sum", 
				measurementColName, xCoordinateColName, yCoordinateColName));
		
		// Check for high dominance
		identifyHighDominanceWithTwoCoordinates(queryResult, xCoordinates, yCoordinates);
		
		// Add a new pattern result object to the results list for low dominance
		results.add(new DominancePatternResult(
				PatternConstants.LOW, "sum", 
				measurementColName, xCoordinateColName, yCoordinateColName));
		
		// Check for low dominance
		identifyLowDominanceWithTwoCoordinates(queryResult, xCoordinates, yCoordinates);
	}
	
	// This method actually performs the check for high dominance with 1 coordinate.
	// Identified results are added to the results list.
	// Finally, identified results are sorted and filtered based on dominance percentage score.
	private void identifyHighDominanceWithOneCoordinate(List<Row> queryResult) {
		if (queryResult.size() <= 1) return;
		for (Row rowA : queryResult) {
			String xCoordinate = parseCoordinateValue(rowA, 0);
			double aggValueA = parseAggregateValue(rowA);
			if (xCoordinate.isEmpty()) continue; 
			if (Double.isNaN(aggValueA)) continue;
			int dominatedValues = 0;
			for (Row rowB : queryResult) {
				double aggValueB = parseAggregateValue(rowB);
				if (Double.isNaN(aggValueB)) continue;
				if (isSameRow(rowA, rowB)) continue;
				if (aggValueA > aggValueB) dominatedValues++;
			}
			
			double dominancePercentage = (double) dominatedValues 
					/ (double) (queryResult.size() - 1) * 100;
			String highlightType = determineHighlightType(
					dominancePercentage, PatternConstants.HIGH);
			
			getLatestResult().addIdentificationResult(
					xCoordinate, 
					aggValueA, 
					dominancePercentage,
					isHighlight(highlightType), 
					highlightType);
		}
		sortDescendingIdentificationResults();
		filterTopKIdentificationResults();
	}
	
	// This method actually performs the check for low dominance with 1 coordinate.
	// Identified results are added to the results list.
	// Finally, identified results are sorted and filtered based on dominance percentage score.
	private void identifyLowDominanceWithOneCoordinate(List<Row> queryResult) {
		if (queryResult.size() <= 1) return;
		for (Row rowA : queryResult) {
			String xCoordinate = parseCoordinateValue(rowA, 0);
			double aggValueA = parseAggregateValue(rowA);
			if (xCoordinate.isEmpty()) continue; 
			if (Double.isNaN(aggValueA)) continue;
			int dominatedValues = 0;
			for (Row rowB : queryResult) {
				double aggValueB = parseAggregateValue(rowB);
				if (Double.isNaN(aggValueB)) continue;
				if (isSameRow(rowA, rowB)) continue;
				if (aggValueA < aggValueB) dominatedValues++;
			}
			
			double dominancePercentage = (double) dominatedValues 
					/ (double) (queryResult.size() - 1) * 100;
			String highlightType = determineHighlightType(
					dominancePercentage, PatternConstants.LOW);
			
			getLatestResult().addIdentificationResult(
					xCoordinate, 
					aggValueA, 
					dominancePercentage,
					isHighlight(highlightType), 
					highlightType);
		}
		sortDescendingIdentificationResults();
		filterTopKIdentificationResults();
	}

	// Sort the latest identification results in descending order based on dominance percentage.
	private void sortDescendingIdentificationResults() {
		Collections.sort(getLatestResult().getIdentificationResults(), new Comparator<Row>() {
		     public int compare(Row row1, Row row2) {
		    	 double row1Score, row2Score;
		    	 try {
		    		 int domPercentageIndex = getLatestResult().getNumOfCoordinates() + 1;
		    		 row1Score = Double.parseDouble(row1.get(domPercentageIndex).toString());
		    		 row2Score = Double.parseDouble(row2.get(domPercentageIndex).toString());
		    	 } catch(Exception e) {
		    		 return 0;
		    	 }
		         if(row1Score == row2Score) return 0;
		         return row1Score < row2Score ? 1 : -1;
		     }
		});
	}
	
	private void filterTopKIdentificationResults() {
		List<Row> identificationResults = getLatestResult().getIdentificationResults();
		if (identificationResults.size()-1 <= TOP_K_FILTERING_AMOUNT) return;
		identificationResults.removeIf(row -> 
			identificationResults.indexOf(row) != 0 &&
			identificationResults.indexOf(row) > TOP_K_FILTERING_AMOUNT);
	}

	private void identifyHighDominanceWithTwoCoordinates(
			List<Row> queryResult, 
			List<String> xCoordinates, 
			List<String> yCoordinates) {
		if (queryResult.size() <= 1) return;
		for (String xCoordinateA: xCoordinates) {
			List<String> dominatedXValues = new ArrayList<String>();
			List<String> onYValues = new ArrayList<String>();
			for (String xCoordinateB : xCoordinates) {
				if (xCoordinateA.equals(xCoordinateB)) continue;
				boolean isDominance = false;
				for (String yCoordinate : yCoordinates) {
					double aggValueA = getAggValue(xCoordinateA, yCoordinate, queryResult);
					double aggValueB = getAggValue(xCoordinateB, yCoordinate, queryResult);
					if (Double.isNaN(aggValueA)) continue;
					if (Double.isNaN(aggValueB)) continue;
					if (aggValueA > aggValueB) {
						if (!onYValues.contains(yCoordinate)) onYValues.add(yCoordinate);
						isDominance = true;
					}
					else {
						isDominance = false;
						break;
					}
				}
				if (isDominance) dominatedXValues.add(xCoordinateB);
			}
			double dominancePercentage = (double) dominatedXValues.size() 
					/ (double) (xCoordinates.size() - 1) * 100;
			String highlightType = determineHighlightType(
					dominancePercentage, PatternConstants.HIGH);
			
			getLatestResult().addIdentificationResult(
					xCoordinateA, 
					dominatedXValues, 
					onYValues,
					dominancePercentage,
					isHighlight(highlightType),
					highlightType,
					calculateAggValuesMarginalSum(xCoordinateA, queryResult));
		}
		sortDescendingIdentificationResults();
		filterTopKIdentificationResults();
	}

	private void identifyLowDominanceWithTwoCoordinates(
			List<Row> queryResult, 
			List<String> xCoordinates, 
			List<String> yCoordinates) {
		if (queryResult.size() <= 1) return;
		for (String xCoordinateA: xCoordinates) {
			List<String> dominatedXValues = new ArrayList<String>();
			List<String> onYValues = new ArrayList<String>();
			for (String xCoordinateB : xCoordinates) {
				if (xCoordinateA.equals(xCoordinateB)) continue;
				boolean isDominance = false;
				for (String yCoordinate : yCoordinates) {
					double aggValueA = getAggValue(xCoordinateA, yCoordinate, queryResult);
					double aggValueB = getAggValue(xCoordinateB, yCoordinate, queryResult);
					if (Double.isNaN(aggValueA)) continue;
					if (Double.isNaN(aggValueB)) continue;
					if (aggValueA < aggValueB) {
						if (!onYValues.contains(yCoordinate)) onYValues.add(yCoordinate);
						isDominance = true;
					}
					else {
						isDominance = false;
						break;
					}
				}
				if (isDominance) dominatedXValues.add(xCoordinateB);
			}
			double dominancePercentage = (double) dominatedXValues.size() 
					/ (double) (xCoordinates.size() - 1) * 100;
			String highlightType = determineHighlightType(
					dominancePercentage, PatternConstants.LOW);
			
			getLatestResult().addIdentificationResult(
					xCoordinateA, 
					dominatedXValues,
					onYValues,
					dominancePercentage,
					isHighlight(highlightType),
					highlightType,
					calculateAggValuesMarginalSum(xCoordinateA, queryResult));
		}
		sortDescendingIdentificationResults();
		filterTopKIdentificationResults();
	}
	
	private double getAggValue(String xCoordinate, String yCoordinate,
			List<Row> queryResult) {
		for (Row row : queryResult) {
			String currentXCoordinate = row.getString(0);
			String currentYCoordinate = row.getString(1);
			if (xCoordinate.equals(currentXCoordinate) &&
					yCoordinate.equals(currentYCoordinate)) {
				return parseAggregateValue(row);
			}
		}
		return Double.NaN;
	}
	
	private double calculateAggValuesMarginalSum(
			String xCoordinate, List<Row> queryResult) {
		double aggValuesMarginalSum = 0;
		for (Row row : queryResult) {
			String currentXCoordinate = row.getString(0);
			if (xCoordinate.equals(currentXCoordinate)) {
				double aggValue = parseAggregateValue(row);
				if (Double.isNaN(aggValue)) continue;
				aggValuesMarginalSum +=  aggValue;
			}
		}
		return aggValuesMarginalSum;
	}

	private double parseAggregateValue(Row row) {
		if (row.get(row.length() - 1) == null) return Double.NaN;
		return Double.parseDouble(row.get(row.length() - 1).toString());
	}

	private String parseCoordinateValue(Row row, int indexOfCoordinate) {
		if (row.get(indexOfCoordinate) == null) return "";
		return row.get(indexOfCoordinate).toString();
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
	private List<Row> runAggregateQuery(
			Dataset<Row> dataset, 
			String measurementColName,
			String xCoordinateColName) {
		return dataset
				.groupBy(xCoordinateColName)
				.sum(measurementColName)
				.collectAsList();
	}

	// TODO is it ok to use collectAsList here?
	// Is it likely that the query returns a dataset that doesn't fit on main
	// memory for very large input datasets?
	private List<Row> runAggregateQuery(
			Dataset<Row> dataset, 
			String measurementColName,
			String xCoordinateColName, 
			String yCoordinateColName) {
		return dataset
				.groupBy(xCoordinateColName, yCoordinateColName)
				.sum(measurementColName)
				.orderBy(xCoordinateColName, yCoordinateColName)
				.collectAsList();
	}
	
	private List<String> runGetDistinctValuesQuery(Dataset<Row> dataset, String colName) {
		return dataset
				.select(colName)
				.distinct()
				.orderBy(colName)
				.collectAsList()
				.stream()
				.map(s -> s.get(0).toString())
				.collect(Collectors.toList());
	}

	// This method checks if the given dominance percentages
	// satisfy the partial or total thresholds and returns a string
	// that describes the type of the highlight.
	private String determineHighlightType(double dominancePercentage, String dominanceType) {
		if (dominancePercentage >= TOTAL_DOMINANCE_THRESHOLD) {
			return String.format("%s %s", PatternConstants.TOTAL, dominanceType);
		}
		if (dominancePercentage >= PARTIAL_DOMINANCE_THRESHOLD) {
			return String.format("%s %s", PatternConstants.PARTIAL, dominanceType);
		}
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
