package gr.uoi.cs.pythia.gui.resultsGui;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.*;
public class ResultsPanelManagerTest {

    private ResultsPanelManager resultsPanelManager;

    /**
     * Setup method before each test.
     * Initializes mock JCheckBoxes and the ResultsPanelManager with the selected analysis types.
     */
    @Before
    public void setUp() throws Exception {
        // Creating mock JCheckBoxes to simulate user selection
        JCheckBox checkBox1 = new JCheckBox("Descriptive Stats");
        JCheckBox checkBox2 = new JCheckBox("Regression");
        checkBox1.setSelected(true);  // First checkbox is selected
        checkBox2.setSelected(false); // Second checkbox is not selected

        // Creating the ResultsPanelManager with the selected checkboxes
        resultsPanelManager = new ResultsPanelManager(new JCheckBox[] { checkBox1, checkBox2 }, null, null);
    }

    /**
     * Tear down method after each test.
     * Restores the original state of the AppController.
     */
    @After
    public void tearDown() throws Exception {
        // Restore the original state of AppController
        setDataset();
        setDatasetProfile();
    }

    /**
     * Helper method to set the dataset in the AppController using reflection.
     *
     * @throws Exception if there is an issue setting the dataset field
     */
    private void setDataset() throws Exception {
        Field field = AppController.class.getDeclaredField("dataset");
        field.setAccessible(true);
        field.set(AppController.getInstance(), null);
    }

    /**
     * Helper method to set the dataset profile in the AppController using reflection.
     *
     * @throws Exception if there is an issue setting the dataset profile field
     */
    private void setDatasetProfile() throws Exception {
        Field field = AppController.class.getDeclaredField("datasetProfile");
        field.setAccessible(true);
        field.set(AppController.getInstance(), null);
    }

    /**
     * Test to verify that the ResultsPanelManager creates the correct tabs for the selected analysis types.
     * It checks if only the "Descriptive Stats" tab is created when selected.
     */
    @Test
    public void testResultsPanelManager_CreatesTabsForSelectedAnalysis() {
        // Extract the JTabbedPane for verification
        JTabbedPane tabbedPane = (JTabbedPane) resultsPanelManager.getComponent(0);

        // Verify that only the "Descriptive Stats" tab is created
        assertEquals(1, tabbedPane.getTabCount());  // Only one tab should exist
        assertEquals("Descriptive Stats", tabbedPane.getTitleAt(0)); // Verify that the tab title is correct
    }

    /**
     * Test to verify the functionality of the back button.
     * Ensures that the back button exists and has the correct label.
     */
    @Test
    public void testResultsPanelManager_BackButton() {
        // Extract the back button component
        JButton backButton = null;
        for (Component component : resultsPanelManager.getComponents()) {
            if (component instanceof JButton) {
                backButton = (JButton) component;
                break;
            }
        }

        // Verify that the back button exists and has the correct text
        assertNotNull(backButton);
        assertEquals("Back", backButton.getText());  // Verify that the button text is "Back"
    }

    /**
     * Test to verify the selected analysis list contains the correct analysis types.
     * It ensures that the "Descriptive Stats" analysis type is selected.
     */
    @Test
    public void testSelectedAnalysisList_ContainsCorrectTypes() {
        try {
            // Extract the 'selectedAnalysis' list from the ResultsPanelManager via reflection
            Field selectedAnalysisField = ResultsPanelManager.class.getDeclaredField("selectedAnalysis");
            selectedAnalysisField.setAccessible(true);
            List<AnalysisType> selectedAnalysis = (List<AnalysisType>) selectedAnalysisField.get(resultsPanelManager);

            // Verify that only one analysis type is selected and it is "Descriptive Stats"
            assertEquals(1, selectedAnalysis.size());  // Only one item should be selected
            assertEquals(AnalysisType.fromString("Descriptive Stats"), selectedAnalysis.get(0)); // Verify that the selected analysis type is correct
        } catch (Exception e) {
            fail("Reflection failed: " + e.getMessage());
        }
    }
}
