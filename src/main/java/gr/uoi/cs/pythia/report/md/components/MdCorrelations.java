package gr.uoi.cs.pythia.report.md.components;

import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.report.md.structures.MdBasicStructures;
import gr.uoi.cs.pythia.report.md.structures.MdTable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MdCorrelations {

    private final List<Column> columns;
    private final DecimalFormat decimalFormat;

    public MdCorrelations(List<Column> columns) {
        this.columns = columns;
        this.decimalFormat = new DecimalFormat("#.###");
    }

    @Override
    public String toString() {
        return getTitle() + "\n" +
                MdBasicStructures.horizontalLine() + "\n" +
                getCorrelationsTable() + "\n";
    }

    private String getTitle() {
        return MdBasicStructures.center(MdBasicStructures.heading3("Correlations"));
    }

    private String getCorrelationsTable() {
        List<String> columnNames = getColumnNames();
        String table = new MdTable(getTableHeaders(columnNames), getTableData(columnNames),
                MdTable.ALIGNMENT_TYPE.CENTER)
                .getTable();
        return MdBasicStructures.center(table);
    }

    private List<String> getColumnNames() {
        return columns.stream()
                .map(Column::getName)
                .collect(Collectors.toList());
    }

    private List<String> getTableHeaders(List<String> columnNames) {
        List<String> headers = new ArrayList<>();
        headers.add("Correlations");
        headers.addAll(columnNames);
        return headers;
    }

    private List<List<String>> getTableData(List<String> columnNames) {
        List<List<String>> data = new ArrayList<>();
        for (Column column : columns) {
            data.add(getColumnData(column, columnNames));
        }
        return data;
    }

    private List<String> getColumnData(Column column, List<String> columnNames) {
        Map<String, Double> correlations = column.getCorrelationsProfile().getAllCorrelations();
        List<String> data = new ArrayList<>();
        data.add(MdBasicStructures.bold(column.getName()));
        for (String columnName : columnNames) {
            if (correlations.containsKey(columnName))
                data.add(decimalFormat.format(correlations.get(columnName)));
            else
                data.add(null);
        }
        return data;
    }
}
