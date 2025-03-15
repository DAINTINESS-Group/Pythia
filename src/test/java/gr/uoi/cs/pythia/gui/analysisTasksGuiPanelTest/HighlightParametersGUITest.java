package gr.uoi.cs.pythia.gui.analysisTasksGuiPanelTest;


import gr.uoi.cs.pythia.gui.analysisTasksGuiPanels.HighlightParametersGUI;
import gr.uoi.cs.pythia.util.HighlightParameters;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HighlightParametersGUITest {

    private HighlightParametersGUI highlightParametersGUI;

    /**
     * Sets up the test environment before each test case.
     * Initializes a HighlightParametersGUI instance for testing.
     */
    @Before
    public void setUp() {
        highlightParametersGUI = new HighlightParametersGUI(null, null, null, null); // No tabsGUI needed for testing
    }

    /**
     * Tests the initialization of the HighlightParametersGUI.
     * Ensures that all input fields are created and have the correct default values.
     *
     * @throws Exception if there is an issue accessing fields via reflection.
     */
    @Test
    public void testInitialization() throws Exception {
        // Get the input fields via reflection
        Field inputFieldsField = HighlightParametersGUI.class.getSuperclass().getDeclaredField("inputFields");
        inputFieldsField.setAccessible(true);
        Map<String, JComponent> inputFields = (Map<String, JComponent>) inputFieldsField.get(highlightParametersGUI);

        // Check that the input fields have been created correctly
        assertNotNull("Extraction Mode field should not be null", inputFields.get("Extraction Mode"));
        assertNotNull("Numeric Limit field should not be null", inputFields.get("Numeric Limit"));

        // Check the default values
        JComboBox<?> extractionModeComboBox = (JComboBox<?>) inputFields.get("Extraction Mode");
        assertEquals("Default extraction mode should be NONE", HighlightParameters.HighlightExtractionMode.NONE, extractionModeComboBox.getSelectedItem());
    }

    /**
     * Tests the update of the result area after user input.
     * Simulates user input and checks if the result area reflects the changes correctly.
     *
     * @throws Exception if there is an issue accessing fields via reflection.
     */
    @Test
    public void testUpdateResultArea() throws Exception {
        // Get the input fields via reflection
        Field inputFieldsField = HighlightParametersGUI.class.getSuperclass().getDeclaredField("inputFields");
        inputFieldsField.setAccessible(true);
        Map<String, JComponent> inputFields = (Map<String, JComponent>) inputFieldsField.get(highlightParametersGUI);

        // Simulate user input
        JComboBox<?> extractionModeComboBox = (JComboBox<?>) inputFields.get("Extraction Mode");
        JTextField numericLimitField = (JTextField) inputFields.get("Numeric Limit");

        extractionModeComboBox.setSelectedItem(HighlightParameters.HighlightExtractionMode.TOP);
        numericLimitField.setText("15.75");

        // Call the updateResultArea method
        highlightParametersGUI.updateResultArea();

        // Get the resultArea via reflection
        Field resultAreaField = HighlightParametersGUI.class.getSuperclass().getDeclaredField("resultArea");
        resultAreaField.setAccessible(true);
        JTextArea resultArea = (JTextArea) resultAreaField.get(highlightParametersGUI);

        // Check that the resultArea has been updated correctly
        String expectedResult = "Current Highlight Parameters:\n" +
                "Extraction Mode: TOP\n" +
                "Numeric Limit: 15.75\n";

        assertEquals(expectedResult, resultArea.getText());
    }
}