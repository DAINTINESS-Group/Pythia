package gr.uoi.cs.pythia.histogram.generator;

import gr.uoi.cs.pythia.model.DescriptiveStatisticsProfile;
import gr.uoi.cs.pythia.model.histogram.Bin;
import gr.uoi.cs.pythia.model.histogram.Histogram;
import gr.uoi.cs.pythia.model.Column;
import org.apache.spark.ml.feature.Bucketizer;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.*;

/**
 * Skips null and NaN values.
 */
public class HistogramGeneratorSkipNaNs implements IHistogramGenerator {

    private final Dataset<Row> dataset;
    private final Column column;

    public HistogramGeneratorSkipNaNs(Dataset<Row> dataset, Column column) {
        this.dataset = dataset;
        this.column = column;
    }

    public Histogram generateHistogram(int bins) {
        double[] splits = getSplits(bins);
        Dataset<Row> binsToCounts = getBinsToCounts(splits);
        return createHistogram(binsToCounts, splits);
    }

    private double[] getSplits(int totalBins) {
        DescriptiveStatisticsProfile descriptiveStatistics = column.getDescriptiveStatisticsProfile();
        double max = Double.parseDouble(descriptiveStatistics.getMax());
        double min = Double.parseDouble(descriptiveStatistics.getMin());
        double binSize = (max - min) / totalBins;
        double[] splits = new double[totalBins + 1];
        splits[0] = min;
        splits[totalBins] = max;
        for (int i=1; i < totalBins; i++) {
            splits[i] = splits[i-1] + binSize;
        }
        return splits;
    }

    private Dataset<Row> getBinsToCounts(double[] splits) {
        Bucketizer bucketizer = new Bucketizer()
                .setInputCol(column.getName())
                .setOutputCol("bin")
                .setHandleInvalid("skip")
                .setSplits(splits);

        return bucketizer
                .transform(dataset.select(column.getName()))
                .groupBy("bin")
                .count()
                .orderBy("bin");
    }

    private Histogram createHistogram(Dataset<Row> binsToCounts, double[] splits) {
        List<Bin> bins = new ArrayList<>();
        Map<Double, Long> lowerBoundToCount = new HashMap<>();
        Iterator<Row> rowIterator = binsToCounts.toLocalIterator();

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            int binNumber = new Double(row.getDouble(0)).intValue();
            lowerBoundToCount.put(splits[binNumber], row.getLong(1));
        }

        for (int i=0; i<splits.length-1; i++) {
            long count = 0;
            if (lowerBoundToCount.containsKey(splits[i])) {
                count = lowerBoundToCount.get(splits[i]);
            }
            boolean isUpperBoundIncluded = i == splits.length - 2;
            Bin bin = new Bin(splits[i], splits[i+1], count, isUpperBoundIncluded);
            bins.add(bin);
        }
        return new Histogram(column.getName(), bins);
    }
}
