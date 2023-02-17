package gr.uoi.cs.pythia.report.md.structures;

import java.util.List;

public class MdTable {

    public enum ALIGNMENT_TYPE { LEFT, CENTER, RIGHT}
    private final List<String> headers;
    private final List<List<String>> data;
    private final String alignment;
    private final StringBuilder stringBuilder;

    public MdTable(List<String> headers, List<List<String>> data, ALIGNMENT_TYPE alignment_type) {
        this.headers = headers;
        this.data = data;
        String[] alignmentTypes = {":---", ":---:", "---:"};
        this.alignment = alignmentTypes[alignment_type.ordinal()];
        this.stringBuilder = new StringBuilder();
    }

    public String getTable() {
        if (stringBuilder.length() == 0) {
            createTableHeader();
            createTableData();
        }
        return stringBuilder.toString();
    }

    private void createTableHeader() {
        for (String header : headers) {
            stringBuilder.append(String.format("| %s ", header));
        }
        stringBuilder.append("|\n");

        for (int i=0; i < headers.size(); i++) {
            stringBuilder.append(String.format("| %s ", alignment));
        }
        stringBuilder.append("|\n");
    }

    private void createTableData() {
        for (List<String> rowData : data) {
            createTableRow(rowData);
        }
    }

    public void createTableRow(List<String> data) {
        for (String value : data) {
            if (value == null || value.equals("null"))
                stringBuilder.append("| X ");
            else
                stringBuilder.append(String.format("| %s ", value));
        }
        stringBuilder.append("|\n");
    }
}
