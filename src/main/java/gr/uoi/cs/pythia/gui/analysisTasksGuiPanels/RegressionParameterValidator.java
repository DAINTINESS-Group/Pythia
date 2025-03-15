package gr.uoi.cs.pythia.gui.analysisTasksGuiPanels;


import gr.uoi.cs.pythia.regression.RegressionRequest;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class RegressionParameterValidator extends Component implements ParameterValidator<RegressionRequest> {

	@Override
    public RegressionRequest validateAndCreate(Map<String, JComponent> inputFields){
        return null;
    }
}