package gr.uoi.cs.pythia.reader;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;

public class JsonDatasetReader implements IDatasetReader {

  private final SparkSession sparkSession;
  private final String path;
  private final StructType schema;

  public JsonDatasetReader(SparkSession sparkSession, String path, StructType schema) {
    this.sparkSession = sparkSession;
    this.path = path;
    this.schema = schema;
  }

  @Override
  public Dataset<Row> read() {
    return sparkSession.read()
            .schema(schema)
            .json(path);
  }
}
