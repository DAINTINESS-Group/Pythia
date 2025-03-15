package gr.uoi.cs.pythia.gui.resultsGui;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.RegressionProfile;
import gr.uoi.cs.pythia.model.regression.RegressionType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RegressionPanelTest {

    private RegressionPanel regressionPanel;

    /**
     * Setup method before each test.
     * Initializes the RegressionPanel.
     */
    @Before
    public void setUp() throws Exception {
        regressionPanel = new RegressionPanel();
    }

    /**
     * Tear down method after each test.
     * Cleans up resources by nullifying the RegressionPanel and resetting the dataset profile.
     */
    @After
    public void tearDown() throws Exception {
        regressionPanel = null;
        setDatasetProfile(null);
        setDataset();
    }

    /**
     * Helper method to inject a fake dataset into AppController using reflection.
     *
     * @throws Exception if there is an issue accessing the field
     */
    private void setDataset() throws Exception {
        Field field = AppController.class.getDeclaredField("dataset");
        field.setAccessible(true);
        field.set(AppController.getInstance(), null);
    }

    /**
     * Helper method to inject a fake dataset profile into AppController using reflection.
     *
     * @param datasetProfile the dataset profile to be set in AppController
     * @throws Exception if there is an issue accessing the field
     */
    private void setDatasetProfile(DatasetProfile datasetProfile) throws Exception {
        Field field = AppController.class.getDeclaredField("datasetProfile");
        field.setAccessible(true);
        field.set(AppController.getInstance(), datasetProfile);
    }

    /**
     * Test to verify the panel content when regression profiles are available.
     * It ensures that the panel correctly displays regression profiles.
     */
    @Test
    public void testCreatePanelContent_WithRegressionProfiles() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            // Create a fake regression profile
            RegressionProfile regressionProfile = createFakeRegressionProfile();
            DatasetProfile datasetProfile = new DatasetProfile();
            datasetProfile.setRegressionProfiles(Collections.singletonList(regressionProfile));

            try {
                setDatasetProfile(datasetProfile);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // Call the method that creates the panel content
            regressionPanel.createPanelContent();

            // Extract the components of the panel
            Component[] components = regressionPanel.getComponents();
            assertTrue(components.length > 0); // At least one component should be added
            assertTrue(components[0] instanceof JScrollPane); // The first component should be a JScrollPane

            JScrollPane scrollPane = (JScrollPane) components[0];
            Component viewportComponent = scrollPane.getViewport().getView();
            assertTrue(viewportComponent instanceof JPanel); // The viewport should contain a JPanel

            JPanel panel = (JPanel) viewportComponent;
            assertTrue(panel.getComponentCount() > 0); // The panel should contain components
        });
    }

    /**
     * Test to verify the panel content when no regression profiles are available.
     * It ensures that the panel displays a message when no regression profiles are found.
     */
    @Test
    public void testCreatePanelContent_NoRegressionProfiles() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            // Set DatasetProfile with no regression profiles
            DatasetProfile profile = new DatasetProfile();
            try {
                setDatasetProfile(profile);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // Call the method that creates the panel content
            regressionPanel.createPanelContent();

            // Extract the components of the panel
            Component[] components = regressionPanel.getComponents();
            assertEquals(1, components.length); // Only one component should be added
            assertTrue(components[0] instanceof JLabel); // The component should be a JLabel

            JLabel label = (JLabel) components[0];
            assertEquals("No regression profiles found.", label.getText()); // Verify the label text
        });
    }


    /**
     * Helper method to create a fake RegressionProfile for testing.
     *
     * @return a fake RegressionProfile for testing purposes
     */
    private RegressionProfile createFakeRegressionProfile() {
        // Create fake independent variables
        List<Column> independentVars = Arrays.asList(
                new Column(0, "X1", "Description for X1"),
                new Column(1, "X2", "Description for X2")
        );

        // Create fake values for independent variables
        List<List<Double>> independentVarsValues = Arrays.asList(
                Arrays.asList(1.0, 2.0, 3.0),  // Values for X1
                Arrays.asList(4.0, 5.0, 6.0)   // Values for X2
        );

        // Create fake dependent variable values
        List<Double> dependentVarsValues = Arrays.asList(10.0, 20.0, 30.0);  // Values for Y

        // Create the dependent variable column
        Column dependentVar = new Column(2, "Y", "Description for Y");

        // Create the regression profile
        RegressionProfile regressionProfile = new RegressionProfile();
        regressionProfile.setIndependentVariables(independentVars);
        regressionProfile.setIndependentVariablesValues(independentVarsValues);
        regressionProfile.setDependentVariable(dependentVar);
        regressionProfile.setDependentVariableValues(dependentVarsValues);
        regressionProfile.setType(RegressionType.LINEAR);  // Example of regression type
        regressionProfile.setSlopes(Arrays.asList(2.0, 3.0));  // Example slopes
        regressionProfile.setIntercept(5.0);  // Example intercept
        regressionProfile.setCorrelations(Arrays.asList(0.9, 0.8));  // Example correlations
        regressionProfile.setpValues(Arrays.asList(0.01, 0.05));  // Example p-values
        regressionProfile.setError(0.1);  // Example error

        return regressionProfile;
    }
}
