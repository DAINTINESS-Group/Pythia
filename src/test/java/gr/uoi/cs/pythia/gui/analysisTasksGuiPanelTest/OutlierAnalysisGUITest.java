package gr.uoi.cs.pythia.gui.analysisTasksGuiPanelTest;


import gr.uoi.cs.pythia.gui.analysisTasksGuiPanels.OutlierAnalysisGUI;
import org.junit.Test;

import javax.swing.*;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OutlierAnalysisGUITest {

    private final OutlierAnalysisGUI gui = new OutlierAnalysisGUI(null, null, null);

    /**
     * Tests the initial state of the GUI to ensure all required input fields are present
     * and set to their expected default values.
     */
    @Test
    public void testInitialGUIState() {
        Map<String, JComponent> inputFields = gui.getInputFields();

        // Ensure input fields exist
        assertNotNull("Outlier Type field should not be null", inputFields.get("Select Outlier Type"));
        assertNotNull("Threshold field should not be null", inputFields.get("Enter Threshold"));

        // Verify default values
        JComboBox<String> outlierTypeComboBox = (JComboBox<String>) inputFields.get("Select Outlier Type");
        assertEquals("Default outlier type should be Z_SCORE", "Z_SCORE", outlierTypeComboBox.getSelectedItem());

        JTextField thresholdField = (JTextField) inputFields.get("Enter Threshold");
        assertEquals("Default threshold should be empty", "", thresholdField.getText());
    }

    /**
     * Tests updating the result area after changing the input values.
     * Ensures that the displayed parameters correctly reflect the selected values.
     */
    @Test
    public void testUpdateResultArea() {
        Map<String, JComponent> inputFields = gui.getInputFields();

        // Retrieve GUI components
        JComboBox<String> outlierTypeComboBox = (JComboBox<String>) inputFields.get("Select Outlier Type");
        JTextField thresholdField = (JTextField) inputFields.get("Enter Threshold");

        // Set new values
        outlierTypeComboBox.setSelectedItem("NORMALIZED_SCORE");
        thresholdField.setText("2.5");

        // Trigger update
        gui.updateResultArea();

        // Expected result text
        String expectedResult = "Current Outlier Parameters:\n" +
                "Outlier Type: NORMALIZED_SCORE\n" +
                "Threshold: 2.5\n";

        // Verify result area content
        JTextArea resultArea = gui.getResultArea();
        assertEquals("Result area text should match expected output", expectedResult, resultArea.getText());
    }
}
