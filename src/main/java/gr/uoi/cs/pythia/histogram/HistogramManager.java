package gr.uoi.cs.pythia.histogram;

import gr.uoi.cs.pythia.histogram.generator.HistogramGeneratorKeepNaNs;
import gr.uoi.cs.pythia.model.histogram.Histogram;
import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.util.DatatypeFilterer;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

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
        return datasetProfile.getColumns().stream()
                .filter(column -> DatatypeFilterer.isNumerical(column.getDatatype()))
                .collect(Collectors.toList());
    }
}
