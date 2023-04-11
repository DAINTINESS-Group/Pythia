package gr.uoi.cs.pythia.patterns;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.patterns.dominance.DominanceAnalysisParameters;

public class IPatternManagerFactory {
	public IPatternManager createPatternManager(
			Dataset<Row> dataset,
			DatasetProfile datasetProfile,
			DominanceAnalysisParameters dominanceAnalysisParameters) {
		return new PatternManager(
				dataset,
				datasetProfile,
				dominanceAnalysisParameters);
	}
}
