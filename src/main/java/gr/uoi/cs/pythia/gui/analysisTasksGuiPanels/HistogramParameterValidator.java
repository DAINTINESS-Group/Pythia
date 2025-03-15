package gr.uoi.cs.pythia.gui.analysisTasksGuiPanels;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.histogram.generator.HistogramGeneratorType;
import gr.uoi.cs.pythia.histogram.generator.HistogramParameters;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class HistogramParameterValidator extends Component implements ParameterValidator<HistogramParameters> {

	@Override
    public HistogramParameters validateAndCreate(Map<String, JComponent> inputFields) {
        String histogramType = (String) ((JComboBox<?>) inputFields.get("Histogram Type")).getSelectedItem();
        String binsText = ((JTextField) inputFields.get("Number of Bins")).getText();

        if(AppController.getInstance().getDatasetProfile() == null){
            JOptionPane.showMessageDialog(null, ("You must register dataset first"), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        try {
            int bins = Integer.parseInt(binsText);
            Map<String, HistogramGeneratorType> map = new HashMap<>();
            map.put("KEEP_NANS", HistogramGeneratorType.KEEP_NANS);
            map.put("SKIP_NANS", HistogramGeneratorType.SKIP_NANS);
            HistogramGeneratorType type = map.get(histogramType);
            return new HistogramParameters(type, bins);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Bins must be integer value", "Input Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}