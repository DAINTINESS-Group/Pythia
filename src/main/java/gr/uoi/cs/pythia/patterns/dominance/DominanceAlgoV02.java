package gr.uoi.cs.pythia.patterns.dominance;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import gr.uoi.cs.pythia.model.dominance.DominanceResult;

/*
    V02: Includes optimization in version V01, also optimized such that both high and low dominance
    checks are performed in a single algorithm execution.
 */
public class DominanceAlgoV02 implements IDominanceAlgo {

    private final Logger logger = Logger.getLogger(DominanceAlgoV02.class);

    private static final String TOTAL = "total";
    private static final String PARTIAL = "partial";
    private static final String EMPTY = "-";

    private static final double TOTAL_DOMINANCE_THRESHOLD = 100.0;
    private static final double PARTIAL_DOMINANCE_THRESHOLD = 75.0;
    private static final int TOP_K_FILTERING_AMOUNT = 6;

    private final Dataset<Row> dataset;

    private final IDominanceComparator highComparator;
    private final IDominanceComparator lowComparator;

    private DominanceResult highDominanceResult;
    private DominanceResult lowDominanceResult;

    public DominanceAlgoV02(
            Dataset<Row> dataset,
            IDominanceComparator highComparator,
            IDominanceComparator lowComparator) {
        this.dataset = dataset;
        this.highComparator = highComparator;
        this.lowComparator = lowComparator;
    }

    private boolean hasHighComparator() {
        return highComparator != null;
    }


    private boolean hasLowComparator() {
        return lowComparator != null;
    }

    @Override
    public Map<String, DominanceResult> identifySingleCoordinateDominance(String measurement, String xCoordinate) {
        Instant start;
        Duration duration;

        // Step 0.
        initializeResultObjects(measurement, xCoordinate);

        // Step 1. Run aggregate measurement query on the dataset
        start = Instant.now();
        List<Row> queryResult = runAggregateQuery(measurement, xCoordinate);
        duration = Duration.between(start, Instant.now());
        logger.info(String.format("Duration of single-coordinate dominance "
                        + "runAggregateQuery for measurement '%s' and coordinate '%s': %s / %sms",
                measurement, xCoordinate, duration, duration.toMillis()));

        // Step 2. Check for dominance
        start = Instant.now();
        executeSingleCoordinateDominanceAlgo(queryResult);
        duration = Duration.between(start, Instant.now());
        logger.info(String.format("Duration of single-coordinate dominance algorithm: %s / %sms / %sns",
                duration, duration.toMillis(), duration.toNanos()));

        // Step 3. Filter top-k
        start = Instant.now();
        filterTopK();
        duration = Duration.between(start, Instant.now());
        logger.info(String.format("Duration of single-coordinate dominance top-K filtering: %s / %sms / %sns\n",
                duration, duration.toMillis(), duration.toNanos()));

        // Step 4. Return a HashMap with highDominanceResult & lowDominanceResult objects
        Map<String, DominanceResult> results = new HashMap<>();
        if (hasHighComparator()) results.put(highComparator.getDominanceType(), highDominanceResult);
        if (hasLowComparator()) results.put(lowComparator.getDominanceType(), lowDominanceResult);
        return results;
    }

    @Override
    public Map<String, DominanceResult> identifyDoubleCoordinateDominance(
            String measurement,
            String xCoordinate,
            String yCoordinate) {
        Instant start;
        Duration duration;

        // Step 1. Run aggregate measurement query on the dataset
        start = Instant.now();
        List<Row> queryResult = runAggregateQuery(measurement, xCoordinate, yCoordinate);
        duration = Duration.between(start, Instant.now());
        logger.info(String.format("Duration of double-coordinate dominance "
                        + "runAggregateQuery for measurement '%s' "
                        + "and coordinates '%s', '%s': %s / %sms",
                measurement, xCoordinate, yCoordinate, duration, duration.toMillis()));

        // Add queryResult to result objects such that it is included in the generated report
        initializeResultObjects(measurement, xCoordinate, yCoordinate, queryResult);

        // Step 2. Find the distinct values of X and Y coordinates
        start = Instant.now();
        Map<String, List<String>> coordinatesFromQueryResult = getDistinctValuesFromQueryResult(queryResult);
        List<String> xCoordinates = coordinatesFromQueryResult.get("x");
        List<String> yCoordinates = coordinatesFromQueryResult.get("y");
        duration = Duration.between(start, Instant.now());
        logger.info(String.format("Duration of double-coordinate dominance "
                        + "getDistinctValuesFromQueryResult method for coordinates '%s', '%s': %s / %sms",
                xCoordinate, yCoordinate, duration, duration.toMillis()));

        // Step. 3 Check for dominance
        start = Instant.now();
        executeDoubleCoordinateDominanceAlgo(queryResult, xCoordinates, yCoordinates);
        duration = Duration.between(start, Instant.now());
        logger.info(String.format("Duration of double-coordinate dominance algorithm: %s / %sms / %sns",
                duration, duration.toMillis(), duration.toNanos()));

        // Step 4. Filter top-k
        start = Instant.now();
        filterTopK();
        duration = Duration.between(start, Instant.now());
        logger.info(String.format("Duration of double-coordinate dominance top-K filtering: %s / %sms / %sns\n",
                duration, duration.toMillis(), duration.toNanos()));

        // Step 4. Return a HashMap with highDominanceResult & lowDominanceResult objects
        Map<String, DominanceResult> results = new HashMap<>();
        if (hasHighComparator()) results.put(highComparator.getDominanceType(), highDominanceResult);
        if (hasLowComparator()) results.put(lowComparator.getDominanceType(), lowDominanceResult);
        return results;
    }

    // This method actually performs the check for dominance with 1 coordinate.
    // Identified results are added to the high & low results lists respectively.
    // Finally, identified results are sorted and filtered based on dominance percentage score.
    private void executeSingleCoordinateDominanceAlgo(List<Row> queryResult) {
        if (queryResult.size() <= 1) return;

        for (Row rowA : queryResult) {
            String xCoordinate = parseCoordinateValue(rowA, 0);
            double aggValueA = parseAggregateValue(rowA);
            if (xCoordinate.isEmpty()) continue;
            if (Double.isNaN(aggValueA)) continue;
            int highDominatedValues = 0;
            int lowDominatedValues = 0;
            for (Row rowB : queryResult) {
                double aggValueB = parseAggregateValue(rowB);
                if (Double.isNaN(aggValueB)) continue;
                if (isSameRow(rowA, rowB)) continue;
                if (hasHighComparator()) {
                    if (highComparator.isDominant(aggValueA, aggValueB)) highDominatedValues++;
                }
                if (hasLowComparator()) {
                    if (lowComparator.isDominant(aggValueA, aggValueB)) lowDominatedValues++;
                }
            }

            if (hasHighComparator()) {
                double highDominancePercentage = (double) highDominatedValues
                        / (double) (queryResult.size() - 1) * 100;
                String highDominanceType = determineDominanceType(
                        highDominancePercentage, highComparator.getDominanceType());

                highDominanceResult.addIdentificationResult(
                        xCoordinate,
                        aggValueA,
                        highDominancePercentage,
                        isDominance(highDominanceType),
                        highDominanceType);
            }

            if (hasLowComparator()) {
                double lowDominancePercentage = (double) lowDominatedValues
                        / (double) (queryResult.size() - 1) * 100;
                String lowDominanceType = determineDominanceType(
                        lowDominancePercentage, lowComparator.getDominanceType());

                lowDominanceResult.addIdentificationResult(
                        xCoordinate,
                        aggValueA,
                        lowDominancePercentage,
                        isDominance(lowDominanceType),
                        lowDominanceType);
            }
        }
    }

    // This method actually performs the check for dominance with 2 coordinates.
    // Identified results are added to the high & low results lists respectively.
    // Finally, identified results are sorted and filtered based on dominance percentage score.
    private void executeDoubleCoordinateDominanceAlgo(
            List<Row> queryResult,
            List<String> xCoordinates,
            List<String> yCoordinates) {
        if (queryResult.size() <= 1) return;

        for (String xCoordinateA : xCoordinates) {
            List<String> highDominatedXValues = new ArrayList<>();
            List<String> lowDominatedXValues = new ArrayList<>();
            HashMap<String, List<String>> onYValuesHigh = new HashMap<>();
            HashMap<String, List<String>> onYValuesLow = new HashMap<>();
            for (String xCoordinateB : xCoordinates) {
                if (xCoordinateA.equals(xCoordinateB)) continue;
                List<String> onYValuesHighForCurrentXCoordinate = new ArrayList<>();
                List<String> onYValuesLowForCurrentXCoordinate = new ArrayList<>();
                boolean shouldCheckHighDominance = hasHighComparator();
                boolean shouldCheckLowDominance = hasLowComparator();
                boolean isADominatesHighB = false;
                boolean isADominatesLowB = false;
                for (String yCoordinate : yCoordinates) {
                    if (!shouldCheckHighDominance && !shouldCheckLowDominance) break;
                    double aggValueA = getAggValue(xCoordinateA, yCoordinate, queryResult);
                    double aggValueB = getAggValue(xCoordinateB, yCoordinate, queryResult);
                    if (Double.isNaN(aggValueA)) continue;
                    if (Double.isNaN(aggValueB)) continue;
                    if (shouldCheckHighDominance) {
                        if (highComparator.isDominant(aggValueA, aggValueB)) {
                            onYValuesHighForCurrentXCoordinate.add(yCoordinate);
                            isADominatesHighB = true;
                        } else {
                            isADominatesHighB = false;
                            shouldCheckHighDominance = false;
                        }
                    }
                    if (shouldCheckLowDominance) {
                        if (lowComparator.isDominant(aggValueA, aggValueB)) {
                            onYValuesLowForCurrentXCoordinate.add(yCoordinate);
                            isADominatesLowB = true;
                        } else {
                            isADominatesLowB = false;
                            shouldCheckLowDominance = false;
                        }
                    }
                }

                if (hasHighComparator() && isADominatesHighB) {
                    highDominatedXValues.add(xCoordinateB);
                    onYValuesHigh.put(xCoordinateB, onYValuesHighForCurrentXCoordinate);
                }
                if (hasLowComparator() && isADominatesLowB) {
                    lowDominatedXValues.add(xCoordinateB);
                    onYValuesLow.put(xCoordinateB, onYValuesLowForCurrentXCoordinate);
                }
            }

            if (hasHighComparator()) {
                double highDominancePercentage = (double) highDominatedXValues.size()
                        / (double) (xCoordinates.size() - 1) * 100;
                String highDominanceType = determineDominanceType(
                        highDominancePercentage, highComparator.getDominanceType());

                highDominanceResult.addIdentificationResult(
                        xCoordinateA,
                        highDominatedXValues,
                        onYValuesHigh,
                        highDominancePercentage,
                        isDominance(highDominanceType),
                        highDominanceType,
                        calculateAggValuesMarginalSum(xCoordinateA, queryResult));
            }

            if (hasLowComparator()) {
                double lowDominancePercentage = (double) lowDominatedXValues.size()
                        / (double) (xCoordinates.size() - 1) * 100;
                String lowDominanceType = determineDominanceType(
                        lowDominancePercentage, lowComparator.getDominanceType());

                lowDominanceResult.addIdentificationResult(
                        xCoordinateA,
                        lowDominatedXValues,
                        onYValuesLow,
                        lowDominancePercentage,
                        isDominance(lowDominanceType),
                        lowDominanceType,
                        calculateAggValuesMarginalSum(xCoordinateA, queryResult));
            }
        }
    }

    private void initializeResultObjects(String measurement, String xCoordinate) {
        if (hasHighComparator()) {
            highDominanceResult = new DominanceResult(
                    highComparator.getDominanceType(),
                    "sum",
                    measurement, xCoordinate);
        }
        if (hasLowComparator()) {
            lowDominanceResult = new DominanceResult(
                    lowComparator.getDominanceType(),
                    "sum",
                    measurement, xCoordinate);
        }
    }

    private void initializeResultObjects(
            String measurement,
            String xCoordinate,
            String yCoordinate,
            List<Row> queryResult) {
        if (hasHighComparator()) {
            highDominanceResult = new DominanceResult(
                    highComparator.getDominanceType(), "sum",
                    measurement, xCoordinate, yCoordinate, queryResult);
        }
        if (hasLowComparator()) {
            lowDominanceResult = new DominanceResult(
                    lowComparator.getDominanceType(), "sum",
                    measurement, xCoordinate, yCoordinate, queryResult);
        }
    }

    private void filterTopK() {
        if (hasHighComparator()) {
            sortDescendingIdentificationResults(highDominanceResult);
            filterTopKIdentificationResults(highDominanceResult);
        }
        if (hasLowComparator()) {
            sortDescendingIdentificationResults(lowDominanceResult);
            filterTopKIdentificationResults(lowDominanceResult);
        }
    }

    // Sort the latest identification results in descending order based on dominance percentage.
    private void sortDescendingIdentificationResults(DominanceResult dominanceResult) {
        dominanceResult.getIdentificationResults().sort((row1, row2) -> {
            double row1Score, row2Score;
            try {
                int domPercentageIndex = dominanceResult.getNumOfCoordinates() + 1;
                row1Score = Double.parseDouble(row1.get(domPercentageIndex).toString());
                row2Score = Double.parseDouble(row2.get(domPercentageIndex).toString());
            } catch (Exception e) {
                return 0;
            }
            if (row1Score == row2Score) return 0;
            return row1Score < row2Score ? 1 : -1;
        });
    }

    private void filterTopKIdentificationResults(DominanceResult dominanceResult) {
        List<Row> identificationResults = dominanceResult.getIdentificationResults();
        if (identificationResults.size() - 1 <= TOP_K_FILTERING_AMOUNT) return;
        identificationResults.removeIf(row ->
                identificationResults.indexOf(row) != 0 &&
                        identificationResults.indexOf(row) > TOP_K_FILTERING_AMOUNT);
    }

    private double getAggValue(String xCoordinate, String yCoordinate, List<Row> queryResult) {
        for (Row row : queryResult) {
            String currentXCoordinate = row.getString(0);
            String currentYCoordinate = row.getString(1);
            if (xCoordinate.equals(currentXCoordinate) &&
                    yCoordinate.equals(currentYCoordinate)) {
                return parseAggregateValue(row);
            }
        }
        return Double.NaN;
    }

    private double calculateAggValuesMarginalSum(String xCoordinate, List<Row> queryResult) {
        double aggValuesMarginalSum = 0;
        for (Row row : queryResult) {
            String currentXCoordinate = row.getString(0);
            if (xCoordinate.equals(currentXCoordinate)) {
                double aggValue = parseAggregateValue(row);
                if (Double.isNaN(aggValue)) continue;
                aggValuesMarginalSum += aggValue;
            }
        }
        return aggValuesMarginalSum;
    }

    private double parseAggregateValue(Row row) {
        if (row.get(row.length() - 1) == null) return Double.NaN;
        return Double.parseDouble(row.get(row.length() - 1).toString());
    }

    private String parseCoordinateValue(Row row, int indexOfCoordinate) {
        if (row.get(indexOfCoordinate) == null) return "";
        return row.get(indexOfCoordinate).toString();
    }

    private boolean isSameRow(Row rowA, Row rowB) {
        return rowA == rowB;
    }

    private boolean isDominance(String dominanceType) {
        return dominanceType != EMPTY;
    }

    // TODO is it ok to use collectAsList here?
    // Is it likely that the query returns a dataset that doesn't fit on main
    // memory for very large input datasets?
    private List<Row> runAggregateQuery(String measurement, String xCoordinate) {
        return dataset
                .groupBy(xCoordinate)
                .sum(measurement)
                .collectAsList();
    }

    // TODO is it ok to use collectAsList here?
    // Is it likely that the query returns a dataset that doesn't fit on main
    // memory for very large input datasets?
    private List<Row> runAggregateQuery(String measurement, String xCoordinate, String yCoordinate) {
        return dataset
                .groupBy(xCoordinate, yCoordinate)
                .sum(measurement)
                .orderBy(xCoordinate, yCoordinate)
                .collectAsList();
    }

    private Map<String, List<String>> getDistinctValuesFromQueryResult(List<Row> queryResult) {
        Map<String, List<String>> coordinates = new HashMap<>();
        List<String> xCoordinates = new ArrayList<>();
        List<String> yCoordinates = new ArrayList<>();

        for (Row row : queryResult) {
            if (row.get(0) != null) {
                String xCoordinateValue = row.get(0).toString();
                if (!xCoordinates.contains(xCoordinateValue)) xCoordinates.add(xCoordinateValue);
            }
            if (row.get(1) != null) {
                String yCoordinateValue = row.get(1).toString();
                if (!yCoordinates.contains(yCoordinateValue)) yCoordinates.add(yCoordinateValue);
            }
        }
        coordinates.put("x", xCoordinates);
        coordinates.put("y", yCoordinates);
        return coordinates;
    }

    // This method checks if the given dominance percentages
    // satisfy the partial or total thresholds and returns a string
    // that describes the type of the dominance feature.
    private String determineDominanceType(double dominancePercentage, String dominanceType) {
        if (dominancePercentage >= TOTAL_DOMINANCE_THRESHOLD) {
            return String.format("%s %s", TOTAL, dominanceType);
        }
        if (dominancePercentage >= PARTIAL_DOMINANCE_THRESHOLD) {
            return String.format("%s %s", PARTIAL, dominanceType);
        }
        return EMPTY;
    }

    private void debugPrintList(String title, List<Row> list)  {
        String str = title;
        for (Row row : list) {
            for (int i = 0; i < row.length(); i++) {
                if (row.get(i) == null) continue;
                str += row.get(i).toString() + "\t";
            }
            str += "\n";
        }
        System.out.println(str);
    }

}
