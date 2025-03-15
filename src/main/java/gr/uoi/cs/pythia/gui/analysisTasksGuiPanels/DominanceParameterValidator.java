package gr.uoi.cs.pythia.gui.analysisTasksGuiPanels;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.patterns.dominance.DominanceColumnSelectionMode;
import gr.uoi.cs.pythia.patterns.dominance.DominanceParameters;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import javax.swing.*;
import java.util.Arrays;
import java.util.Map;

public class DominanceParameterValidator implements ParameterValidator<DominanceParameters> {
    @Override
    public DominanceParameters validateAndCreate(Map<String, JComponent> inputFields) {
        DominanceColumnSelectionMode mode = (DominanceColumnSelectionMode) ((JComboBox<?>) inputFields.get("Selection Mode")).getSelectedItem();
        String measurementColumnsStr = ((JTextField) inputFields.get("Measurement Columns")).getText();
        String coordinateColumnsStr = ((JTextField) inputFields.get("Coordinate Columns")).getText();

        String[] measurementColumns = parseAndValidateColumns(measurementColumnsStr, "Measurement");
        String[] coordinateColumns = parseAndValidateColumns(coordinateColumnsStr, "Coordinate");

        if (measurementColumns == null || coordinateColumns == null) {
            return null; // Stop processing if there's an error in validation
        }
        return new DominanceParameters(mode, measurementColumns, coordinateColumns);
    }

    private String[] parseAndValidateColumns(String columnsStr, String columnName) {
        if (columnsStr == null || columnsStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, columnName + " columns cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        String[] columns = Arrays.stream(columnsStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);

        if (columns.length == 0) {
            JOptionPane.showMessageDialog(null, columnName + " columns must contain at least one value.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        // Validate that all measurement and coordinate columns exist in the dataset
        Dataset<Row> dataset = AppController.getInstance().getDataset();
        if(dataset == null){
            JOptionPane.showMessageDialog(null, "You must register dataset first", "Error", JOptionPane.ERROR_MESSAGE);
             return null;
        }
        String[] datasetColumns = dataset.columns();

        for(String column : columnsStr.split(",")){
            if(!Arrays.asList(datasetColumns).contains(column)){
                JOptionPane.showMessageDialog(null, columnName + "column '"+column+"' does not exist in the dataset.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }

        return columns;
    }
}