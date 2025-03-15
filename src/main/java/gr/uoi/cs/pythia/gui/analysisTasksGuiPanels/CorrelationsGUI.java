package gr.uoi.cs.pythia.gui.analysisTasksGuiPanels;


import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.correlations.CorrelationsParameters;

import javax.swing.*;
import java.awt.*;

public class CorrelationsGUI extends AnalysisParametersGUI<CorrelationsParameters> {

    /**
	 * 
	 */

	public CorrelationsGUI(AnalysisTabsPanel tabsGUI,JPanel cardPanel,CardLayout cardLayout) {
        super("Correlations", tabsGUI, new CorrelationsParameterValidator(), AppController.getInstance()::declareCorrelationsParameters,cardPanel, cardLayout);

        JComboBox<String> correlationMethodComboBox = new JComboBox<>(new String[]{"PEARSON"});
        addInputField("Correlation Method", correlationMethodComboBox);



    }

    @Override
    public void updateResultArea() {
        // Retrieve the component from the inputFields map
        Object correlationMethodComponent = inputFields.get("Correlation Method");

        // Initialize a StringBuilder to construct the result string
        StringBuilder sb = new StringBuilder("Current Correlations Parameters:\n");

        // Safely retrieve and append the correlation method
        if (correlationMethodComponent instanceof JComboBox<?>) {
            JComboBox<?> correlationMethodComboBox = (JComboBox<?>) correlationMethodComponent;
            String correlationMethod = correlationMethodComboBox.getSelectedItem() != null ?
                    correlationMethodComboBox.getSelectedItem().toString() : "";
            sb.append("Correlation Method: ").append(correlationMethod).append("\n");
        }

        // Set the constructed string to the result area
        resultArea.setText(sb.toString());
    }
}