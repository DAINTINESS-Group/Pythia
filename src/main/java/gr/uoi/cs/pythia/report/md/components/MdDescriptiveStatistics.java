package gr.uoi.cs.pythia.report.md.components;

import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DescriptiveStatisticsProfile;
import gr.uoi.cs.pythia.report.md.structures.MdBasicStructures;
import gr.uoi.cs.pythia.report.md.structures.MdTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MdDescriptiveStatistics {

    private final List<Column> columns;

    public MdDescriptiveStatistics(List<Column> columns) {
        this.columns = columns;
    }

    @Override
    public String toString() {
        return getTitle() + "\n" +
                MdBasicStructures.horizontalLine() + "\n" +
                getDescriptiveStatisticsTable() + "\n";
    }

    private String getTitle() {
        return MdBasicStructures.center(MdBasicStructures.heading2("Descriptive Statistics"));
    }

    private String getDescriptiveStatisticsTable() {
        String table = new MdTable(getTableHeaders(), getTableData(), MdTable.ALIGNMENT_TYPE.CENTER)
                .getTable();
        return MdBasicStructures.center(table);
    }

    private List<String> getTableHeaders() {
        return Arrays.asList("Column", "Count", "Mean",
                "Standard Deviation", "Median", "Min", "Max");
    }

    private List<List<String>> getTableData() {
        List<List<String>> data = new ArrayList<>();
        for (Column column : columns) {
            data.add(getColumnData(column));
        }
        return data;
    }

    private List<String> getColumnData(Column column) {
        DescriptiveStatisticsProfile stats = column.getDescriptiveStatisticsProfile();
        if (stats == null) {
            List<String> data = new ArrayList<>();
            data.add(column.getName());
            data.addAll(Collections.nCopies(6, null));
            return data;
        }
        return Arrays.asList(column.getName(),
                stats.getCount(),
                stats.getMean(),
                stats.getStandardDeviation(),
                stats.getMedian(),
                stats.getMin(),
                stats.getMax());
    }
}
