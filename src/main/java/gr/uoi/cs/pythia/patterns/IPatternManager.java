package gr.uoi.cs.pythia.patterns;

import java.io.IOException;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import gr.uoi.cs.pythia.model.DatasetProfile;

public interface IPatternManager {

	public void identifyPatternHighlights(Dataset<Row> dataset, DatasetProfile datasetProfile) throws IOException;
}
