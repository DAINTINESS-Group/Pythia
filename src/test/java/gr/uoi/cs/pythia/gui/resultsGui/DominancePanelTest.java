package gr.uoi.cs.pythia.gui.resultsGui;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.PatternsProfile;
import gr.uoi.cs.pythia.model.dominance.DominanceResult;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class DominancePanelTest {

    private DominancePanel dominancePanel;
    private List<DominanceResult> highDominanceResults;
    private List<DominanceResult> lowDominanceResults;

    /**
     * Setup method before each test.
     * Initializes the DominancePanel and sets up a fake dataset and profile for testing.
     */
    @Before
    public void setUp() throws Exception {
        dominancePanel = new DominancePanel();

        // Initialize with a fake dataset and dataset profile
        setDataset(null);
        setDatasetProfile(null);
        setDataset(createFakeDataset());
        setDatasetProfile(createFakeDominanceProfile());
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
     * This injects the dataset into AppController for testing purposes.
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
     * This injects the dataset profile into AppController for testing purposes.
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
     * Helper method to create a fake dataset for testing purposes.
     *
     * @return a fake dataset
     */
    private Dataset<Row> createFakeDataset() {
        SparkSession spark = SparkSession.builder().master("local").appName("FakeDataset").getOrCreate();
        List<Row> data = Arrays.asList(
                RowFactory.create(1.0, 2.0, 3.0),
                RowFactory.create(4.0, 5.0, 6.0),
                RowFactory.create(7.0, 8.0, 9.0)
        );
        StructType schema = new StructType(new StructField[] {
                new StructField("feature1", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("feature2", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("feature3", DataTypes.DoubleType, false, Metadata.empty())
        });
        return spark.createDataFrame(data, schema);
    }

    /**
     * Helper method to create a fake dominance profile for testing purposes.
     *
     * @return a fake dataset profile with high and low dominance results
     */
    private DatasetProfile createFakeDominanceProfile() {
        highDominanceResults = Arrays.asList(
                new DominanceResult("TypeA", "Sum", "feature1", "feature2"),
                new DominanceResult("TypeB", "Avg", "feature3", "feature1")
        );
        lowDominanceResults = Collections.singletonList(
                new DominanceResult("TypeC", "Min", "feature2", "feature3")
        );

        PatternsProfile patternsProfile = new PatternsProfile();
        patternsProfile.setHighDominanceResults(highDominanceResults);
        patternsProfile.setLowDominanceResults(lowDominanceResults);

        DatasetProfile datasetProfile = new DatasetProfile();
        datasetProfile.setPatternsProfile(patternsProfile);

        return datasetProfile;
    }

    /**
     * Test to verify that the panel content is created correctly when there are dominance results.
     * Verifies that the correct components (JPanels, JScrollPanes, JTextAreas) are displayed for high and low dominance results.
     */
    @Test
    public void testCreatePanelContent_WithDominanceResults() {
        dominancePanel.createPanelContent();

        // Verify that the panel has components
        Component[] components = dominancePanel.getComponents();
        assertTrue(components.length > 0);
        assertTrue(components[0] instanceof JPanel);

        JPanel mainPanel = (JPanel) components[0];
        assertEquals(2, mainPanel.getComponentCount()); // Expecting 2 panels: highPanel and lowPanel

        // Verify the highPanel
        JPanel highPanel = (JPanel) mainPanel.getComponent(0);
        assertTrue(highPanel.getComponentCount() > 0);
        Component firstHighComponent = highPanel.getComponent(0);
        assertTrue(firstHighComponent instanceof JLabel);
        Component secondHighComponent = highPanel.getComponent(1);
        assertTrue(secondHighComponent instanceof JScrollPane);

        JScrollPane highScrollPane = (JScrollPane) secondHighComponent;
        JTextArea highTextArea = (JTextArea) highScrollPane.getViewport().getView();

        // Build the expected content for high dominance results
        String expectedHighText = buildDominanceResultString(highDominanceResults.get(0));

        // Verify the content of the highTextArea
        assertNotNull("High text area should not be null", highTextArea);
        assertTrue("Text area should match expected content for high dominance results", highTextArea.getText().contains(expectedHighText));

        // Verify the lowPanel
        JPanel lowPanel = (JPanel) mainPanel.getComponent(1);
        assertTrue(lowPanel.getComponentCount() > 0);
        Component firstLowComponent = lowPanel.getComponent(0);
        assertTrue(firstLowComponent instanceof JLabel);
        Component secondLowComponent = lowPanel.getComponent(1);
        assertTrue(secondLowComponent instanceof JScrollPane);

        JScrollPane lowScrollPane = (JScrollPane) secondLowComponent;
        JTextArea lowTextArea = (JTextArea) lowScrollPane.getViewport().getView();

        // Build the expected content for low dominance results
        String expectedLowText = buildDominanceResultString(lowDominanceResults.get(0));

        // Verify the content of the lowTextArea
        assertNotNull("Low text area should not be null", lowTextArea);
        assertTrue("Text area should match expected content for low dominance results", lowTextArea.getText().contains(expectedLowText));
    }

    /**
     * Helper method to build the expected dominance result string.
     * This method constructs the expected content string for dominance results.
     *
     * @param dominanceResult the dominance result to be used for building the string
     * @return the formatted string for dominance result
     */
    private String buildDominanceResultString(DominanceResult dominanceResult) {
        StringBuilder resultString = new StringBuilder();

        // Add horizontal separator line
        resultString.append("--------------------------------------------------\n");

        // Add title
        resultString.append(dominanceResult.titleToString()).append("\n");

        // Add metadata
        resultString.append("Metadata:\n");
        resultString.append(dominanceResult.metadataToString()).append("\n");

        // Add detailed results
        resultString.append("Detailed Results:\n");
        resultString.append(dominanceResult.identificationResultsToString(true)).append("\n");

        // Add identified dominance features
        resultString.append("Identified Dominance Features:\n");
        resultString.append(dominanceResult.dominanceToString(true)).append("\n");

        // Add query results if two coordinates exist
        if (dominanceResult.hasTwoCoordinates()) {
            resultString.append("Query Results:\n");
            resultString.append(dominanceResult.queryResultToString()).append("\n");
        }

        // Add horizontal separator line
        resultString.append("--------------------------------------------------\n");

        return resultString.toString();
    }
}
