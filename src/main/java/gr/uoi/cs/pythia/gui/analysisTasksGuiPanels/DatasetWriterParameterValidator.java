package gr.uoi.cs.pythia.gui.analysisTasksGuiPanels;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.writer.DatasetWriterParameters;

import javax.swing.*;
import java.util.Map;

public class DatasetWriterParameterValidator implements ParameterValidator<DatasetWriterParameters> {
    @Override
    public DatasetWriterParameters validateAndCreate(Map<String, JComponent> inputFields) {
        String alias = ((JTextField) inputFields.get("Dataset Alias")).getText().trim();
        String writerType = (String) ((JComboBox<?>) inputFields.get("Writer Type")).getSelectedItem();
        String path = ((JTextField) inputFields.get("Output Path")).getText().trim();

        if (alias.isEmpty() || path.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if(AppController.getInstance().getDatasetProfile() == null){
            JOptionPane.showMessageDialog(null, ("You must register dataset first"), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        return new DatasetWriterParameters(alias, writerType, path);
    }
}