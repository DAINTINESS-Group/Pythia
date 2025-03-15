package gr.uoi.cs.pythia.gui.guiScores;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;


public class DataPanel extends JPanel {

	private final JTable table;
    public DataPanel(Dataset<Row> dataset, String[] columnNames) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLUE, 2),
                "Preview Data",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 16),
                Color.BLUE
        ));

        table = new JTable(createDataModel(dataset, columnNames));
        TableStyler.styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(scrollPane , BorderLayout.CENTER);
    }

    private DefaultTableModel createDataModel(Dataset<Row> dataset, String[] columnNames) {
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Row row : dataset.collectAsList()) {
            Object[] rowData = new Object[columnNames.length];
            for (int i = 0; i < columnNames.length; i++) {
                Object value = row.get(i);

                if (value != null) {
                    rowData[i] = value.toString();
                }
            }
            model.addRow(rowData);
        }
        return model;
    }


    public JTable getTable() {
        return table;
    }
}
