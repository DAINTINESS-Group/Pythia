package gr.uoi.cs.pythia.reader;

import lombok.AllArgsConstructor;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;

@AllArgsConstructor
public class DelimiterSeparatedDataFrameReader implements IDatasetReader {

  private SparkSession sparkSession;
  private String path;
  private String separator;
  private StructType schema;

  public Dataset<Row> read() {
    return sparkSession
        .read()
        .option("header", "true")
        .option("sep", separator)
        .schema(schema)
        .csv(path);
  }
}
