package gr.uoi.cs.pythia.patterns.algos;

import java.io.IOException;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import gr.uoi.cs.pythia.model.DatasetProfile;

public interface IPatternAlgo {
	
	void identify(Dataset<Row> dataset, String measurementColName, String xCoordinateColName)
			throws IOException;
	
	void identify(Dataset<Row> dataset, String measurementColName, String xCoordinateColName,
			String yCoordinateColName) throws IOException;
}
