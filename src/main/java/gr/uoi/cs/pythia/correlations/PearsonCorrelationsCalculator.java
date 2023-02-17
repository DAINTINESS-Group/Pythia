package gr.uoi.cs.pythia.correlations;

import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.CorrelationsProfile;
import gr.uoi.cs.pythia.model.DatasetProfile;


import gr.uoi.cs.pythia.util.DatatypeFilterer;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PearsonCorrelationsCalculator implements ICorrelationsCalculator {

    @Override
    public void calculateAllPairsCorrelations(Dataset<Row> dataset, DatasetProfile datasetProfile) {
        List<Column> columns = getNumericalColumns(datasetProfile);
        for (Column column : columns) {
            Map<String, Double> correlations = new HashMap<>();
            for (Column targetColumn : columns) {
                if (column == targetColumn) {
                    continue;
                }
                double correlation = dataset.stat().corr(column.getName(), targetColumn.getName());
                correlations.put(targetColumn.getName(), correlation);
            }
            column.setCorrelationsProfile(new CorrelationsProfile(correlations));
        }
    }

    public static List<Column> getNumericalColumns(DatasetProfile datasetProfile) {
        List<Column> numericalColumns = new ArrayList<>();
        for (Column column : datasetProfile.getColumns()) {
            if (DatatypeFilterer.isNumerical(column.getDatatype()))
                numericalColumns.add(column);
        }
        return numericalColumns;
    }
}
