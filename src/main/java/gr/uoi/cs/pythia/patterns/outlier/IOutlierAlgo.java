package gr.uoi.cs.pythia.patterns.outlier;

import java.io.IOException;
import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import gr.uoi.cs.pythia.model.DatasetProfile;

public interface IOutlierAlgo {

	String getPatternName();
	
	void identifyOutliers(Dataset<Row> dataset, DatasetProfile datasetProfile);

	List<OutlierResult> getResults();
	
	void exportResultsToFile(String path) throws IOException;
}
