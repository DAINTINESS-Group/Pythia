package gr.uoi.cs.pythia.gui.analysisTasksGuiPanels;


import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.outliers.OutlierParameters;

import javax.swing.*;
import java.awt.*;

public class OutlierAnalysisGUI extends AnalysisParametersGUI<OutlierParameters> {

	public OutlierAnalysisGUI(AnalysisTabsPanel tabsGUI, JPanel cardPanel,CardLayout cardLayout) {
        super("Outlier", tabsGUI, new OutlierParameterValidator(), AppController.getInstance()::declareOutlierParameters,cardPanel,cardLayout);

        JComboBox<String> outlierTypeComboBox=new JComboBox<>(new String[]{"Z_SCORE", "NORMALIZED_SCORE"});
        addInputField("Select Outlier Type", outlierTypeComboBox);

        JTextField thresholdField=new JTextField(10);
        addInputField("Enter Threshold", thresholdField);
    }

    @Override
    public void updateResultArea() {
        // Retrieve components from the inputFields map
        Object outlierTypeComponent = inputFields.get("Select Outlier Type");
        Object thresholdComponent = inputFields.get("Enter Threshold");

        // Initialize a StringBuilder to construct the result string
        StringBuilder sb = new StringBuilder("Current Outlier Parameters:\n");

        // Safely retrieve and append the outlier type
        if (outlierTypeComponent instanceof JComboBox<?>) {
            JComboBox<?> outlierTypeComboBox = (JComboBox<?>) outlierTypeComponent;
            String outlierType = outlierTypeComboBox.getSelectedItem() != null ?
                    outlierTypeComboBox.getSelectedItem().toString() : "";
            sb.append("Outlier Type: ").append(outlierType).append("\n");
        }

        // Safely retrieve and append the threshold
        if (thresholdComponent instanceof JTextField) {
            JTextField thresholdField = (JTextField) thresholdComponent;
            String threshold = thresholdField.getText();
            sb.append("Threshold: ").append(threshold).append("\n");
        }

        // Set the constructed string to the result area
        resultArea.setText(sb.toString());
    }


}