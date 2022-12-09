package gr.uoi.cs.pythia.patterns.algos;

import java.io.IOException;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import gr.uoi.cs.pythia.model.DatasetProfile;

public interface IPatternAlgo {
//	void identify(List<Row> measurementColumn, List<Row> coordinateColumn);
	void identify(Dataset<Row> dataset, DatasetProfile datasetProfile) throws IOException;
}
