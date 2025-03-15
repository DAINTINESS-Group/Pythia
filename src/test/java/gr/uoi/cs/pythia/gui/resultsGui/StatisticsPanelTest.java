package gr.uoi.cs.pythia.gui.resultsGui;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.DescriptiveStatisticsProfile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
public class StatisticsPanelTest {

    private StatisticsPanel statisticsPanel;
    private DatasetProfile fakeDatasetProfile;

    /**
     * Setup method before each test.
     * Initializes the StatisticsPanel and resets the dataset and dataset profile.
     */
    @Before
    public void setUp() throws Exception {
        statisticsPanel = new StatisticsPanel();
        setDataset();
        setDatasetProfile(null);
    }

    /**
     * Tear down method after each test.
     * Restores the original state of the AppController and clears the fake dataset profile.
     */
    @After
    public void tearDown() throws Exception {
        setDataset();
        setDatasetProfile(null);
        fakeDatasetProfile = null;
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
     * @param datasetProfile the dataset profile to be set
     * @throws Exception if there is an issue setting the dataset profile field
     */
    private void setDatasetProfile(DatasetProfile datasetProfile) throws Exception {
        Field field = AppController.class.getDeclaredField("datasetProfile");
        field.setAccessible(true);
        field.set(AppController.getInstance(), datasetProfile);
    }

    /**
     * Test to verify that the panel is created correctly when columns are present in the dataset profile.
     * It checks that a table is created with the correct number of rows and column names.
     */
    @Test
    public void testCreatePanelContent_WithColumns() throws Exception {
        // Create a fake dataset profile with columns
        fakeDatasetProfile = createFakeDatasetProfile();
        setDatasetProfile(fakeDatasetProfile);
        // Create the panel content
        statisticsPanel.createPanelContent();

        // Extract the first component from the StatisticsPanel (it should be a JScrollPane)
        Component[] components = statisticsPanel.getComponents();
        assertEquals(1, components.length);  // Verify only one component is added
        assertTrue(components[0] instanceof JScrollPane);  // The first component should be JScrollPane

        JScrollPane scrollPane = (JScrollPane) components[0];
        Component viewportComponent = scrollPane.getViewport().getView();
        assertTrue(viewportComponent instanceof JTable);  // The viewport should contain a JTable

        JTable table = (JTable) viewportComponent;
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        // Verify that the rows in the table contain the correct data
        assertEquals(2, model.getRowCount());  // Verify that the table contains two rows

        // Verify the values in the first row and column
        assertEquals("Column1", model.getValueAt(0, 0));  // Verify the name of the first column
        assertEquals("5", model.getValueAt(0, 1));  // Verify the count in the first column
    }

    /**
     * Test to verify the panel content when there are no columns in the dataset profile.
     * It checks that a label with the message "No columns found." is displayed.
     */
    @Test
    public void testCreatePanelContent_NoColumns() throws InterruptedException, InvocationTargetException{
        SwingUtilities.invokeAndWait(() ->{
            // Create an empty dataset profile
            DatasetProfile profile = new DatasetProfile();
            try {
                setDatasetProfile(profile);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // Create the panel content
            statisticsPanel.createPanelContent();

            // Extract the components from the panel
            Component[] components = statisticsPanel.getComponents();

            // Verify that only one component is added and it's a JLabel
            assertEquals(1, components.length);
            assertTrue(components[0] instanceof JLabel);

            JLabel label = (JLabel) components[0];
            // Verify that the label's text is correct
            assertEquals("No columns found.", label.getText());
        });
    }

    /**
     * Helper method to create a fake DatasetProfile with columns.
     *
     * @return a mock DatasetProfile containing columns with descriptive statistics
     */
    private DatasetProfile createFakeDatasetProfile() {
        DatasetProfile datasetProfile = new DatasetProfile();

        // Create mock columns with descriptive statistics
        Column column1 = new Column(0, "Column1", "");
        column1.setDescriptiveStatisticsProfile(new DescriptiveStatisticsProfile(
                "5", "10.0", "2.5", "7.0", "1.0", "15.0", "15.0", "15", new ArrayList<>()));

        Column column2 = new Column(1, "Column2", "");
        column2.setDescriptiveStatisticsProfile(new DescriptiveStatisticsProfile(
                "3", "20.0", "5.0", "15.0", "5.0", "30.0", "12.0", "432.0", new ArrayList<>()));

        // Add columns to the dataset profile
        datasetProfile.setColumns(Arrays.asList(column1, column2));
        return datasetProfile;
    }

}

