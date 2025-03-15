package gr.uoi.cs.pythia.gui.analysisTasksGuiPanels;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.report.ReportGeneratorConstants;
import gr.uoi.cs.pythia.report.ReportParameters;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class ReportParameterValidator implements ParameterValidator<ReportParameters> {
    @Override
    public ReportParameters validateAndCreate(Map<String, JComponent> inputFields) {

        if(AppController.getInstance().getDatasetProfile() == null){
            JOptionPane.showMessageDialog(null, ("You must register dataset first"), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        if(inputFields == null){
            return null;
        }

        String reportType = (String) ((JComboBox<?>) inputFields.get("Report Type")).getSelectedItem();
        String path = ((JTextField) inputFields.get("Save Path")).getText();

        if (path.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please select a save path.", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if(reportType==null || reportType.isEmpty()){
            JOptionPane.showMessageDialog(null, "Please select a report type.", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        Map<String, String> map = new HashMap<>();
        map.put("TXT", ReportGeneratorConstants.TXT_REPORT);
        map.put("MD", ReportGeneratorConstants.MD_REPORT);
        map.put("JSON", ReportGeneratorConstants.JSON_REPORT);
        if (!map.containsKey(reportType)) {
            JOptionPane.showMessageDialog(null, "Report type is wrong.", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        String type = map.get(reportType);
        return new ReportParameters(type, path);
    }
}
