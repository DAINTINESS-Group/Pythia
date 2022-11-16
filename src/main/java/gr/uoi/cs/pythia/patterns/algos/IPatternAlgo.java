package gr.uoi.cs.pythia.patterns.algos;

import java.util.List;

import org.apache.spark.sql.Row;

public interface IPatternAlgo {
	void identify(List<Row> measurementColumn, List<Row> coordinateColumn);
}
