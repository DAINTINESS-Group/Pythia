package gr.uoi.cs.pythia.model.dominance;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;

public class DominanceResult {

	private final int numOfCoordinates;
	private final String dominanceType;
	private final String aggregationMethod;
	private final String measurementColName;
	private final String xCoordinateColName;
	private String yCoordinateColName;
	private final List<Row> identificationResults;
	private List<Row> queryResult;

	private final DecimalFormat decimalFormat = new DecimalFormat("#.###",
			new DecimalFormatSymbols(Locale.ENGLISH));



	public DominanceResult(
			String dominanceType,
			String aggregationMethod,
			String measurementColumnName,
			String firstCoordinateColumnName) {
		this.numOfCoordinates = 1;
		this.dominanceType = dominanceType;
		this.aggregationMethod = aggregationMethod;
		this.measurementColName = measurementColumnName;
		this.xCoordinateColName = firstCoordinateColumnName;
		this.identificationResults = new ArrayList<>();
		identificationResults.add(RowFactory.create(
				xCoordinateColName,
				measurementColName + " (" + aggregationMethod + ")",
				"Dominance%",
				"Is dominance?",
				"Dominance Type"));
		this.decimalFormat.setRoundingMode(RoundingMode.FLOOR);
	}

	public DominanceResult(
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
		this.identificationResults = new ArrayList<>();
		identificationResults.add(RowFactory.create(
				xCoordinateColName,
				"Dominates the " + xCoordinateColName + "(s)",
				"for the " + yCoordinateColName + "(s)",
				"Dominance%",
				"Is dominance?",
				"Dominance Type",
				"Aggr. Marginal Sum (" + measurementColName + ")"));
		this.queryResult = queryResult;
		this.decimalFormat.setRoundingMode(RoundingMode.FLOOR);
	}

	public void addIdentificationResult(
			String xCoordinate,
			double aggValue,
			double dominancePercentage,
			boolean isDominance,
			String dominanceType) {

		identificationResults.add(RowFactory.create(
				xCoordinate,
				Double.parseDouble(decimalFormat.format(aggValue)),
				Double.parseDouble(decimalFormat.format(dominancePercentage)),
				isDominance,
				dominanceType
				));
	}

	public void addIdentificationResult(
			String xCoordinate,
			List<String> dominatedXValues,
			HashMap<String, List<String>> onYValues,
			double dominancePercentage,
			boolean isDominance,
			String dominanceType,
			double aggValuesMarginalSum) {

		identificationResults.add(RowFactory.create(
				xCoordinate,
				dominatedXValues,
				onYValues,
				Double.parseDouble(decimalFormat.format(dominancePercentage)),
				isDominance,
				dominanceType,
				Double.parseDouble(decimalFormat.format(aggValuesMarginalSum))
				));
	}

	public boolean hasOneCoordinate() {
		return numOfCoordinates == 1;
	}

	public boolean hasTwoCoordinates() {
		return numOfCoordinates == 2;
	}

	public String queryResultToString() {
		StringBuilder str = new StringBuilder(String.format("%-10s%-10s%-10s\n",
				xCoordinateColName, yCoordinateColName, measurementColName));
		for (Row row : queryResult) {
			for (int i = 0; i < row.length(); i++) {
				if (row.get(i) == null) continue;
				str.append(String.format("%-10s", row.get(i).toString()));
			}
			str.append("\n");
		}
		return str.toString();
	}

	public String titleToString() {
		String str = String.format(
				"%s dominance identification results for the %s of %s(s) of %s(s)",
				capitalizeFirstLetter(dominanceType),
				aggregationMethod,
				measurementColName,
				xCoordinateColName
				);
		if (hasTwoCoordinates()) str += String.format(" over the %s(s)", yCoordinateColName);
		str += "\n";
		return str;
	}

	private String capitalizeFirstLetter(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	public String metadataToString() {
		String str = String.format("%-30s%s\n%-30s%s\n%-30s%s\n%-30s%s\n%-30s%s\n",
				"- Dominance Type: ", dominanceType + " dominance",
				"- Num. of Coordinates: ", numOfCoordinates,
				"- Aggregation Method: ", aggregationMethod,
				"- Measurement Column Name: ", measurementColName,
				"- X-Coordinate Column Name: ", xCoordinateColName);
		if (hasTwoCoordinates()) {
			str += String.format("%-30s%s\n", "- Y-Coordinate Column Name: ", yCoordinateColName);
		}
		return str;
	}

	public String identificationResultsToString(boolean isExtensiveReport) {
		StringBuilder str = new StringBuilder();
		for (Row row : identificationResults) {
			boolean isDominance = isDominanceRow(row);
			if (!isExtensiveReport && !isDominance) continue;
			for (int i = 0; i < row.length(); i++) {
				if (row.get(i) == null) continue;
				if (hasTwoCoordinates() && i == 2) continue;
				str.append(String.format(
						"%-" + (getLongestStringLength(i) + 3) + "s",
						row.get(i).toString()));
			}
			str.append("\n");
		}
		return str.toString();
	}

	private boolean isDominanceRow(Row row) {
		// First row contains the titles of columns so we always include it
		if (identificationResults.indexOf(row) == 0) return true;
		if (hasOneCoordinate()) return row.getBoolean(3);
		return row.getBoolean(4);
	}

	private int getLongestStringLength(int index) {
		int length = identificationResults.get(0).get(index).toString().length();
		for (Row row : identificationResults) {
			int itemLength = row.get(index).toString().length();
			if (itemLength > length) length = itemLength;
		}
		return length;
	}

	public int getNumOfCoordinates() {
		return numOfCoordinates;
	}

	public String getDominanceType() {
		return dominanceType;
	}

	public String getAggregationMethod() {
		return aggregationMethod;
	}

	public String getMeasurementColName() {
		return measurementColName;
	}

	public String getXCoordinateColName() {
		return xCoordinateColName;
	}

	public String getYCoordinateColName() {
		return yCoordinateColName;
	}

	public List<Row> getIdentificationResults() {
		return identificationResults;
	}


	public String dominanceToString(boolean isExtensiveReport) {
		StringBuilder str = new StringBuilder();
		for (Row row : identificationResults) {
			if (identificationResults.indexOf(row) == 0) continue;
			if (hasOneCoordinate()) {
				if (row.getBoolean(3)) {
					str.append(buildDominanceStringWithOneCoord(row)).append("\n");
				}
			} else if (hasTwoCoordinates()) {
				if (row.getBoolean(4)) {
					str.append(buildDominanceStringWithTwoCoords(row, isExtensiveReport)).append("\n");
				}
			}
		}
		if (str.length() == 0) return "No dominance patterns identified.\n";
		return str.toString();
	}

	private String buildDominanceStringWithOneCoord(Row row) {
		return String.format("- Coordinate: %s (%s) has an aggregate (%s) value of %s (%s)" +
				"\nand a %s dominance of %s%% over the aggregate values of the other %ss.",
				row.getString(0), xCoordinateColName, aggregationMethod,
				decimalFormat.format(row.getDouble(1)),
				measurementColName, row.getString(4),
				decimalFormat.format(row.getDouble(2)), xCoordinateColName
				);
	}

	private String buildDominanceStringWithTwoCoords(Row row, boolean isExtensiveReport) {
		String dominatedOnYValuesString = "";
		if (isExtensiveReport) {
			dominatedOnYValuesString = 
					String.format("\nIn detail, the aggregate values of %s dominate the %ss:" +
							"\n%s", 
							row.getString(0), xCoordinateColName,
							buildDominatedOnYValuesString(row));
		}
		return String.format("- Coordinate X: %s (%s) presents a %s dominance over the %ss: %s. " + 
				dominatedOnYValuesString +
				"Overall, %s has a dominance percentage score of %s%% " +
				"and an aggregate marginal sum of %s (%s).",
				row.getString(0), xCoordinateColName, row.getString(5), xCoordinateColName,
				row.get(1).toString(),
				row.getString(0), decimalFormat.format(row.getDouble(3)),
				decimalFormat.format(row.getDouble(6)), measurementColName
				);
	}

	@SuppressWarnings("unchecked")
	private String buildDominatedOnYValuesString(Row row) {
		HashMap<String, List<String>> hash = (HashMap<String, List<String>>) row.get(2);
		StringBuilder str = new StringBuilder();
		for (String key : (List<String>) row.get(1)) {
			str.append(key).append(" on the ").append(yCoordinateColName).append("(s): ").append(hash.get(key)).append(".\n");
		}
		return str.toString();
	}

	public boolean hasNoDominance() {
		for (Row row : identificationResults) {
			if (identificationResults.indexOf(row) == 0) continue;
			if (isDominanceRow(row)) return false;
		}
		return true;
	}

}
