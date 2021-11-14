package gr.uoi.cs.pythia.writer;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;

public class HadoopDatasetWriter implements IDatasetWriter {

  @Override
  public void write(Dataset<Row> dataset, String path) {
    try {
      dataset
          .repartition(1)
          .write()
          .option("header", true)
          .mode(SaveMode.Overwrite)
          .option("sep", ',')
          .csv(path);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
