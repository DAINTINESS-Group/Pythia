package gr.uoi.cs.pythia.gui.analysisTasksGuiPanels;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.util.HighlightParameters;

import javax.swing.*;
import java.util.Map;

public class HighlightParameterValidator implements ParameterValidator<HighlightParameters> {
    @Override
    public HighlightParameters validateAndCreate(Map<String, JComponent> inputFields) {

        if(AppController.getInstance().getDatasetProfile() == null){
            JOptionPane.showMessageDialog(null, ("You must register dataset first"), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        try {
            HighlightParameters.HighlightExtractionMode mode = (HighlightParameters.HighlightExtractionMode) ((JComboBox<?>) inputFields.get("Extraction Mode")).getSelectedItem();
            double numericLimit = Double.parseDouble(((JTextField) inputFields.get("Numeric Limit")).getText());
            return new HighlightParameters(mode, numericLimit);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Invalid Numeric Limit. Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}