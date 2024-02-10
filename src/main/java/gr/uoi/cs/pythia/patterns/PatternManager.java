package gr.uoi.cs.pythia.patterns;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gr.uoi.cs.pythia.patterns.dominance.*;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.dominance.DominanceResult;
import gr.uoi.cs.pythia.model.outlier.OutlierResult;
import gr.uoi.cs.pythia.model.outlier.OutlierType;
import gr.uoi.cs.pythia.patterns.outlier.IOutlierAlgo;
import gr.uoi.cs.pythia.patterns.outlier.OutlierAlgoFactory;

import static gr.uoi.cs.pythia.patterns.dominance.DominanceAlgoFactory.DominanceAlgoVersion.V02_HIGH_AND_LOW;

public class PatternManager implements IPatternManager {

    private final Logger logger = Logger.getLogger(PatternManager.class);
    private final Dataset<Row> dataset;
    private final DatasetProfile datasetProfile;
    private final DominanceParameters dominanceParameters;
    private final OutlierType outlierType;
    private final IDominanceAlgo dominanceAlgo;
    private final IOutlierAlgo outlierAlgo;

    private final List<DominanceResult> highDominanceResults;
    private final List<DominanceResult> lowDominanceResults;

    public PatternManager(
            Dataset<Row> dataset,
            DatasetProfile datasetProfile,
            DominanceParameters dominanceParameters,
            OutlierType outlierType,
            double outlierThreshold) {
        this.dataset = dataset;
        this.datasetProfile = datasetProfile;
        this.dominanceParameters = dominanceParameters;
        this.outlierType = outlierType;
        outlierAlgo = new OutlierAlgoFactory().createOutlierAlgo(outlierType, outlierThreshold);

        // Default to the latest optimized dominance algo version
        DominanceAlgoFactory factory = new DominanceAlgoFactory();
        dominanceAlgo = factory.generateDominanceAlgo(V02_HIGH_AND_LOW, dataset);

        // Initialize lists of result objects
        highDominanceResults = new ArrayList<>();
        lowDominanceResults = new ArrayList<>();

    }

    @Override
    public void identifyOutliers() {
        List<OutlierResult> outlierResults = outlierAlgo.identifyOutliers(dataset, datasetProfile);

        datasetProfile.getPatternsProfile().setOutlierResults(outlierResults);

        datasetProfile.getPatternsProfile().setOutlierType(outlierAlgo.getOutlierType().replace("_", " "));

        logger.info(String.format(
                "Identified outliers using the \"%s\" outlier type for dataset: '%s'",
                outlierType, datasetProfile.getAlias()));
    }

    @Override
    public void identifyDominance() {
        Instant start;
        Duration duration;

        // Select the measurement & coordinate columns for dominance identification
        start = Instant.now();
        DominanceColumnSelector columnSelector = new DominanceColumnSelector(dominanceParameters);
        List<String> measurements = columnSelector.selectMeasurementColumns(datasetProfile);
        List<String> coordinates = columnSelector.selectCoordinateColumns(datasetProfile, dataset);
        duration = Duration.between(start, Instant.now());
        logger.info(String.format("Duration of dominance column selection: %s / %sms", duration, duration.toMillis()));

        // Highlight identification can not proceed with no measurement/coordinate
        if (measurements.isEmpty()) return;
        if (coordinates.isEmpty()) return;

        // Pass all the measurement & coordinate column combinations through
        // low & high dominance algorithms for one & two coordinates respectively.
        identifySingleCoordinateDominance(measurements, coordinates);
        identifyDoubleCoordinateDominance(measurements, coordinates);

        // Add results to patterns profile
        datasetProfile.getPatternsProfile().setHighDominanceResults(highDominanceResults);
        datasetProfile.getPatternsProfile().setLowDominanceResults(lowDominanceResults);

        logger.info(String.format("Identified dominance for dataset: '%s'", datasetProfile.getAlias()));
    }

    private void identifySingleCoordinateDominance(List<String> measurements, List<String> coordinates) {
        Instant start = Instant.now();

        for (String measurement : measurements) {
            for (String xCoordinate : coordinates) {
                Map<String, DominanceResult> results;
                results = dominanceAlgo.identifySingleCoordinateDominance(measurement, xCoordinate);
                highDominanceResults.add(results.get("high"));
                lowDominanceResults.add(results.get("low"));
            }
        }

        Duration duration = Duration.between(start, Instant.now());
        logger.info(String.format("Duration of identifySingleCoordinateDominance: %s / %sms",
                duration, duration.toMillis()));
    }

    private void identifyDoubleCoordinateDominance(List<String> measurements, List<String> coordinates) {
        Instant start = Instant.now();

        for (String measurement : measurements) {
            for (String xCoordinate : coordinates) {
                for (String yCoordinate : coordinates) {
                    if (xCoordinate.equals(yCoordinate)) continue;
                    Map<String, DominanceResult> results;
                    results = dominanceAlgo.identifyDoubleCoordinateDominance(measurement, xCoordinate, yCoordinate);
                    highDominanceResults.add(results.get("high"));
                    lowDominanceResults.add(results.get("low"));
                }
            }
        }

        Duration duration = Duration.between(start, Instant.now());
        logger.info(String.format("Duration of identifyDoubleCoordinateDominance: %s / %sms",
                duration, duration.toMillis()));
    }
}
