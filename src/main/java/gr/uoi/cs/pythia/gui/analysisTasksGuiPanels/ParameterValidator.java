package gr.uoi.cs.pythia.gui.analysisTasksGuiPanels;

import javax.swing.*;
import java.util.Map;


public interface ParameterValidator<T> {
    T validateAndCreate(Map<String, JComponent> inputFields);
}