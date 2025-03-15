package gr.uoi.cs.pythia.gui.analysisTasksGuiPanels;


import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.histogram.generator.HistogramParameters;

import javax.swing.*;
import java.awt.*;

// HistogramGUI.java
public class HistogramGUI extends AnalysisParametersGUI<HistogramParameters> {
    /**
	 * 
	 */


	public HistogramGUI(AnalysisTabsPanel tabs,JPanel cardPanel, CardLayout cardLayout) {
        super("Histograms", tabs, new HistogramParameterValidator(), AppController.getInstance()::declareHistogramParameters,cardPanel,cardLayout);

        JComboBox<String> histogramTypeComboBox=new JComboBox<>(new String[]{"KEEP_NANS", "SKIP_NANS"});
        addInputField("Histogram Type", histogramTypeComboBox);

        JTextField numberOfBinsField=new JTextField();
        addInputField("Number of Bins", numberOfBinsField);
    }

    @Override
    public void updateResultArea() {
        // Retrieve components from the inputFields map
        Object histogramTypeComponent = inputFields.get("Histogram Type");
        Object numberOfBinsComponent = inputFields.get("Number of Bins");

        // Initialize a StringBuilder to construct the result string
        StringBuilder sb = new StringBuilder("Current Histogram Parameters:\n");

        // Safely retrieve and append the histogram type
        if (histogramTypeComponent instanceof JComboBox<?>) {
            JComboBox<?> histogramTypeComboBox = (JComboBox<?>) histogramTypeComponent;
            String histogramType = histogramTypeComboBox.getSelectedItem() != null ?
                    histogramTypeComboBox.getSelectedItem().toString() : "";
            sb.append("Histogram Type: ").append(histogramType).append("\n");
        }

        // Safely retrieve and append the number of bins
        if (numberOfBinsComponent instanceof JTextField) {
            JTextField numberOfBinsField = (JTextField) numberOfBinsComponent;
            String numberOfBins = numberOfBinsField.getText();
            sb.append("Number of Bins: ").append(numberOfBins).append("\n");
        }

        // Set the constructed string to the result area
        resultArea.setText(sb.toString());
    }

}
