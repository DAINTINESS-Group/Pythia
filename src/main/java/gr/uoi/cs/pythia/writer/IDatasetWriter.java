package gr.uoi.cs.pythia.writer;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import java.io.IOException;

public interface IDatasetWriter {

  void write(Dataset<Row> dataset, String path) throws IOException;
}
