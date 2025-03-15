package gr.uoi.cs.pythia.gui.analysisTasksGuiPanelTest;


import gr.uoi.cs.pythia.gui.analysisTasksGuiPanels.AnalysisTabsPanel;
import gr.uoi.cs.pythia.gui.analysisTasksGuiPanels.CorrelationsGUI;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CorrelationsGUITest {

    private CorrelationsGUI correlationsGUI;

    /**
     * Sets up the test environment before each test case.
     * Initializes a simple instance of AnalysisTabsPanel and CorrelationsGUI for testing.
     */
    @Before
    public void setUp() {
        // Create a simple instance of AnalysisTabsPanel for testing
        AnalysisTabsPanel mockTabsGUI = new AnalysisTabsPanel(new ArrayList<>(), null, null, null); // Assuming a no-arg constructor exists
        correlationsGUI = new CorrelationsGUI(mockTabsGUI, null, null);
    }

    /**
     * Tests the initialization of the CorrelationsGUI.
     * Ensures that all input fields are created and have the correct default values.
     */
    @Test
    public void testInitialization() {
        // Get the input fields via the public method getInputFields
        Map<String, JComponent> inputFields = correlationsGUI.getInputFields();

        // Check that the "Correlation Method" input field has been created correctly
        assertNotNull("Correlation Method field should not be null", inputFields.get("Correlation Method"));

        // Check the default values
        JComboBox<String> correlationMethodComboBox = (JComboBox<String>) inputFields.get("Correlation Method");
        assertEquals("Default correlation method should be PEARSON", "PEARSON", correlationMethodComboBox.getSelectedItem());
    }

    /**
     * Tests the update of the result area after user input.
     * Simulates user input and checks if the result area reflects the changes correctly.
     */
    @Test
    public void testUpdateResultArea() {
        // Get the input fields via the public method getInputFields
        Map<String, JComponent> inputFields = correlationsGUI.getInputFields();

        // Simulate user input
        JComboBox<String> correlationMethodComboBox = (JComboBox<String>) inputFields.get("Correlation Method");
        correlationMethodComboBox.setSelectedItem("PEARSON");

        // Call the updateResultArea method
        correlationsGUI.updateResultArea();

        // Check that the resultArea has been updated correctly
        String expectedResult = "Current Correlations Parameters:\n" +
                "Correlation Method: PEARSON\n";

        JTextArea resultArea = correlationsGUI.getResultArea();
        assertEquals("Result area text should match expected output", expectedResult, resultArea.getText());
    }
}