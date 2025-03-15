package gr.uoi.cs.pythia.gui.analysisTasksGuiPanelTest;


import gr.uoi.cs.pythia.gui.analysisTasksGuiPanels.AnalysisTabsPanel;
import gr.uoi.cs.pythia.gui.analysisTasksGuiPanels.DominanceParametersGUI;
import gr.uoi.cs.pythia.patterns.dominance.DominanceColumnSelectionMode;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DominanceParametersGUITest {

    private DominanceParametersGUI dominanceParametersGUI;

    /**
     * Sets up the test environment before each test case.
     * Initializes a simple instance of AnalysisTabsPanel and DominanceParametersGUI for testing.
     */
    @Before
    public void setUp() {
        // Create a simple instance of AnalysisTabsPanel for testing
        AnalysisTabsPanel mockTabsGUI = new AnalysisTabsPanel(new ArrayList<>(), null, null, null); // Assuming a no-arg constructor exists
        dominanceParametersGUI = new DominanceParametersGUI(mockTabsGUI, null, null);
    }

    /**
     * Tests the initialization of the DominanceParametersGUI.
     * Ensures that all input fields are created and have the correct default values.
     */
    @Test
    public void testInitialization() {
        // Get the input fields via the public method getInputFields
        Map<String, JComponent> inputFields = dominanceParametersGUI.getInputFields();

        // Check that the input fields have been created correctly
        assertNotNull("Selection Mode field should not be null", inputFields.get("Selection Mode"));
        assertNotNull("Measurement Columns field should not be null", inputFields.get("Measurement Columns"));
        assertNotNull("Coordinate Columns field should not be null", inputFields.get("Coordinate Columns"));

        // Check the default values
        JComboBox<DominanceColumnSelectionMode> selectionModeComboBox = (JComboBox<DominanceColumnSelectionMode>) inputFields.get("Selection Mode");
        assertEquals("Default selection mode should be EXHAUSTIVE", DominanceColumnSelectionMode.EXHAUSTIVE, selectionModeComboBox.getSelectedItem());
    }

    /**
     * Tests the update of the result area after user input.
     * Simulates user input and checks if the result area reflects the changes correctly.
     */
    @Test
    public void testUpdateResultArea() {
        // Get the input fields via the public method getInputFields
        Map<String, JComponent> inputFields = dominanceParametersGUI.getInputFields();

        // Simulate user input
        JComboBox<DominanceColumnSelectionMode> selectionModeComboBox = (JComboBox<DominanceColumnSelectionMode>) inputFields.get("Selection Mode");
        JTextField measurementColumnsField = (JTextField) inputFields.get("Measurement Columns");
        JTextField coordinateColumnsField = (JTextField) inputFields.get("Coordinate Columns");

        selectionModeComboBox.setSelectedItem(DominanceColumnSelectionMode.EXHAUSTIVE);
        measurementColumnsField.setText("col1, col2");
        coordinateColumnsField.setText("col3, col4");

        // Call the updateResultArea method
        dominanceParametersGUI.updateResultArea();

        // Check that the resultArea has been updated correctly
        String expectedResult = "Current Dominance Parameters:\n" +
                "Selection Mode: EXHAUSTIVE\n" +
                "Measurement Columns: col1, col2\n" +
                "Coordinate Columns: col3, col4\n";

        JTextArea resultArea = dominanceParametersGUI.getResultArea();
        assertEquals("Result area text should match expected output", expectedResult, resultArea.getText());
    }
}
