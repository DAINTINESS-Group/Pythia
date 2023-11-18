package gr.uoi.cs.pythia.patterns;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.outlier.OutlierType;
import gr.uoi.cs.pythia.patterns.dominance.DominanceParameters;

public class IPatternManagerFactory {
	public IPatternManager createPatternManager(
			Dataset<Row> dataset,
			DatasetProfile datasetProfile,
			DominanceParameters dominanceAnalysisParameters,
			OutlierType outlierType,
			double outlierThreshold) {
		return new PatternManager(
				dataset,
				datasetProfile,
				dominanceAnalysisParameters,
				outlierType,
				outlierThreshold);
	}
}
