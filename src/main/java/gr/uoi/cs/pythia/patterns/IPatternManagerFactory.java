package gr.uoi.cs.pythia.patterns;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import gr.uoi.cs.pythia.config.AnalysisParameters;
import gr.uoi.cs.pythia.model.DatasetProfile;

public class IPatternManagerFactory {
	public IPatternManager createPatternManager(
			Dataset<Row> dataset,
			DatasetProfile datasetProfile,
			AnalysisParameters analysisParameters) {
		return new PatternManager(
				dataset,
				datasetProfile,
				analysisParameters);
	}
}
