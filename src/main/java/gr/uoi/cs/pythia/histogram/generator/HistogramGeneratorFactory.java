package gr.uoi.cs.pythia.histogram.generator;

import gr.uoi.cs.pythia.model.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class HistogramGeneratorFactory {

    public IHistogramGenerator createGenerator(Dataset<Row> dataset, Column column, HistogramGeneratorType type) {
        switch (type) {
            case KEEP_NANS:
                return new HistogramGeneratorKeepNaNs(dataset, column);
            case SKIP_NANS:
                return new HistogramGeneratorSkipNaNs(dataset, column);
            default:
                return new HistogramGeneratorKeepNaNs(dataset, column);
        }
    }
}
