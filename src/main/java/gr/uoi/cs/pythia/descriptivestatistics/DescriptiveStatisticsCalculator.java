package gr.uoi.cs.pythia.descriptivestatistics;

import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.DescriptiveStatisticsProfile;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DescriptiveStatisticsCalculator implements IDescriptiveStatisticsCalculator {

    public void computeDescriptiveStats(Dataset<Row> dataset, DatasetProfile datasetProfile) {
        Dataset<Row> descriptiveStatistics =
                dataset.summary(
                        DescriptiveStatisticsConstants.COUNT,
                        DescriptiveStatisticsConstants.MEAN,
                        DescriptiveStatisticsConstants.STANDARD_DEVIATION,
                        DescriptiveStatisticsConstants.MEDIAN,
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
                String median = (String) descriptiveStatisticsRow.get(3);
                String min = (String) descriptiveStatisticsRow.get(4);
                String max = (String) descriptiveStatisticsRow.get(5);

                DescriptiveStatisticsProfile columnDescriptiveStatisticsProfile =
                        new DescriptiveStatisticsProfile(count, mean, standardDeviation, median, min, max);
                column.setDescriptiveStatisticsProfile(columnDescriptiveStatisticsProfile);
            }
        }
    }
}
