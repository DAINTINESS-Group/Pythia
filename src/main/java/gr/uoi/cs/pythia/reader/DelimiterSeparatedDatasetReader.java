package gr.uoi.cs.pythia.reader;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;

public class DelimiterSeparatedDatasetReader implements IDatasetReader {

  private SparkSession sparkSession;
  private String path;
  private String separator;
  private StructType schema;

  public DelimiterSeparatedDatasetReader(SparkSession sparkSession, String path, String separator, StructType schema) {
    this.sparkSession = sparkSession;
    this.path = path;
    this.separator = separator;
    this.schema = schema;
  }

  public Dataset<Row> read() {
    return sparkSession
        .read()
        .option("header", "true")
        .option("sep", separator)
        .schema(schema)
        .csv(path);
  }
}
