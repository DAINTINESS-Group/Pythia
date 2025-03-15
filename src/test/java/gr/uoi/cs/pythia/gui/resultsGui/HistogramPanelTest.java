package gr.uoi.cs.pythia.gui.resultsGui;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.histogram.Bin;
import gr.uoi.cs.pythia.model.histogram.Histogram;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.jfree.chart.ChartPanel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class HistogramPanelTest {

    private HistogramPanel histogramPanel;

    /**
     * Setup method before each test.
     * Initializes the HistogramPanel and sets up a fake dataset and profile for testing.
     */
    @Before
    public void setUp() throws Exception {
        histogramPanel = new HistogramPanel();

        // Initialize with a fake dataset and dataset profile
        setDataset(null);
        setDatasetProfile(null);
        setDataset(createFakeDataset());
        setDatasetProfile(createFakeDatasetProfile());
    }

    /**
     * Tear down method after each test.
     * Resets the dataset and dataset profile to null after each test.
     */
    @After
    public void tearDown() throws Exception {
        setDataset(null); // Reset the dataset
        setDatasetProfile(null); // Reset the dataset profile
    }

    /**
     * Helper method to inject a fake dataset into AppController using reflection.
     * This method sets the dataset field in AppController for testing purposes.
     *
     * @param dataset the dataset to be set in AppController
     * @throws Exception if there is an issue accessing the field
     */
    private void setDataset(Dataset<Row> dataset) throws Exception {
        Field field = AppController.class.getDeclaredField("dataset");
        field.setAccessible(true);
        field.set(AppController.getInstance(), dataset);
    }

    /**
     * Helper method to inject a fake dataset profile into AppController using reflection.
     * This method sets the dataset profile field in AppController for testing purposes.
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
     * Helper method to create a fake dataset.
     *
     * @return a fake dataset (currently returning null for testing)
     */
    private Dataset<Row> createFakeDataset() {
        // Your code to create a fake dataset goes here
        // Return the dataset (mocking it for the purpose of testing)
        return null;
    }

    /**
     * Helper method to create a fake dataset profile with histogram data.
     * This method creates fake columns with histograms for testing purposes.
     *
     * @return a dataset profile with columns containing histograms
     */
    private DatasetProfile createFakeDatasetProfile() {
        // Create fake columns with histograms
        List<Column> columns = new ArrayList<>();
        columns.add(createFakeColumn("Column1"));
        columns.add(createFakeColumn("Column2"));

        DatasetProfile datasetProfile = new DatasetProfile();
        datasetProfile.setColumns(columns);

        return datasetProfile;
    }

    /**
     * Helper method to create a fake column with histogram data.
     *
     * @param name the name of the column
     * @return a column with histogram data
     */
    private Column createFakeColumn(String name) {
        // Create fake bins for histogram data
        List<Bin> bins = Arrays.asList(
                new Bin(0.0, 1.0, 10, true),   // isUpperBoundIncluded = true
                new Bin(1.0, 2.0, 20, false),  // isUpperBoundIncluded = false
                new Bin(2.0, 3.0, 30, true)    // isUpperBoundIncluded = true
        );
        Histogram histogram = new Histogram(name, bins);
        Column column = new Column(0, name, "");
        column.setHistogram(histogram);
        return column;
    }

    /**
     * Test to verify that the panel content is created correctly.
     * Verifies that the histogram panel is populated with the expected components
     * such as JScrollPane, JPanel, and ChartPanel containing a JFreeChart.
     */
    @Test
    public void testCreatePanelContent() {
        histogramPanel.createPanelContent();

        // Check that the panel has been populated with the expected components
        Component[] components = histogramPanel.getComponents();
        assertTrue("Panel should contain components", components.length > 0);
        assertTrue("First component should be a JScrollPane", components[0] instanceof JScrollPane);

        JScrollPane scrollPane = (JScrollPane) components[0];
        JPanel chartsContainer = (JPanel) scrollPane.getViewport().getView();
        assertTrue("Charts container should contain at least one panel", chartsContainer.getComponentCount() > 0);

        // Verify that each column's histogram has been added to the container
        Component firstPanel = chartsContainer.getComponent(0);
        assertTrue("First panel should be a JPanel", firstPanel instanceof JPanel);

        JPanel firstHistogramPanel = (JPanel) firstPanel;
        assertTrue("First histogram panel should have a ChartPanel", firstHistogramPanel.getComponentCount() > 0);
        assertTrue("The chart panel should contain a JFreeChart", firstHistogramPanel.getComponent(0) instanceof ChartPanel);
    }
}
