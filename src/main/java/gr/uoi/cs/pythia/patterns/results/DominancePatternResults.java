package gr.uoi.cs.pythia.patterns.results;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;

public class DominancePatternResults {

	private String title;
	private String aggregationMethod;
	private String measurementColName;
	private String xCoordinateColName;
	private String yCoordinateColName;
	private List<String> highlights; 
	private List<Row> results;
	
	private DecimalFormat decimalFormat = new DecimalFormat("#.###", 
			new DecimalFormatSymbols(Locale.ENGLISH));
	
	public String getTitle() { return title; }
	public String getAggregationMethod() { return aggregationMethod; }
	public String getMeasurementColumnName() { return measurementColName; }
	public String getXCoordinateColName() { return xCoordinateColName; }
	public String getYCoordinateColName() { return yCoordinateColName; }
	public List<String> getHighlights() { return highlights; }
	public List<Row> getResults() { return results; }

	public DominancePatternResults(
			String title,
			String aggregationMethod,
			String measurementColumnName, 
			String firstCoordinateColumnName) {
		this.title = title;
		this.aggregationMethod = aggregationMethod;
		this.measurementColName = measurementColumnName;
		this.xCoordinateColName = firstCoordinateColumnName;
		this.highlights = new ArrayList<String>();
		this.results = new ArrayList<Row>();
		results.add(RowFactory.create(
				xCoordinateColName, 
				measurementColName + " (" + aggregationMethod + ")", 
				"High%", 
				"Low%", 
				"Is highlight?", 
				"Highlight Type"));
		this.decimalFormat.setRoundingMode(RoundingMode.FLOOR);
	}

	public DominancePatternResults(
			String title,
			String aggregationMethod,
			String measurementColumnName, 
			String firstCoordinateColumnName, 
			String secondCoordinateColumnName) {
		this.title = title;
		this.aggregationMethod = aggregationMethod;
		this.measurementColName = measurementColumnName;
		this.xCoordinateColName = firstCoordinateColumnName;
		this.yCoordinateColName = secondCoordinateColumnName;
		this.highlights = new ArrayList<String>();
		this.results = new ArrayList<Row>();
		results.add(RowFactory.create(
				xCoordinateColName, 
				yCoordinateColName,
				measurementColName + " (" + aggregationMethod + ")", 
				"High%", 
				"Low%", 
				"Is highlight?", 
				"Highlight Type"));
		this.decimalFormat.setRoundingMode(RoundingMode.FLOOR);
	}

	public void addResult(
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
					"\nand a %s dominance of %s%% over the other aggregate values of the query results.\n",
					xCoordinate, xCoordinateColName, aggregationMethod, decimalFormat.format(aggValue), 
					measurementColName, highlightType, decimalFormat.format(dominancePercentage)
			));
		}
		results.add(RowFactory.create(
				xCoordinate, 
				Double.parseDouble(decimalFormat.format(aggValue)),
				Double.parseDouble(decimalFormat.format(highDominancePercentage)),
				Double.parseDouble(decimalFormat.format(lowDominancePercentage)), 
				isHighlight, 
				highlightType));	
	}
	
	public void addResult(
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
					"\nfor the coordinate Y: %s (%s).\n", 
					xCoordinate, xCoordinateColName, aggregationMethod, decimalFormat.format(aggValue),
					measurementColName, highlightType, decimalFormat.format(dominancePercentage),
					yCoordinate, yCoordinateColName));
		}
		results.add(RowFactory.create(
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
		String str = title + "\n\n" + 
				"- Aggregation  Method: " + aggregationMethod + "\n" +
				"- Measurement Column Name: " + measurementColName + "\n" +
				"- Coordinate X Column Name: " + xCoordinateColName + "\n";	
		if (hasSecondCoordinate()) {
			str += "- Coordinate Y Column Name: " + yCoordinateColName + "\n";
		}		
		str += "\n### Detailed Results:\n" + resultsToString() + 
				"\n### Identified Highlights:\n" + highlightsToString();	
		return str;
	}
	
	private String highlightsToString() {
		if (highlights.isEmpty()) return "No highlights identified.";
		String str = "";
		for (String highlight : highlights) {
			str += highlight + "\n";
		}
		return str;
	}
	
	// TODO maybe print into markdown table
	private String resultsToString() {
		String str = "";
		for (Row row : results) {
			for (int i=0; i<row.length(); i++) {
				if (row.get(i) == null) continue;
				str += String.format(
						"%-" + String.valueOf(results.get(0).get(i).toString().length() + 4) + "s", 
						row.get(i).toString());
			}
			str += "\n";
		}
		return str;
	}
	
	public void writeToFile(String path) throws IOException {
		PrintWriter printWriter = new PrintWriter(new FileWriter(path));
	    printWriter.write(this.toString());
	    printWriter.flush();
	    printWriter.close();
	}

}
