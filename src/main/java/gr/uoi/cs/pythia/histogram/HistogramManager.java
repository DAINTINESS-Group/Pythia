package gr.uoi.cs.pythia.histogram;

import gr.uoi.cs.pythia.histogram.generator.HistogramGeneratorKeepNaNs;
import gr.uoi.cs.pythia.histogram.model.Histogram;
import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.DataTypes;

import java.util.*;
import java.util.stream.Collectors;

public class HistogramManager {
    private final DatasetProfile datasetProfile;
    private final Dataset<Row> dataset;

    public HistogramManager(DatasetProfile datasetProfile, Dataset<Row> dataset) {
        this.datasetProfile = datasetProfile;
        this.dataset = dataset;
    }

    public List<Histogram> createAllHistograms() {
        List<Column> columns = getNumericalColumns();
        List<Histogram> histograms = new ArrayList<>();

        for (Column column : columns) {
            Histogram histogram = new HistogramGeneratorKeepNaNs(dataset, column)
                    .generateHistogram(10);
            histograms.add(histogram);
        }
        return histograms;
    }

    private List<Column> getNumericalColumns() {
        Set<String> numericDatatypes = new HashSet<>(
                Arrays.asList(
                        DataTypes.ByteType.toString(),
                        DataTypes.ShortType.toString(),
                        DataTypes.IntegerType.toString(),
                        DataTypes.LongType.toString(),
                        DataTypes.FloatType.toString(),
                        DataTypes.DoubleType.toString(),
                        DataTypes.createDecimalType().toString()));

        return datasetProfile.getColumns().stream()
                .filter(column -> numericDatatypes.contains(column.getDatatype()))
                .collect(Collectors.toList());
    }
}
