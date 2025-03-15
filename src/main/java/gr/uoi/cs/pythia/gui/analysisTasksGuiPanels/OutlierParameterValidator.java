package gr.uoi.cs.pythia.gui.analysisTasksGuiPanels;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.model.outlier.OutlierType;
import gr.uoi.cs.pythia.outliers.OutlierParameters;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class OutlierParameterValidator implements ParameterValidator<OutlierParameters> {
    @Override
    public OutlierParameters validateAndCreate(Map<String, JComponent> inputFields) {
        String selectedOutlierType = (String) ((JComboBox<?>) inputFields.get("Select Outlier Type")).getSelectedItem();
        String thresholdText = ((JTextField) inputFields.get("Enter Threshold")).getText();

        if(AppController.getInstance().getDatasetProfile() == null){
            JOptionPane.showMessageDialog(null, ("You must register dataset first"), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        try {
            double threshold = Double.parseDouble(thresholdText);
            Map<String, OutlierType> map = new HashMap<>();
            map.put("Z_SCORE", OutlierType.Z_SCORE);
            map.put("NORMALIZED_SCORE", OutlierType.NORMALIZED_SCORE);
            OutlierType type = map.get(selectedOutlierType);
            return new OutlierParameters(type, threshold);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "threshold must be double value", "Input Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}