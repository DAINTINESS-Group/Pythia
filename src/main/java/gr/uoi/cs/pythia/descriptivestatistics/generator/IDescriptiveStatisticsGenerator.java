package gr.uoi.cs.pythia.descriptivestatistics.generator;

import gr.uoi.cs.pythia.model.DatasetProfile;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public interface IDescriptiveStatisticsGenerator {

     /**
      * Computes the min, max, mean, median, count and standard deviation for each column
      * and sets the Descriptive Statistics Profile of each column.
      * @param dataset The corresponding dataset
      * @param datasetProfile The corresponding dataset profile that contains the columns
      */
     void computeDescriptiveStats(Dataset<Row> dataset, DatasetProfile datasetProfile);
}
