package gr.uoi.cs.pythia.histogram;

import gr.uoi.cs.pythia.histogram.generator.HistogramGeneratorFactory;
import gr.uoi.cs.pythia.histogram.generator.HistogramGeneratorType;
import gr.uoi.cs.pythia.histogram.generator.IHistogramGenerator;
import gr.uoi.cs.pythia.model.histogram.Histogram;
import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.util.DatatypeFilterer;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HistogramManager {
    private final DatasetProfile datasetProfile;
    private final Dataset<Row> dataset;

    public HistogramManager(DatasetProfile datasetProfile, Dataset<Row> dataset) {
        this.datasetProfile = datasetProfile;
        this.dataset = dataset;
    }

    public List<Histogram> createAllHistograms() throws IOException {
        List<Column> columns = getNumericalColumns();
        if (columns.isEmpty()) {
            return new ArrayList<>();
        }
        // for visualization
//        Path outputDirectory = Paths.get(datasetProfile.getOutputDirectory(), "histograms");
//        createDirectory(outputDirectory);

        HistogramGeneratorFactory histogramGeneratorFactory = new HistogramGeneratorFactory();
        List<Histogram> histograms = new ArrayList<>();

        for (Column column : columns) {
            IHistogramGenerator histogramGenerator = histogramGeneratorFactory
                    .createGenerator(dataset, column, HistogramGeneratorType.KEEP_NANS);
            Histogram histogram = histogramGenerator.generateHistogram(10);
            column.setHistogram(histogram);
            histograms.add(histogram);
            // for visualization
//            createDirectory(Paths.get(outputDirectory.toString(), column.getName()));
            // histogramVisualizer etc...
        }
        return histograms;
    }

    private List<Column> getNumericalColumns() {
        return datasetProfile.getColumns().stream()
                .filter(column -> DatatypeFilterer.isNumerical(column.getDatatype()))
                .collect(Collectors.toList());
    }

    private void createDirectory(Path path) throws IOException {
        Files.createDirectories(path);
    }
}
