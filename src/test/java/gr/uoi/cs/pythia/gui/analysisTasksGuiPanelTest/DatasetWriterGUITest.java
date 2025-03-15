package gr.uoi.cs.pythia.gui.analysisTasksGuiPanelTest;


import gr.uoi.cs.pythia.gui.analysisTasksGuiPanels.DatasetWriterGUI;
import gr.uoi.cs.pythia.writer.DatasetWriterConstants;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DatasetWriterGUITest {

    private DatasetWriterGUI datasetWriterGUI;

    /**
     * Sets up the test environment before each test case.
     * Initializes a DatasetWriterGUI instance for testing.
     */
    @Before
    public void setUp() {
        datasetWriterGUI = new DatasetWriterGUI(null, null);
    }

    /**
     * Tests the initialization of the DatasetWriterGUI.
     * Ensures that all input fields are created and have the correct default values.
     */
    @Test
    public void testInitialization() {
        // Get the input fields via the public method getInputFields
        Map<String, JComponent> inputFields = datasetWriterGUI.getInputFields();

        // Check that the input fields have been created correctly
        assertNotNull("Dataset Alias field should not be null", inputFields.get("Dataset Alias"));
        assertNotNull("Writer Type field should not be null", inputFields.get("Writer Type"));
        assertNotNull("Output Path field should not be null", inputFields.get("Output Path"));

        // Check the default values
        JComboBox<String> writerTypeComboBox = (JComboBox<String>) inputFields.get("Writer Type");
        assertEquals("Default writer type should be HADOOP", DatasetWriterConstants.HADOOP, writerTypeComboBox.getSelectedItem());
    }

    /**
     * Tests the update of the result area after user input.
     * Simulates user input and checks if the result area reflects the changes correctly.
     */
    @Test
    public void testUpdateResultArea() {
        // Get the input fields via the public method getInputFields
        Map<String, JComponent> inputFields = datasetWriterGUI.getInputFields();

        // Simulate user input
        JTextField datasetAliasField = (JTextField) inputFields.get("Dataset Alias");
        JComboBox<String> writerTypeComboBox = (JComboBox<String>) inputFields.get("Writer Type");
        JTextField outputPathField = (JTextField) inputFields.get("Output Path");

        datasetAliasField.setText("my_dataset");
        writerTypeComboBox.setSelectedItem(DatasetWriterConstants.NAIVE);
        outputPathField.setText("/path/to/output");

        // Call the updateResultArea method
        datasetWriterGUI.updateResultArea();

        // Check that the resultArea has been updated correctly
        String expectedResult = "Current Dataset Writer Parameters:\n" +
                "Dataset Alias: my_dataset\n" +
                "Writer Type: naive\n" +
                "Output Path: /path/to/output\n";

        JTextArea resultArea = datasetWriterGUI.getResultArea();
        assertEquals("Result area text should match expected output", expectedResult, resultArea.getText());
    }
}