package gr.uoi.cs.pythia.descriptivestatistics;

import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.DescriptiveStatisticsProfile;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.functions;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.spark.sql.functions.col;

public class DescriptiveStatisticsCalculator implements IDescriptiveStatisticsCalculator{

    private final static int NUMBER_MODES_OPTIMIZER = 30;

    public void computeDescriptiveStats(Dataset<Row> dataset, DatasetProfile datasetProfile){
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
        for(Column column : columns){
            if(summaryColumns.contains(column.getName())){
                List<Row> columnNames = descriptiveStatistics.select(column.getName()).collectAsList();
                List<Object> descriptiveStatisticsRow =
                        columnNames.stream().map(col->col.get(0)).collect(Collectors.toList());

                String count = (String) descriptiveStatisticsRow.get(0);
                String mean = (String) descriptiveStatisticsRow.get(1);
                String standardDeviation = (String) descriptiveStatisticsRow.get(2);
                String q1 = (String) descriptiveStatisticsRow.get(3);
                String median = (String) descriptiveStatisticsRow.get(4);
                String q3 = (String) descriptiveStatisticsRow.get(5);
                String min = (String) descriptiveStatisticsRow.get(6);
                String max = (String) descriptiveStatisticsRow.get(7);


                List<String> listModes = computeModeValues(dataset, column);
                DescriptiveStatisticsProfile columnDescriptiveStatisticsProfile =
                        new DescriptiveStatisticsProfile(count, mean, standardDeviation, q1, median, q3, min, max, listModes);
                column.setDescriptiveStatisticsProfile(columnDescriptiveStatisticsProfile);
            }

        }
    }

    public List<String> computeModeValues(Dataset<Row> dataset, Column column){
        // Φιλτράρει τα null και τα κενά
        Dataset<Row> frequencyDataset = dataset.filter(col(column.getName()).isNotNull()
                .and(functions.trim(col(column.getName())).notEqual("")));

        Dataset<Row> grouped = frequencyDataset.groupBy(column.getName()).count();
        List<Row> modeRows = grouped.orderBy(functions.desc("count")).collectAsList();
        if(modeRows.isEmpty()){
            return new ArrayList<>();
        }


        long maxFrequency = modeRows.get(0).getAs("count");
        List<String> modes = new ArrayList<>();


        for(Row row : modeRows){
            if(row == null) { continue; }
            Long currentFrequency = row.getAs("count");
            if(currentFrequency == null) { continue; }
            if(currentFrequency == maxFrequency){
                Object columnValue = row.getAs(column.getName());
                if(columnValue != null){
                    modes.add(String.valueOf(columnValue));
                }
                if(modes.size() >= NUMBER_MODES_OPTIMIZER){
                    break;
                }
            } else {
                break;
            }
        }
        return modes;
    }


}

