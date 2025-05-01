package gr.uoi.cs.pythia.gui.guiScores;

import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

public class ScorePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7400743541985730022L;
	private final JTable table;
    private final String[] columnNames;

    public ScorePanel(Map<String, Map<DataType, Integer>> scoresPerColumnMap, String[] columnNames) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLUE, 2),
                "Editor Type Scores",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 16),
                Color.BLUE
        ));
        setBackground(new Color(240, 255, 240));
        this.columnNames = columnNames;
        table = new JTable(createScoreModel(scoresPerColumnMap));
        TableStyler.styleTable(table);


        int lastColumnIndex = table.getColumnModel().getColumnCount() - 1;
        table.getColumnModel().getColumn(lastColumnIndex).setCellRenderer(new ButtonRenderer());

        JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);
    }


    private DefaultTableModel createScoreModel(Map<String, Map<DataType, Integer>> scoresPerColumnMap) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Column Name");


        ArrayList<DataType> dataTypes = new ArrayList<>();
        dataTypes.add(DataTypes.StringType);
        dataTypes.add(DataTypes.BooleanType);
        dataTypes.add(DataTypes.DateType);
        dataTypes.add(DataTypes.TimestampType);
        dataTypes.add(DataTypes.ShortType);
        dataTypes.add(DataTypes.IntegerType);
        dataTypes.add(DataTypes.LongType);
        dataTypes.add(DataTypes.FloatType);
        dataTypes.add(DataTypes.DoubleType);
        dataTypes.add(DataTypes.createDecimalType());

        for (DataType type : dataTypes) {
            model.addColumn(type.toString());
        }

        model.addColumn("DataType Editor");

        if (scoresPerColumnMap == null || scoresPerColumnMap.isEmpty() || columnNames == null) {
            return model;
        }

        for (String columnName : columnNames) {
            if (scoresPerColumnMap.containsKey(columnName)) {
                Map<DataType, Integer> scores = scoresPerColumnMap.get(columnName);
                Object[] rowData = new Object[dataTypes.size() + 2]; // +2  "Column Name","Edit"
                rowData[0] = columnName;

                int i = 1;
                for (DataType type : dataTypes) {
                    rowData[i++] = scores.getOrDefault(type, 0);
                }
                rowData[i] = "Set Type: "+columnName;
                model.addRow(rowData);
            }
        }
        return model;
    }



    public JTable getTable() {
        return table;
    }

}