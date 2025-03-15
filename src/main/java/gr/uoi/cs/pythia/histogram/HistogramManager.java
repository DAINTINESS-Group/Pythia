package gr.uoi.cs.pythia.histogram;

import gr.uoi.cs.pythia.histogram.generator.*;
import gr.uoi.cs.pythia.model.histogram.Histogram;
import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.util.DatatypeFilterer;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HistogramManager {
    private final DatasetProfile datasetProfile;
    private final Dataset<Row> dataset;
    private HistogramParameters histogramParameters;

    public HistogramManager(DatasetProfile datasetProfile, Dataset<Row> dataset,HistogramParameters histogramParameters) {
        this.datasetProfile = datasetProfile;
        this.dataset = dataset;
        this.histogramParameters = histogramParameters;
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
                    .createGenerator(dataset, column, histogramParameters.getType());
            Histogram histogram = histogramGenerator.generateHistogram(histogramParameters.getNumberOfBins());
            column.setHistogram(histogram);
            histograms.add(histogram);

            QuartilesHistogramGenerator quartilesHistogramGenerator =  new QuartilesHistogramGenerator(dataset,column);
            Histogram quartileHistogram = quartilesHistogramGenerator.generateHistogram(10); //Bins not used
            column.setQuartilesHistogram(quartileHistogram);
            histograms.add(quartileHistogram);
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

    @SuppressWarnings("unused")
	private void createDirectory(Path path) throws IOException {
        Files.createDirectories(path);
    }
}
