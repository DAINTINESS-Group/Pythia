package gr.uoi.cs.pythia.patterns.algos;

import java.util.List;

import org.apache.spark.sql.Row;

public class DominancePatternAlgo implements IPatternAlgo {

	@Override
	public void identify(List<Row> measurementColumn, List<Row> coordinateColumn) {
		// TODO Actually check for a dominance highlight
		// Currently this method just iterates through the values of the two columns and prints them
		String str = "Identifying potential dominance pattern highlights...";
		str += "\nMeasurement column values: ";
		for (Row measurementValue : measurementColumn) {
			str += measurementValue.get(0).toString() + " ";
		}
		str += "\nCoordinate column values: ";
		for (Row coordinateValue : coordinateColumn) {
			str += coordinateValue.get(0).toString() + " ";
		}
		System.out.println(str);
	}

}
