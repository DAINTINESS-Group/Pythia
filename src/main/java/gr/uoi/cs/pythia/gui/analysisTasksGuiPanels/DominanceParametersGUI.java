package gr.uoi.cs.pythia.gui.analysisTasksGuiPanels;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.patterns.dominance.DominanceColumnSelectionMode;
import gr.uoi.cs.pythia.patterns.dominance.DominanceParameters;

import javax.swing.*;
import java.awt.*;

public class DominanceParametersGUI extends AnalysisParametersGUI<DominanceParameters> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7454744830595618395L;

	public DominanceParametersGUI( AnalysisTabsPanel tabsGUI,JPanel cardPanel, CardLayout cardLayout) {
        super("Dominance Patterns", tabsGUI, new DominanceParameterValidator(), AppController.getInstance()::declareDominanceParameters,cardPanel,cardLayout);

        JComboBox<DominanceColumnSelectionMode> selectionModeComboBox=new JComboBox<>(DominanceColumnSelectionMode.values());
        addInputField("Selection Mode", selectionModeComboBox);

        JTextField measurementColumnsField=new JTextField(20);
        addInputField("Measurement Columns", measurementColumnsField);

        JTextField coordinateColumnsField=new JTextField(20);
        addInputField("Coordinate Columns", coordinateColumnsField);
    }

    @Override
    public void updateResultArea() {
        // Retrieve components from the inputFields map
        Object selectionModeComponent = inputFields.get("Selection Mode");
        Object measurementColumnsComponent = inputFields.get("Measurement Columns");
        Object coordinateColumnsComponent = inputFields.get("Coordinate Columns");

        // Initialize a StringBuilder to construct the result string
        StringBuilder sb = new StringBuilder("Current Dominance Parameters:\n");

        // Safely retrieve and append the selection mode
        if (selectionModeComponent instanceof JComboBox<?>) {
            JComboBox<?> selectionModeComboBox = (JComboBox<?>) selectionModeComponent;
            DominanceColumnSelectionMode selectionMode = selectionModeComboBox.getSelectedItem() != null ?
                    (DominanceColumnSelectionMode) selectionModeComboBox.getSelectedItem() : null;
            sb.append("Selection Mode: ").append(selectionMode != null ? selectionMode.toString() : "").append("\n");
        }

        // Safely retrieve and append the measurement columns
        if (measurementColumnsComponent instanceof JTextField) {
            JTextField measurementColumnsField = (JTextField) measurementColumnsComponent;
            String measurementColumns = measurementColumnsField.getText();
            sb.append("Measurement Columns: ").append(measurementColumns).append("\n");
        }

        // Safely retrieve and append the coordinate columns
        if (coordinateColumnsComponent instanceof JTextField) {
            JTextField coordinateColumnsField = (JTextField) coordinateColumnsComponent;
            String coordinateColumns = coordinateColumnsField.getText();
            sb.append("Coordinate Columns: ").append(coordinateColumns).append("\n");
        }

        // Set the constructed string to the result area
        resultArea.setText(sb.toString());
    }

}