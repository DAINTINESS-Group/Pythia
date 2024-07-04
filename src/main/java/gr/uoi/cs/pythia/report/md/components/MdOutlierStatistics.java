package gr.uoi.cs.pythia.report.md.components;

import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.OutlierProfile;
import gr.uoi.cs.pythia.model.outlier.OutlierResult;
import gr.uoi.cs.pythia.report.md.structures.MdBasicStructures;
import gr.uoi.cs.pythia.report.md.structures.MdTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MdOutlierStatistics {


    private final List<Column> columns;

    public MdOutlierStatistics(DatasetProfile datasetProfile) {
        this.columns = datasetProfile.getColumns();
    }


    @Override
    public String toString() {

        String tableData = getOutlierStatisticsTable();
       // if(tableData.isEmpty()){
         // return getTitle() + "\n" +MdBasicStructures.horizontalLine() + "\n" + "Not found Outlier Statistics";
       // }
        return getTitle() + "\n" +
                MdBasicStructures.horizontalLine() + "\n" +
                tableData + "\n";

    }

    private String getTitle() {
        return MdBasicStructures.center(MdBasicStructures.heading2("Outliers Statistics"));
    }

    private String getOutlierStatisticsTable() {
        List<List<String>> listData = getTableData();
        if (!listData.isEmpty()) {
            String table = new MdTable(getTableHeaders(), listData, MdTable.ALIGNMENT_TYPE.CENTER).getTable();
            return MdBasicStructures.center(table);
        }
        String notFound = "Not found Outlier Statistics";
        return MdBasicStructures.center(notFound);
    }

    private List<String> getTableHeaders() {
        return Arrays.asList("Column","outlierType", "value", "score",
                "position");
    }

    private List<List<String>> getTableData() {
        List<List<String>> data = new ArrayList<>();
        for (Column column : columns) {
            List<List<String>> columnData = getColumnData(column);
            if (!columnData.isEmpty()) {
                data.addAll(columnData);
            }
        }
        return data;
    }

    private List<List<String>> getColumnData(Column column) {
        List<List<String>> rows = new ArrayList<>();
        OutlierProfile outlierProfile = column.getOutlierProfile();
        if (outlierProfile == null) {
            return Collections.emptyList();
        }

        List<OutlierResult> outlierResults = outlierProfile.getOutlierResultList();
        for (OutlierResult result : outlierResults) {
            List<String> rowData = new ArrayList<>();
            rowData.add(column.getName());
            rowData.add(outlierProfile.getOutlierType());
            rowData.add(String.valueOf(result.getValue()));
            rowData.add(String.valueOf(result.getScore()));
            rowData.add(String.valueOf(result.getPosition()));
            rows.add(rowData);
        }
        return rows;
    }

}
