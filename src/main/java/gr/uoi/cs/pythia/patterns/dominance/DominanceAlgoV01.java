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
    V01: Optimized such that distinct coordinate values in double coordinate dominance
    are not fetched via query, but rather via the aggregate query result.
    (see: getDistinctValuesFromQueryResult method)
 */
@Deprecated
public class DominanceAlgoV01 implements IDominanceAlgo {

    private final Logger logger = Logger.getLogger(DominanceAlgoV01.class);

    private static final String TOTAL = "total";
    private static final String PARTIAL = "partial";
    private static final String EMPTY = "-";

    private static final double TOTAL_DOMINANCE_THRESHOLD = 100.0;
    private static final double PARTIAL_DOMINANCE_THRESHOLD = 75.0;
    private static final int TOP_K_FILTERING_AMOUNT = 6;

    private final Dataset<Row> dataset;

    private final IDominanceComparator comparator;

    public DominanceAlgoV01(Dataset<Row> dataset, IDominanceComparator comparator) {
        this.dataset = dataset;
        this.comparator = comparator;
    }

    @Override
    public Map<String, DominanceResult> identifySingleCoordinateDominance(
            String measurement,
            String xCoordinate) {
        Instant start = Instant.now();

        // Run aggregate measurement query on the dataset
        List<Row> queryResult = runAggregateQuery(dataset, measurement, xCoordinate);

        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        logger.info(String.format("Duration of single-coordinate %s dominance "
                        + "runAggregateQuery for measurement '%s' and coordinate '%s': %s / %sms",
                comparator.getDominanceType(), measurement, xCoordinate,
                duration, duration.toMillis()));

        // Initialize a dominance result object
        DominanceResult dominanceResult = new DominanceResult(
                comparator.getDominanceType(), "sum",
                measurement, xCoordinate);

        // Check for dominance
        executeDominanceAlgoWithOneCoordinate(queryResult, dominanceResult);

        // Return a HashMap with the dominanceResult object
        Map<String, DominanceResult> results = new HashMap<>();
        results.put(comparator.getDominanceType(), dominanceResult);
        return results;
    }

    @Override
    public Map<String, DominanceResult> identifyDoubleCoordinateDominance(
            String measurement,
            String xCoordinate,
            String yCoordinate) {

        Instant start = Instant.now();

        // Run aggregate measurement query on the dataset
        List<Row> queryResult = runAggregateQuery(dataset, measurement,
                xCoordinate, yCoordinate);

        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        logger.info(String.format("Duration of double-coordinate %s dominance "
                        + "runAggregateQuery for measurement '%s' "
                        + "and coordinates '%s', '%s': %s / %sms",
                comparator.getDominanceType(), measurement, xCoordinate, yCoordinate,
                duration, duration.toMillis()));

        start = Instant.now();

        // Find the distinct values of X and Y coordinates
        Map<String, List<String>> coordinatesFromQueryResult = getDistinctValuesFromQueryResult(queryResult);
        List<String> xCoordinates = coordinatesFromQueryResult.get("x");
        List<String> yCoordinates = coordinatesFromQueryResult.get("y");

        end = Instant.now();
        duration = Duration.between(start, end);
        logger.info(String.format("Duration of double-coordinate %s dominance "
                        + "getDistinctValuesFromQueryResult method for coordinates '%s', '%s': %s / %sms",
                comparator.getDominanceType(), xCoordinate,
                yCoordinate, duration, duration.toMillis()));

        // Initialize a dominance result object
        DominanceResult dominanceResult = new DominanceResult(
                comparator.getDominanceType(), "sum",
                measurement, xCoordinate, yCoordinate,
                queryResult);

        // Check for dominance
        executeDominanceAlgoWithTwoCoordinates(
                queryResult, xCoordinates, yCoordinates, dominanceResult);

        // Return a HashMap with the dominanceResult object
        Map<String, DominanceResult> results = new HashMap<>();
        results.put(comparator.getDominanceType(), dominanceResult);
        return results;
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

    // This method actually performs the check for dominance with 1 coordinate.
    // Identified results are added to the results list.
    // Finally, identified results are sorted and filtered based on dominance percentage score.
    private void executeDominanceAlgoWithOneCoordinate(
            List<Row> queryResult, DominanceResult dominanceResult) {
        if (queryResult.size() <= 1) return;

        Instant start = Instant.now();

        for (Row rowA : queryResult) {
            String xCoordinate = parseCoordinateValue(rowA, 0);
            double aggValueA = parseAggregateValue(rowA);
            if (xCoordinate.isEmpty()) continue;
            if (Double.isNaN(aggValueA)) continue;
            int dominatedValues = 0;
            for (Row rowB : queryResult) {
                double aggValueB = parseAggregateValue(rowB);
                if (Double.isNaN(aggValueB)) continue;
                if (isSameRow(rowA, rowB)) continue;
                if (comparator.isDominant(aggValueA, aggValueB)) dominatedValues++;
            }

            double dominancePercentage = (double) dominatedValues
                    / (double) (queryResult.size() - 1) * 100;
            String dominanceType = determineDominanceType(
                    dominancePercentage, comparator.getDominanceType());

            dominanceResult.addIdentificationResult(
                    xCoordinate,
                    aggValueA,
                    dominancePercentage,
                    isDominance(dominanceType),
                    dominanceType);
        }
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        logger.info(String.format("Duration of single-coordinate %s dominance algorithm check:"
                        + " %s / %sms / %sns",
                comparator.getDominanceType(), duration, duration.toMillis(), duration.toNanos()));

        start = Instant.now();

        sortDescendingIdentificationResults(dominanceResult);
        filterTopKIdentificationResults(dominanceResult);

        end = Instant.now();
        duration = Duration.between(start, end);
        logger.info(String.format("Duration of single-coordinate %s dominance top-K filtering:"
                        + " %s / %sms / %sns\n",
                comparator.getDominanceType(), duration, duration.toMillis(), duration.toNanos()));
    }

    // This method actually performs the check for dominance with 2 coordinates.
    // Identified results are added to the results list.
    // Finally, identified results are sorted and filtered based on dominance percentage score.
    private void executeDominanceAlgoWithTwoCoordinates(
            List<Row> queryResult,
            List<String> xCoordinates,
            List<String> yCoordinates,
            DominanceResult dominanceResult) {
        if (queryResult.size() <= 1) return;

        Instant start = Instant.now();

        for (String xCoordinateA : xCoordinates) {
            List<String> dominatedXValues = new ArrayList<>();
            HashMap<String, List<String>> onYValues = new HashMap<>();
            for (String xCoordinateB : xCoordinates) {
                if (xCoordinateA.equals(xCoordinateB)) continue;
                List<String> onYValuesForCurrentXCoordinate = new ArrayList<>();
                boolean isADominatesB = false;
                for (String yCoordinate : yCoordinates) {
                    double aggValueA = getAggValue(xCoordinateA, yCoordinate, queryResult);
                    double aggValueB = getAggValue(xCoordinateB, yCoordinate, queryResult);
                    if (Double.isNaN(aggValueA)) continue;
                    if (Double.isNaN(aggValueB)) continue;
                    if (comparator.isDominant(aggValueA, aggValueB)) {
                        onYValuesForCurrentXCoordinate.add(yCoordinate);
                        isADominatesB = true;
                    } else {
                        isADominatesB = false;
                        break;
                    }
                }
                if (isADominatesB) {
                    dominatedXValues.add(xCoordinateB);
                    onYValues.put(xCoordinateB, onYValuesForCurrentXCoordinate);
                }
            }
            double dominancePercentage = (double) dominatedXValues.size()
                    / (double) (xCoordinates.size() - 1) * 100;
            String dominanceType = determineDominanceType(
                    dominancePercentage, comparator.getDominanceType());

            dominanceResult.addIdentificationResult(
                    xCoordinateA,
                    dominatedXValues,
                    onYValues,
                    dominancePercentage,
                    isDominance(dominanceType),
                    dominanceType,
                    calculateAggValuesMarginalSum(xCoordinateA, queryResult));
        }

        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        logger.info(String.format("Duration of double-coordinate %s dominance algorithm check: "
                        + "%s / %sms / %sns",
                comparator.getDominanceType(), duration, duration.toMillis(), duration.toNanos()));

        start = Instant.now();

        sortDescendingIdentificationResults(dominanceResult);
        filterTopKIdentificationResults(dominanceResult);

        end = Instant.now();
        duration = Duration.between(start, end);
        logger.info(String.format("Duration of double-coordinate %s dominance top-K filtering: "
                        + "%s / %sms / %sns\n",
                comparator.getDominanceType(), duration, duration.toMillis(), duration.toNanos()));
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

    private double getAggValue(String xCoordinate, String yCoordinate,
                               List<Row> queryResult) {
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
    private List<Row> runAggregateQuery(
            Dataset<Row> dataset,
            String measurementColName,
            String xCoordinateColName) {
        return dataset
                .groupBy(xCoordinateColName)
                .sum(measurementColName)
                .collectAsList();
    }

    // TODO is it ok to use collectAsList here?
    // Is it likely that the query returns a dataset that doesn't fit on main
    // memory for very large input datasets?
    private List<Row> runAggregateQuery(
            Dataset<Row> dataset,
            String measurementColName,
            String xCoordinateColName,
            String yCoordinateColName) {
        return dataset
                .groupBy(xCoordinateColName, yCoordinateColName)
                .sum(measurementColName)
                .orderBy(xCoordinateColName, yCoordinateColName)
                .collectAsList();
    }

    private String parseStringValue(Object object) {
        if (object == null) return "";
        return object.toString();
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
