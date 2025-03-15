package gr.uoi.cs.pythia.gui.analysisTasksGuiPanelTest;


import gr.uoi.cs.pythia.gui.analysisTasksGuiPanels.AnalysisTabsPanel;
import gr.uoi.cs.pythia.gui.analysisTasksGuiPanels.ClusteringGUI;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ClusteringGUITest {

    private ClusteringGUI clusteringGUI;

    @Before
    public void setUp() {
        // Creating a simple instance of AnalysisTabsPanel for testing
        AnalysisTabsPanel mockTabsGUI = new AnalysisTabsPanel(new ArrayList<>(), null, null, null);
        clusteringGUI = new ClusteringGUI(mockTabsGUI,null,null);
    }

    /**
     * Test to check the initialization of the ClusteringGUI.
     * This ensures that all input fields are created and have the correct default values.
     */
    @Test
    public void testInitialization() {
        // Get the input fields via the public method getInputFields
        Map<String, JComponent> inputFields = clusteringGUI.getInputFields();

        // Check that the input fields have been created correctly
        assertNotNull("Clustering Type field should not be null", inputFields.get("Clustering Type"));
        assertNotNull("Number of Clusters field should not be null", inputFields.get("Number of Clusters"));
        assertNotNull("Selected Features field should not be null", inputFields.get("Selected Features (comma-separated)"));

        // Check the default values
        JComboBox<String> clusteringTypeComboBox = (JComboBox<String>) inputFields.get("Clustering Type");
        assertEquals("Default clustering type should be KMEANS", "KMEANS", clusteringTypeComboBox.getSelectedItem());

        JTextField numberOfClustersField = (JTextField) inputFields.get("Number of Clusters");
        assertEquals("Number of Clusters field should be empty by default", "", numberOfClustersField.getText());

        JTextField selectedFeaturesField = (JTextField) inputFields.get("Selected Features (comma-separated)");
        assertEquals("Selected Features field should be empty by default", "", selectedFeaturesField.getText());
    }

    /**
     * Test to verify that the result area updates correctly after input changes.
     * This simulates user input and checks if the result area reflects the changes properly.
     */
    @Test
    public void testUpdateResultArea() {
        // Get the input fields via the public method getInputFields
        Map<String, JComponent> inputFields = clusteringGUI.getInputFields();

        // Simulate user input
        JComboBox<String> clusteringTypeComboBox = (JComboBox<String>) inputFields.get("Clustering Type");
        JTextField numberOfClustersField = (JTextField) inputFields.get("Number of Clusters");
        JTextField selectedFeaturesField = (JTextField) inputFields.get("Selected Features (comma-separated)");

        clusteringTypeComboBox.setSelectedItem("DIVISIVE");
        numberOfClustersField.setText("5");
        selectedFeaturesField.setText("feature1, feature2");

        // Call the updateResultArea method
        clusteringGUI.updateResultArea();

        // Check that the resultArea has been updated correctly
        String expectedResult = "Current Clustering Parameters:\n" +
                "Clustering Type: DIVISIVE\n" +
                "Number of Clusters: 5\n" +
                "Selected Features (comma-separated): feature1, feature2\n";

        JTextArea resultArea = clusteringGUI.getResultArea();
        assertEquals("Result area text should match expected output", expectedResult, resultArea.getText());
    }
}
