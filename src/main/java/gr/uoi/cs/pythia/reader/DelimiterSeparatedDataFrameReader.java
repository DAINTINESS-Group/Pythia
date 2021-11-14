package gr.uoi.cs.pythia.reader;

import lombok.Data;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;

@Data
public class DelimiterSeparatedDataFrameReader implements IDatasetReader {

  private final SparkSession sparkSession;
  private final String path;
  private final String separator;
  private final StructType schema;

  public Dataset<Row> read() {
    return sparkSession
        .read()
        .option("header", "true")
        .option("sep", separator)
        .schema(schema)
        .csv(path);
  }
}
