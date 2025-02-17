package gr.uoi.cs.pythia.descriptivestatistics;

import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.DescriptiveStatisticsProfile;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.functions;

import java.util.*;
import java.util.stream.Collectors;

public class DescriptiveStatisticsCalculator implements IDescriptiveStatisticsCalculator {

    private final static int NUMBER_MODES_OPTIMIZER = 30;

    public void computeDescriptiveStats(Dataset<Row> dataset, DatasetProfile datasetProfile) {
        Dataset<Row> descriptiveStatistics =
                dataset.summary(
                        DescriptiveStatisticsConstants.COUNT,
                        DescriptiveStatisticsConstants.MEAN,
                        DescriptiveStatisticsConstants.STANDARD_DEVIATION,
                        DescriptiveStatisticsConstants.Q1,
                        DescriptiveStatisticsConstants.MEDIAN,
                        DescriptiveStatisticsConstants.Q3,
                        DescriptiveStatisticsConstants.MIN,
                        DescriptiveStatisticsConstants.MAX);

        List<Column> columns = datasetProfile.getColumns();
        Set<String> summaryColumns = new HashSet<>(Arrays.asList(descriptiveStatistics.columns()));
        for (Column column : columns) {
            if (summaryColumns.contains(column.getName())) {
                List<Row> columnNames = descriptiveStatistics.select(column.getName()).collectAsList();
                List<Object> descriptiveStatisticsRow =
                        columnNames.stream().map(col -> col.get(0)).collect(Collectors.toList());

                String count = (String) descriptiveStatisticsRow.get(0);
                String mean = (String) descriptiveStatisticsRow.get(1);
                String standardDeviation = (String) descriptiveStatisticsRow.get(2);
                String q1 = (String) descriptiveStatisticsRow.get(3);
                String median = (String) descriptiveStatisticsRow.get(4);
                String q3 = (String) descriptiveStatisticsRow.get(5);
                String min = (String) descriptiveStatisticsRow.get(6);
                String max = (String) descriptiveStatisticsRow.get(7);


                List<String> listModes = computeModeValues(dataset,column);
                DescriptiveStatisticsProfile columnDescriptiveStatisticsProfile =
                        new DescriptiveStatisticsProfile(count, mean, standardDeviation,q1, median, q3,min, max,listModes);
                column.setDescriptiveStatisticsProfile(columnDescriptiveStatisticsProfile);
            }

        }
    }

    private List<String> computeModeValues(Dataset<Row> dataset, Column column) {
        // Group by the column, count frequencies, and order by frequency in descending order
        Dataset<Row> frequencyDataset = dataset
                .groupBy(column.getName())
                .count()
                .orderBy(functions.desc("count"));

        // Collect all rows with the highest frequency
        List<Row> modeRows = frequencyDataset.collectAsList();

        // If there's no data, return an empty list
        if (modeRows.isEmpty()) {
            return new ArrayList<>();
        }

        // Get the maximum frequency value
        long maxFrequency = modeRows.get(0).getAs("count");

        // Collect all values with the maximum frequency
        List<String> modes = new ArrayList<>();

        for (Row row : modeRows) {
            // Έλεγχος για null γραμμή
            if (row == null) {
                continue; // Παράλειψη null γραμμών
            }
            // Εξαγωγή της συχνότητας και έλεγχος για null
            Long currentFrequency = row.getAs("count");
            if (currentFrequency == null) {
                continue; // Παράλειψη γραμμών με null συχνότητα
            }

            // Έλεγχος αν η συχνότητα είναι ίση με τη μέγιστη
            if (currentFrequency == maxFrequency) {
                // Εξαγωγή της τιμής της στήλης και έλεγχος για null
                Object columnValue = row.getAs(column.getName());
                if (columnValue != null) {
                    modes.add(String.valueOf(columnValue)); // Προσθήκη της τιμής στη λίστα modes
                }

                // Διακοπή αν φτάσαμε το όριο του NUMBER_MODES_OPTIMIZER
                if (modes.size() >= NUMBER_MODES_OPTIMIZER) {
                    break;
                }
            } else {
                break; // Διακοπή αν η συχνότητα είναι μικρότερη από τη μέγιστη
            }
        }

        // Return the list of mode values
        return modes;
    }


}

