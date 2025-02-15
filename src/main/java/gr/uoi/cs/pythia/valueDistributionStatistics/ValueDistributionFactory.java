package gr.uoi.cs.pythia.valueDistributionStatistics;

import gr.uoi.cs.pythia.model.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class ValueDistributionFactory {

    public IValueDistributionsTasks createDistribution(Dataset<Row> dataset, Column column){
        return new SparkValueDistributionsCalculator(dataset,column);
    }
}
