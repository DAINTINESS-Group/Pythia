package gr.uoi.cs.pythia.patterns.results;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;

public class DominancePatternResult {

	private int numOfCoordinates;
	private String dominanceType;
	private String aggregationMethod;
	private String measurementColName;
	private String xCoordinateColName;
	private String yCoordinateColName;
	private List<Row> identificationResults;
	private List<Row> queryResult;
	
	private DecimalFormat decimalFormat = new DecimalFormat("#.###", 
			new DecimalFormatSymbols(Locale.ENGLISH));
	
	public int getNumOfCoordinates() { return numOfCoordinates; }
	public String getDominanceType() { return dominanceType;  }
	public String getAggregationMethod() { return aggregationMethod; }
	public String getMeasurementColName() { return measurementColName; }
	public String getXCoordinateColName() { return xCoordinateColName; }
	public String getYCoordinateColName() { return yCoordinateColName; }
	public List<Row> getIdentificationResults() { return identificationResults; }

	public DominancePatternResult(
			String dominanceType,
			String aggregationMethod,
			String measurementColumnName, 
			String firstCoordinateColumnName) {
		this.numOfCoordinates = 1;
		this.dominanceType = dominanceType;
		this.aggregationMethod = aggregationMethod;
		this.measurementColName = measurementColumnName;
		this.xCoordinateColName = firstCoordinateColumnName;
		this.identificationResults = new ArrayList<Row>();
		identificationResults.add(RowFactory.create(
				xCoordinateColName, 
				measurementColName + " (" + aggregationMethod + ")", 
				"Dominance%", 
				"Is highlight?", 
				"Highlight Type"));
		this.decimalFormat.setRoundingMode(RoundingMode.FLOOR);
	}

	public DominancePatternResult(
			String dominanceType,
			String aggregationMethod,
			String measurementColumnName, 
			String firstCoordinateColumnName, 
			String secondCoordinateColumnName,
			List<Row> queryResult) {
		this.numOfCoordinates = 2;
		this.dominanceType = dominanceType;
		this.aggregationMethod = aggregationMethod;
		this.measurementColName = measurementColumnName;
		this.xCoordinateColName = firstCoordinateColumnName;
		this.yCoordinateColName = secondCoordinateColumnName;
		this.identificationResults = new ArrayList<Row>();
		identificationResults.add(RowFactory.create(
				xCoordinateColName, 
				"Dominates the " + xCoordinateColName + "(s)",
				"for the " + yCoordinateColName + "(s)",
				"Dominance%", 
				"Is highlight?", 
				"Highlight Type",
				"Aggr. Marginal Sum (" + measurementColName + ")"));
		this.queryResult = queryResult;
		this.decimalFormat.setRoundingMode(RoundingMode.FLOOR);
	}

	public void addIdentificationResult(
			String xCoordinate, 
			double aggValue, 
			double dominancePercentage, 
			boolean isHighlight, 
			String highlightType) {

		identificationResults.add(RowFactory.create(
				xCoordinate, 
				Double.parseDouble(decimalFormat.format(aggValue)),
				Double.parseDouble(decimalFormat.format(dominancePercentage)),
				isHighlight, 
				highlightType
		));	
	}
	
	public void addIdentificationResult(			
			String xCoordinate, 
			List<String> dominatedXValues, 
			HashMap<String, List<String>> onYValues, 
			double dominancePercentage, 
			boolean isHighlight, 
			String highlightType,
			double aggValuesMarginalSum) {
		
		identificationResults.add(RowFactory.create(
				xCoordinate, 
				dominatedXValues,
				onYValues,
				Double.parseDouble(decimalFormat.format(dominancePercentage)),
				isHighlight, 
				highlightType,
				Double.parseDouble(decimalFormat.format(aggValuesMarginalSum))
		));	
	}
	
	private boolean hasOneCoordinate() {
		return numOfCoordinates == 1;
	}
	
	private boolean hasTwoCoordinates() {
		return numOfCoordinates == 2;
	}
	
	@Override
	public String toString() {
		String queryResultToString = "";
		if (hasTwoCoordinates()) {
			queryResultToString = "\n### Query Results:\n" +
					queryResultToString();
		}
		return "\n\n-----------------------------------------------" + 
				"-----------------------------------------------------\n\n" +
				"\n### Metadata:\n" + metadataToString() +
				"\n### Detailed Results:\n" + identificationResultsToString() +
				"\n### Identified Highlights:" + highlightsToString() + 
				queryResultToString;
	}
	
	private String queryResultToString() {
		String str = String.format("%-10s%-10s%-10s\n",
				xCoordinateColName, yCoordinateColName, measurementColName);
		for (Row row : queryResult) {
			for (int i = 0; i < row.length(); i++) {
				if (row.get(i) == null) continue;
				str += String.format("%-10s", row.get(i).toString());
			}
			str += "\n";
		}
		return str;
	}
	
	private String metadataToString() {
		String str = String.format("%-30s%s\n%-30s%s\n%-30s%s\n%-30s%s\n%-30s%s\n",
				"- Dominance Type: ", dominanceType + " dominance",
				"- Num. of Coordinates: ", numOfCoordinates,
				"- Aggregation Method: ", aggregationMethod,
				"- Measurement Column Name: ", measurementColName,
				"- Coordinate X Column Name: ", xCoordinateColName);
		if (hasTwoCoordinates()) {
			str += String.format("%-30s%s\n", "- Coordinate Y Column Name: ", yCoordinateColName);
		}	
		return str;
	}
	
	// TODO maybe print into markdown table
	private String identificationResultsToString() {
		String str = "";
		for (Row row : identificationResults) {
			for (int i=0; i<row.length(); i++) {
				if (row.get(i) == null) continue;
				if (hasTwoCoordinates() && i == 2) continue;
				str += String.format(
						"%-" + String.valueOf(getLongestStringLength(i) + 3) + "s", 
						row.get(i).toString());
			}
			str += "\n";
		}
		return str;
	}
	
	private int getLongestStringLength(int index) {
		int length = identificationResults.get(0).get(index).toString().length();
		for (Row row : identificationResults) {
			int itemLength = row.get(index).toString().length();
			if (itemLength > length) length = itemLength;
		}
		return length;
	}
	
	private String highlightsToString() {
		String str = "";
		for (Row row : identificationResults) {
			if (identificationResults.indexOf(row) == 0) continue;
				if (hasOneCoordinate()) {
					if (row.getBoolean(3)) {
						str += "\n" + buildHighlightStringWithOneCoord(row) + "\n";	
					}
				}
				else if (hasTwoCoordinates()) {
					if (row.getBoolean(4)) {
						str += "\n" + buildHighlightStringWithTwoCoords(row) + "\n";
					}
				}
		}
		if (str.isEmpty()) return "\nNo highlights identified.\n";
		return str;
	}
	
	private String buildHighlightStringWithOneCoord(Row row) {
		return String.format("- Coordinate: %s (%s) has an aggregate (%s) value of %s (%s)" + 
				"\nand a %s dominance of %s%% over the aggregate values of the other %ss.",
				row.getString(0), xCoordinateColName, aggregationMethod, 
				decimalFormat.format(row.getDouble(1)), 
				measurementColName, row.getString(4), 
				decimalFormat.format(row.getDouble(2)), xCoordinateColName
		);
	}
	
	private String buildHighlightStringWithTwoCoords(Row row) {
		return String.format("- Coordinate X: %s (%s) presents a %s dominance over the %ss: %s." +
				"\nIn detail, the aggregate values of %s dominate the %ss:" +
				"\n%s" +
				"Overall, %s has a dominance percentage score of %s%% " +
				"and an aggregate marginal sum of %s (%s).",
				row.getString(0), xCoordinateColName, row.getString(5), xCoordinateColName, 
				row.get(1).toString(), row.getString(0), xCoordinateColName,
				buildDominatedOnYValuesString(row), 
				row.getString(0), decimalFormat.format(row.getDouble(3)),
				decimalFormat.format(row.getDouble(6)), measurementColName
		);
	}
	
	@SuppressWarnings("unchecked")
	private String buildDominatedOnYValuesString(Row row) {
		HashMap<String, List<String>> hash = (HashMap<String, List<String>>) row.get(2);
		String str = "";
		for (String key : (List<String>) row.get(1)) {
			str +=  key + " on the " + yCoordinateColName +"(s): " + hash.get(key) + ".\n";
		}
		return str;
	}
		
}
