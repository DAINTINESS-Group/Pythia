package gr.uoi.cs.pythia.patterns.results;

// TODO is there a way to achieve abstraction here?
// e.g. having an IHighlight interface *thinking*
public class DominanceHighlight {

	private String type; // partial high, total high, partial low, total low
	private double dominancePercentage;
	private String firstCoordinateValue;
	private String secondCoordinateValue;
	private double measurementAggValue;
	private String aggType; // avg, sum, mean, etc.
	
	public DominanceHighlight(
			String type, 
			double dominancePercentage, 
			String firstCoordinateValue,
			String secondCoordinateValue, 
			double measurementAggValue, 
			String aggType) {
		this.type = type;
		this.dominancePercentage = dominancePercentage;
		this.firstCoordinateValue = firstCoordinateValue;
		this.secondCoordinateValue = secondCoordinateValue;
		this.measurementAggValue = measurementAggValue;
		this.aggType = aggType;
	}

	public String getType() { return type; }
	public double getDominancePercentage() { return dominancePercentage; }
	public String getFirstCoordinateValue() { return firstCoordinateValue; }
	public String getSecondCoordinateValue() { return secondCoordinateValue; }
	public double getMeasurementAggValue() { return measurementAggValue; }
	public String getAggType() { return aggType;}
	
	@Override
	public String toString() {
		String str = "";
		if (secondCoordinateValue == null) {
			str = "Coordinate: " + firstCoordinateValue + 
					" has an aggregate " + aggType + " value of " + measurementAggValue;
		} else {
			str = "Coordinates: " + firstCoordinateValue + ", " + secondCoordinateValue + 
			" have an aggregate " + aggType + " value of " + measurementAggValue;
		}
		str += " \nand a " + type + " dominance of " + dominancePercentage +
				"% over the other aggregate values of the query results.\n";
		return str;
	}
}
