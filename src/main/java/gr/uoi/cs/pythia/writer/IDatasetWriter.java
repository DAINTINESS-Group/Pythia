package gr.uoi.cs.pythia.writer;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public interface IDatasetWriter {

  void write(Dataset<Row> dataset, String path);
}
