package gr.uoi.cs.pythia.patterns.results;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;

public class DominancePatternResult {

	private String aggregationMethod;
	private String measurementColName;
	private String xCoordinateColName;
	private String yCoordinateColName;
	private List<String> highlights; 
	private List<Row> identificationResults;
	
	private DecimalFormat decimalFormat = new DecimalFormat("#.###", 
			new DecimalFormatSymbols(Locale.ENGLISH));
	
	public String getAggregationMethod() { return aggregationMethod; }
	public String getMeasurementColName() { return measurementColName; }
	public String getXCoordinateColName() { return xCoordinateColName; }
	public String getYCoordinateColName() { return yCoordinateColName; }
	public List<String> getHighlights() { return highlights; }
	public List<Row> getIdentificationResults() { return identificationResults; }

	public DominancePatternResult(
			String aggregationMethod,
			String measurementColumnName, 
			String firstCoordinateColumnName) {
		this.aggregationMethod = aggregationMethod;
		this.measurementColName = measurementColumnName;
		this.xCoordinateColName = firstCoordinateColumnName;
		this.highlights = new ArrayList<String>();
		this.identificationResults = new ArrayList<Row>();
		identificationResults.add(RowFactory.create(
				xCoordinateColName, 
				measurementColName + " (" + aggregationMethod + ")", 
				"High%", 
				"Low%", 
				"Is highlight?", 
				"Highlight Type"));
		this.decimalFormat.setRoundingMode(RoundingMode.FLOOR);
	}

	public DominancePatternResult(
			String aggregationMethod,
			String measurementColumnName, 
			String firstCoordinateColumnName, 
			String secondCoordinateColumnName) {
		this.aggregationMethod = aggregationMethod;
		this.measurementColName = measurementColumnName;
		this.xCoordinateColName = firstCoordinateColumnName;
		this.yCoordinateColName = secondCoordinateColumnName;
		this.highlights = new ArrayList<String>();
		this.identificationResults = new ArrayList<Row>();
		identificationResults.add(RowFactory.create(
				xCoordinateColName, 
				yCoordinateColName,
				measurementColName + " (" + aggregationMethod + ")", 
				"High%", 
				"Low%", 
				"Is highlight?", 
				"Highlight Type"));
		this.decimalFormat.setRoundingMode(RoundingMode.FLOOR);
	}

	public void addIdentificationResult(
			String xCoordinate, 
			double aggValue, 
			double highDominancePercentage, 
			double lowDominancePercentage, 
			boolean isHighlight, 
			String highlightType) {

		if(isHighlight) {
			double dominancePercentage = lowDominancePercentage;
			if  (highlightType.equals("partial high") || highlightType.equals("total high")) {
				dominancePercentage = highDominancePercentage;
			}
			highlights.add(String.format("- Coordinate: %s (%s) has an aggregate (%s) value of %s (%s)" + 
					"\nand a %s dominance of %s%% over the other aggregate values of the query results.",
					xCoordinate, xCoordinateColName, aggregationMethod, decimalFormat.format(aggValue), 
					measurementColName, highlightType, decimalFormat.format(dominancePercentage)
			));
		}
		identificationResults.add(RowFactory.create(
				xCoordinate, 
				Double.parseDouble(decimalFormat.format(aggValue)),
				Double.parseDouble(decimalFormat.format(highDominancePercentage)),
				Double.parseDouble(decimalFormat.format(lowDominancePercentage)), 
				isHighlight, 
				highlightType));	
	}
	
	public void addIdentificationResult(
			String xCoordinate, 
			String yCoordinate, 
			double aggValue,
			double highDominancePercentage, 
			double lowDominancePercentage, 
			boolean isHighlight,
			String highlightType) {

		if (isHighlight) {
			double dominancePercentage = lowDominancePercentage;
			if  (highlightType.equals("partial high") || highlightType.equals("total high")) {
				dominancePercentage = highDominancePercentage;
			}
			highlights.add(String.format("- Coordinate X: %s (%s) has an aggregate (%s) value of %s (%s)" +
					"\nand a %s dominance of %s%% over the other aggregate values of the query results" +
					"\nfor the coordinate Y: %s (%s).", 
					xCoordinate, xCoordinateColName, aggregationMethod, decimalFormat.format(aggValue),
					measurementColName, highlightType, decimalFormat.format(dominancePercentage),
					yCoordinate, yCoordinateColName));
		}
		identificationResults.add(RowFactory.create(
				xCoordinate,
				yCoordinate,
				Double.parseDouble(decimalFormat.format(aggValue)),
				Double.parseDouble(decimalFormat.format(highDominancePercentage)),
				Double.parseDouble(decimalFormat.format(lowDominancePercentage)), 
				isHighlight, 
				highlightType));
	}
	
	private boolean hasSecondCoordinate() {
		return yCoordinateColName != null;
	}
	
	@Override
	public String toString() {
		return "\n\n-----------------------------------------------" + 
				"-----------------------------------------------------\n\n" +
				"\n### Metadata:\n" + metadataToString() +
				"\n### Detailed Results:\n" + identificationResultsToString() + 
				"\n### Identified Highlights:" + highlightsToString();	
	}
	
	private String metadataToString() {
		String str = String.format("%-30s%s\n%-30s%s\n%-30s%s\n",
				"- Aggregation Method: ", aggregationMethod,
				"- Measurement Column Name: ", measurementColName,
				"- Coordinate X Column Name: ", xCoordinateColName);
		if (hasSecondCoordinate()) {
			str += String.format("%-30s%s\n", "- Aggregation Method: ", yCoordinateColName);
		}	
		return str;
	}
	
	// TODO maybe print into markdown table
	private String identificationResultsToString() {
		String str = "";
		for (Row row : identificationResults) {
			for (int i=0; i<row.length(); i++) {
				if (row.get(i) == null) continue;
				str += String.format(
						"%-" + String.valueOf(identificationResults.get(0).get(i).toString().length() + 4) + "s", 
						row.get(i).toString());
			}
			str += "\n";
		}
		return str;
	}
	
	private String highlightsToString() {
		if (highlights.isEmpty()) return "\nNo highlights identified.\n";
		String str = "";
		for (String highlight : highlights) {
			str += "\n" + highlight + "\n";
		}
		return str;
	}
	
	
}
