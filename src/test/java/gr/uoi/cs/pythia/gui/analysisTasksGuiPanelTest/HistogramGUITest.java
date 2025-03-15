package gr.uoi.cs.pythia.gui.analysisTasksGuiPanelTest;


import gr.uoi.cs.pythia.gui.analysisTasksGuiPanels.AnalysisTabsPanel;
import gr.uoi.cs.pythia.gui.analysisTasksGuiPanels.HistogramGUI;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HistogramGUITest {

    private HistogramGUI histogramGUI;

    /**
     * Sets up the test environment before each test case.
     * Initializes a simple instance of AnalysisTabsPanel and HistogramGUI for testing.
     */
    @Before
    public void setUp() {
        // Create a simple instance of AnalysisTabsPanel for testing
        AnalysisTabsPanel mockTabsGUI = new AnalysisTabsPanel(new ArrayList<>(), null, null, null); // Assuming a no-arg constructor exists
        histogramGUI = new HistogramGUI(mockTabsGUI, null, null);
    }

    /**
     * Tests the initialization of the HistogramGUI.
     * Ensures that all input fields are created and have the correct default values.
     *
     * @throws Exception if there is an issue accessing fields via reflection.
     */
    @Test
    public void testInitialization() throws Exception {
        // Get the input fields via reflection
        Field inputFieldsField = HistogramGUI.class.getSuperclass().getDeclaredField("inputFields");
        inputFieldsField.setAccessible(true);
        Map<String, JComponent> inputFields = (Map<String, JComponent>) inputFieldsField.get(histogramGUI);

        // Check that the input fields have been created correctly
        assertNotNull("Histogram Type field should not be null", inputFields.get("Histogram Type"));
        assertNotNull("Number of Bins field should not be null", inputFields.get("Number of Bins"));

        // Check the default values
        JComboBox<?> histogramTypeComboBox = (JComboBox<?>) inputFields.get("Histogram Type");
        assertEquals("Default histogram type should be KEEP_NANS", "KEEP_NANS", histogramTypeComboBox.getSelectedItem());
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
        Field inputFieldsField = HistogramGUI.class.getSuperclass().getDeclaredField("inputFields");
        inputFieldsField.setAccessible(true);
        Map<String, JComponent> inputFields = (Map<String, JComponent>) inputFieldsField.get(histogramGUI);

        // Simulate user input
        JComboBox<?> histogramTypeComboBox = (JComboBox<?>) inputFields.get("Histogram Type");
        JTextField numberOfBinsField = (JTextField) inputFields.get("Number of Bins");

        histogramTypeComboBox.setSelectedItem("SKIP_NANS");
        numberOfBinsField.setText("15");

        // Call the updateResultArea method
        histogramGUI.updateResultArea();

        // Get the resultArea via reflection
        JTextArea resultArea = histogramGUI.getResultArea();

        // Check that the resultArea has been updated correctly
        String expectedResult = "Current Histogram Parameters:\n" +
                "Histogram Type: SKIP_NANS\n" +
                "Number of Bins: 15\n";

        assertEquals(expectedResult, resultArea.getText());
    }
}