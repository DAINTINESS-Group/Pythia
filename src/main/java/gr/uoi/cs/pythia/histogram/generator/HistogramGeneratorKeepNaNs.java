package gr.uoi.cs.pythia.histogram.generator;

import gr.uoi.cs.pythia.histogram.model.Bin;
import gr.uoi.cs.pythia.histogram.model.Histogram;
import gr.uoi.cs.pythia.model.Column;
import org.apache.spark.ml.feature.Bucketizer;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Keeps null and NaN values and counts them to an extra bin.
 */
public class HistogramGeneratorKeepNaNs {

    private final Dataset<Row> dataset;
    private final Column column;

    public HistogramGeneratorKeepNaNs(Dataset<Row> dataset, Column column) {
        this.dataset = dataset;
        this.column = column;
    }

    public Histogram generateHistogram(int bins) {
        double[] splits = getSplits(bins);
        Dataset<Row> binsToCounts = getBinsToCounts(splits);
        return createHistogram(binsToCounts, splits);
    }

    private double[] getSplits(int totalBins) {
        double max = Double.parseDouble(column.getDescriptiveStatisticsProfile().getMax());
        double min = Double.parseDouble(column.getDescriptiveStatisticsProfile().getMin());
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
                .setHandleInvalid("keep")
                .setSplits(splits);

        return bucketizer.transform(dataset.select(column.getName()))
                .groupBy("bin")
                .count()
                .orderBy("bin");
    }

    private Histogram createHistogram(Dataset<Row> binsToCounts, double[] splits) {
        Histogram histogram = new Histogram(column.getName());
        Map<Double, Long> lowerBoundToCount = new HashMap<>();
        Iterator<Row> rowIterator = binsToCounts.toLocalIterator();

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (row.isNullAt(0)) {
                histogram.addBin(new Bin(Double.NaN,Double.NaN,row.getLong(1), true));
                continue;
            }
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
            histogram.addBin(bin);
        }
        return histogram;
    }
}
