package gr.uoi.cs.pythia.reader;

import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;
import org.sparkproject.guava.io.Files;

public class IDatasetReaderFactory {

  private final SparkSession sparkSession;

  public IDatasetReaderFactory(SparkSession sparkSession) {
    this.sparkSession = sparkSession;
  }

  public IDatasetReader createDataframeReader(String path, StructType schema) {
    String fileExtension = Files.getFileExtension(path);
    switch (fileExtension) {
      case DatasetReaderConstants.CSV:
        return new DelimiterSeparatedDatasetReader(
            sparkSession, path, DatasetReaderConstants.CSV_DELIMITER, schema);
      case DatasetReaderConstants.TSV:
        return new DelimiterSeparatedDatasetReader(
            sparkSession, path, DatasetReaderConstants.TSV_DELIMITER, schema);
      case DatasetReaderConstants.JSON:
        return new JsonDatasetReader(sparkSession, path, schema);
    }
    throw new IllegalArgumentException(
        String.format("File %s is not a supported dataset type.", path));
  }
}
