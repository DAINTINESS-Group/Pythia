package gr.uoi.cs.pythia.valueDistributionStatistics;

import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DistributionsValues.Mode;
import gr.uoi.cs.pythia.model.DistributionsValues.QuartilesProfile;
import gr.uoi.cs.pythia.model.DistributionsValuesProfile;
import gr.uoi.cs.pythia.model.histogram.Histogram;
import gr.uoi.cs.pythia.util.DatatypeFilterer;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.apache.spark.sql.functions.col;
public class SparkValueDistributionsCalculator implements IValueDistributionsTasks {

    private final Dataset<Row> dataset;
    private final List<Mode> listModes;
    private final Column column;
    private QuartilesProfile quartilesProfile;
    private Histogram quartilesHistogram;

    public SparkValueDistributionsCalculator(Dataset<Row> dataset, Column column) {
        this.listModes = new ArrayList<>();
        this.dataset = dataset;
        this.column = column;
    }

    @Override
    public void calculateMode() {
        System.out.println("breakpoint1");

        if (column == null) {
            System.out.println("Column is null");
            return;
        }

        System.out.println("breakpoint2pa");
        String columnName = column.getName();

        try {
            // Group data by the column and count occurrences
            Dataset<Row> groupedData = getGroupedData(columnName);

            // Check if there is any data to process
            if (groupedData != null && groupedData.count() > 0) {
                System.out.println("breakpoint2");

                // Get the maximum occurrence count
                long maxOccurrences = getMaxOccurrences(groupedData);
                System.out.println("breakpoint3");

                // Filter the rows with the maximum occurrence count
                List<Row> modeResult = getModesWithMaxOccurrences(groupedData, maxOccurrences);
                System.out.println("breakpoint4");

                // Populate listModes with Mode objects
                populateModeList(modeResult, maxOccurrences);
                System.out.println("breakpoint5");
            }

            // If no modes were found, print a message
            if (this.listModes.isEmpty()) {
                System.out.println("No data available for mode calculation.");
                return;
            }

            System.out.println("breakpoint6");

        } catch (Exception e) {
            System.err.println("An error occurred during mode calculation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Dataset<Row> getGroupedData(String columnName) {
        try {
            // Check if the column exists in the dataset
            if (dataset.columns() != null && Arrays.asList(dataset.columns()).contains(columnName)) {
                // Handle null or empty values and group by the column
                String wrappedColumnName = "`" + columnName + "`";
                return dataset
                        .filter(col(columnName).isNotNull()
                                .and(functions.expr("trim(" + wrappedColumnName + ")").notEqual("")))
                        .groupBy(columnName)
                        .count();
            } else {
                // Return an empty DataFrame if the column doesn't exist
                System.out.println("Column " + columnName + " does not exist.");
                return SparkSession.builder().getOrCreate().emptyDataFrame();
            }
        } catch (Exception e) {
            System.err.println("An error occurred in getGroupedData: " + e.getMessage());
            e.printStackTrace();
            return SparkSession.builder().getOrCreate().emptyDataFrame();
        }
    }

    private long getMaxOccurrences(Dataset<Row> groupedData) {
        try {
            // Get the maximum count value from grouped data
            return groupedData.agg(functions.max("count").alias("maxCount")).first().getLong(0);
        } catch (Exception e) {
            System.err.println("An error occurred in getMaxOccurrences: " + e.getMessage());
            e.printStackTrace();
            return 0L;
        }
    }

    private List<Row> getModesWithMaxOccurrences(Dataset<Row> groupedData, long maxOccurrences) {
        try {
            // Filter the rows that have the maximum occurrence count
            return groupedData.filter(col("count").equalTo(maxOccurrences)).collectAsList();
        } catch (Exception e) {
            System.err.println("An error occurred in getModesWithMaxOccurrences: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void populateModeList(List<Row> modeResult, long maxOccurrences) {
        try {
            // Create Mode objects from the rows and add them to listModes
            modeResult.forEach(row -> {
                Object modeValue = row.get(0); // The value of the mode
                this.listModes.add(new Mode(maxOccurrences, modeValue));
            });
        } catch (Exception e) {
            System.err.println("An error occurred in populateModeList: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void createQuartilesProfile() {
        if (column == null) {
            System.out.println("Column is null");
            return;
        }

        // Check if the column is numerical
        if (!DatatypeFilterer.isNumerical(column.getDatatype())) {
            DistributionsValuesProfile distributionsValuesProfile = new DistributionsValuesProfile(null, listModes);
            column.setDistributionsValuesProfile(distributionsValuesProfile);
            return;
        }

        // Sort data before calculating quartiles
        Sorting sorter = new Sorting(dataset, column);
        sorter.sort();
        List<Number> sortedList = sorter.getsortedList();

        // Calculate Q1, Q2 (median), and Q3
        Number Q1 = calculateQuartile(1, sortedList);
        Number Q2 = calculateQuartile(2, sortedList);
        Number Q3 = calculateQuartile(3, sortedList);

        // Store the quartiles profile
        this.quartilesProfile = new QuartilesProfile(Q1, Q2, Q3);

        DistributionsValuesProfile distributionsValuesProfile = new DistributionsValuesProfile(quartilesProfile, listModes);
        column.setDistributionsValuesProfile(distributionsValuesProfile);
    }

    private Number calculateQuartile(int quartile, List<Number> data) {
        if (quartile < 1 || quartile > 3 || data == null || data.isEmpty()) {
            System.out.println("Quartile is null or empty");
            return null;
        }

        int n = data.size();

        // If there is only one element, all quartiles are the same
        if (n == 1) {
            return data.get(0);
        }

        // Calculate the requested quartile
        switch (quartile) {
            case 1: // Q1 - First quartile (25%)
                return calculateMedian(getLowerHalf(data));

            case 2: // Q2 - Median (50%)
                return calculateMedian(data);

            case 3: // Q3 - Third quartile (75%)
                return calculateMedian(getUpperHalf(data));

            default:
                throw new IllegalArgumentException("Invalid quartile number.");
        }
    }

    private Number calculateMedian(List<Number> data) {
        int n = data.size();
        if (n % 2 == 0) {
            // If even number of elements, return the average of the two middle elements
            return (data.get(n / 2 - 1).doubleValue() + data.get(n / 2).doubleValue()) / 2.0;
        } else {
            // If odd number of elements, return the middle element
            return data.get(n / 2);
        }
    }

    private static List<Number> getLowerHalf(List<Number> data) {
        int n = data.size();
        return new ArrayList<>(data.subList(0, n / 2));
    }

    private static List<Number> getUpperHalf(List<Number> data) {
        int n = data.size();
        if (n % 2 == 0) {
            return new ArrayList<>(data.subList(n / 2, n));
        } else {
            return new ArrayList<>(data.subList((n / 2 + 1), n));
        }
    }

    @Override
    public void createHistogramForQuartiles() {
        if (!DatatypeFilterer.isNumerical(column.getDatatype())) {
            column.setHistogram(null);
            return;
        }
        QuartilesHistogramGenerator quartilesHistogramGenerator = new QuartilesHistogramGenerator(dataset, column);
        quartilesHistogram = quartilesHistogramGenerator.generateHistogram(5);
        column.setHistogram(quartilesHistogram);
    }

    @Override
    public List<Mode> getListModes(){
        return this.listModes;
    }

    @Override
    public Histogram getHistogram(){
        return this.quartilesHistogram;
    }

}
