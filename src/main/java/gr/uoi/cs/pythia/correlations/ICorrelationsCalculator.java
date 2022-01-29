package gr.uoi.cs.pythia.correlations;

import gr.uoi.cs.pythia.model.DatasetProfile;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public interface ICorrelationsCalculator {

  void calculateAllPairsCorrelations(Dataset<Row> dataset, DatasetProfile datasetProfile);
}
