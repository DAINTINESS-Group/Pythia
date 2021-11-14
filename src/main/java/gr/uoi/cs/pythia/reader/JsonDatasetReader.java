package gr.uoi.cs.pythia.reader;

import lombok.Data;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;

@Data
public class JsonDatasetReader implements IDatasetReader {

  private final SparkSession sparkSession;
  private final String path;
  private final StructType schema;

  @Override
  public Dataset<Row> read() {
    return sparkSession.read().option("header", "true").schema(schema).json(path);
  }
}
