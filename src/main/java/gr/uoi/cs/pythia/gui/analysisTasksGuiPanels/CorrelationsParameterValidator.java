package gr.uoi.cs.pythia.gui.analysisTasksGuiPanels;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.correlations.CorrelationsMethod;
import gr.uoi.cs.pythia.correlations.CorrelationsParameters;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;


public class CorrelationsParameterValidator implements ParameterValidator<CorrelationsParameters> {
    @Override
    public CorrelationsParameters validateAndCreate(Map<String, JComponent> inputFields) {
        String method = (String) ((JComboBox<?>) inputFields.get("Correlation Method")).getSelectedItem();
        Map<String, CorrelationsMethod> map = new HashMap<>();
        map.put("PEARSON", CorrelationsMethod.PEARSON);
        CorrelationsMethod correlationsMethod;
        if(map.containsKey(method)){
            correlationsMethod = map.get(method);
        }else{
            return null;
        }
        if(AppController.getInstance().getDatasetProfile() == null){
            JOptionPane.showMessageDialog(null, ("You must register dataset first"), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        return new CorrelationsParameters(correlationsMethod);
    }
}