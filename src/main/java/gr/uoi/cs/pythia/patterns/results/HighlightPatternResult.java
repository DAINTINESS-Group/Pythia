package gr.uoi.cs.pythia.patterns.results;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.spark.sql.Row;

public class HighlightPatternResult {

	private String title;
	private String measurementColumnName;
	private String firstCoordinateColumnName;
	private String secondCoordinateColumnName;
	private List<Row> queryResult;
	
	// TODO we can't have a list of DominanceHighlight objects
	// if this class is responsible for keeping results of different pattern algos
	private List<DominanceHighlight> highlights; 
	
	public String getTitle() { return title; }
	public String getMeasurementColumnName() { return measurementColumnName; }
	public String getFirstCoordinateColumnName() { return firstCoordinateColumnName; }
	public String getSecondCoordinateColumnName() { return secondCoordinateColumnName; }
	public List<Row> getQueryResult() { return queryResult; }
	public List<DominanceHighlight> getHighlights() { return highlights; }

	public HighlightPatternResult(
			String title,
			String measurementColumnName, 
			String firstCoordinateColumnName) {
		this.title = title;
		this.measurementColumnName = measurementColumnName;
		this.firstCoordinateColumnName = firstCoordinateColumnName;
		this.highlights = new ArrayList<DominanceHighlight>();
	}

	public HighlightPatternResult(
			String title,
			String measurementColumnName, 
			String firstCoordinateColumnName, 
			String secondCoordinateColumnName) {
		this.title = title;
		this.measurementColumnName = measurementColumnName;
		this.firstCoordinateColumnName = firstCoordinateColumnName;
		this.secondCoordinateColumnName = secondCoordinateColumnName;
		this.highlights = new ArrayList<DominanceHighlight>();
	}

	public void setQueryResult(List<Row> queryResult) {
		this.queryResult = queryResult;
	}
	
	public void addHighlight(DominanceHighlight highlight) {
		highlights.add(highlight);
	}
	
	@Override
	public String toString() {
		String str = title + "\n\n" + 
				"- Measurement Column Name: " + measurementColumnName + "\n" +
				"- Fist Coordinate Column Name: " + firstCoordinateColumnName + "\n";	
		if (secondCoordinateColumnName != null) {
			str += "- Second Coordinate Column Name: " + secondCoordinateColumnName + "\n";
		}		
		str += "\n- Query Result:\n" + queryResultToString() + 
				"\n- Identified Highlights:\n" + highlightsToString();	
		return str;
	}
	
	private String highlightsToString() {
		if (highlights.isEmpty()) return "No highlights identified.";
		String str = "";
		for (DominanceHighlight highlight : highlights) {
			str += highlight.toString() + "\n";
		}
		return str;
	}
	
	private String queryResultToString() {
		String str = "";
		for (Row row : queryResult) {
			for (int i=0; i<row.length(); i++) {
				if (row.get(i) == null) continue;
				str += row.get(i).toString() + "\t";
			}
			str += "\n";
		}
		return str;
	}
	
	public void writeResultToFile(String path) throws IOException {
		PrintWriter printWriter = new PrintWriter(new FileWriter(path));
	    printWriter.write(this.toString());
	    printWriter.flush();
	    printWriter.close();
	}
}
