package gr.uoi.cs.pythia.report.md.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import gr.uoi.cs.pythia.model.CardinalitiesProfile;
import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.report.md.structures.MdBasicStructures;
import gr.uoi.cs.pythia.report.md.structures.MdTable;


public class MdCardinalities {

    private final List<Column> columnList;

    public MdCardinalities(DatasetProfile datasetProfile) {
        this.columnList = datasetProfile.getColumns();
    }

    @Override
    public String toString() {
        return getTitle() + "\n" +
                MdBasicStructures.horizontalLine() + "\n" +
                getCardinalitiesTable() + "\n";
    }

    private String getTitle() {
        return MdBasicStructures.center(MdBasicStructures.heading2("Cardinalities Statistics"));
    }

    private String getCardinalitiesTable() {
        String table = new MdTable(getTableHeaders(), getTableData(), MdTable.ALIGNMENT_TYPE.CENTER)
                .getTable();
        return MdBasicStructures.center(table);
    }
    private List<String> getTableHeaders() {
        return Arrays.asList("Column", "NullValues", "DistinctValues");
    }
    private List<List<String>> getTableData() {
        List<List<String>> data = new ArrayList<>();
        for (Column column : columnList) {
            data.add(getColumnData(column));
        }
        return data;
    }
    private List<String> getColumnData(Column column) {
        CardinalitiesProfile profile = column.getCardinalitiesProfile();
        if (profile == null) {
            List<String> data = new ArrayList<>();
            data.add(column.getName());
            data.addAll(Collections.nCopies(3, null));
            return data;
        }
        String nullValues = String.valueOf(profile.getNumberOfNullValues());
        String distinctValues = String.valueOf(profile.getNumberOfDistinctValues());
        return Arrays.asList(column.getName(),
                nullValues,
                distinctValues);
    }


}
