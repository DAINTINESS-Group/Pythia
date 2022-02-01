package gr.uoi.cs.pythia.reader;

import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public interface IDatasetReader {

  Dataset<Row> read() throws AnalysisException;
}
