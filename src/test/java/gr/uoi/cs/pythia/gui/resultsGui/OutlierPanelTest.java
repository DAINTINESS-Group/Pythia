package gr.uoi.cs.pythia.gui.resultsGui;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.OutlierProfile;
import gr.uoi.cs.pythia.model.outlier.OutlierResult;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OutlierPanelTest {

    private OutlierPanel outlierPanel;

    /**
     * Setup method before each test.
     * Initializes the OutlierPanel and sets up a fake dataset for testing.
     */
    @Before
    public void setUp() throws Exception {
        outlierPanel = new OutlierPanel();
        setDataset(); // Clear any dataset
        setDatasetProfile(); // Clear any dataset profile
        Dataset<Row> fakeDataset = createFakeDataset();

        // Set AppController to use the fake dataset for testing
        setPrivateField(AppController.getInstance(), "dataset", fakeDataset);
    }

    /**
     * Tear down method after each test.
     * Resets the dataset and dataset profile to null after each test.
     */
    @After
    public void tearDown() throws Exception {
        setDataset(); // Reset dataset
        setDatasetProfile(); // Reset dataset profile
    }

    /**
     * Helper method to set private fields using reflection.
     * This method sets the private field in the given target object for testing purposes.
     *
     * @param targetObject the object whose field is to be set
     * @param fieldName the name of the field to be set
     * @param value the value to assign to the field
     * @throws NoSuchFieldException if the field cannot be found
     * @throws IllegalAccessException if the field is not accessible
     */
    private void setPrivateField(Object targetObject, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = targetObject.getClass().getDeclaredField(fieldName);
        field.setAccessible(true); // Make private field accessible
        field.set(targetObject, value);
    }

    /**
     * Helper method to set the dataset in AppController using reflection.
     *
     * @throws Exception if there is an issue accessing the field
     */
    private void setDataset() throws Exception {
        Field field = AppController.class.getDeclaredField("dataset");
        field.setAccessible(true);
        field.set(AppController.getInstance(), null);
    }

    /**
     * Helper method to set the dataset profile in AppController using reflection.
     *
     * @throws Exception if there is an issue accessing the field
     */
    private void setDatasetProfile() throws Exception {
        Field field = AppController.class.getDeclaredField("datasetProfile");
        field.setAccessible(true);
        field.set(AppController.getInstance(), null);
    }

    /**
     * Helper method to create a fake dataset for testing.
     *
     * @return a fake dataset for testing purposes
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
     * Test to verify the panel content when no columns are available.
     * It ensures that the panel displays a "No columns found." message when no columns exist in the dataset profile.
     */
    @Test
    public void testCreatePanelContent_NoColumns() throws NoSuchFieldException, IllegalAccessException {
        // Set the DatasetProfile to have no columns using reflection
        DatasetProfile datasetProfile = new DatasetProfile();
        setPrivateField(AppController.getInstance(), "datasetProfile", datasetProfile);

        // Call the method that creates the panel content
        outlierPanel.createPanelContent();

        // Extract the components of the panel
        Component[] components = outlierPanel.getComponents();

        // Verify that the panel has at least one component
        assertTrue(components.length > 0);

        // Verify that the panel contains only one component, a JLabel
        assertEquals(components.length, 1);
        assertTrue(components[0] instanceof JLabel);

        JLabel label = (JLabel) components[0];
        assertEquals("No columns found.", label.getText());
    }

    /**
     * Test to verify the panel content when outliers are present.
     * It ensures that the panel correctly displays data related to outliers.
     */
    @Test
    public void testCreatePanelContent_WithOutliers() throws NoSuchFieldException, IllegalAccessException{

        // Create fake outlier data
        List<OutlierResult> outliers = Arrays.asList(
                new OutlierResult(100.0, 1.2, 1), // value = 100.0, score = 1.2, position = 1
                new OutlierResult(150.0, 2.4, 2)  // value = 150.0, score = 2.4, position = 2
        );
        OutlierProfile outlierProfile = new OutlierProfile(outliers, "TestOutlier");

        // Create fake Column with outlier profile
        Column column = new Column(0, "feature1", ""); // Empty histogram
        column.setOutlierProfile(outlierProfile);

        // Set the DatasetProfile with the fake column using reflection
        List<Column> columns = new ArrayList<>();
        columns.add(column);
        DatasetProfile datasetProfile = new DatasetProfile();
        datasetProfile.setColumns(columns);

        setPrivateField(AppController.getInstance(), "datasetProfile", datasetProfile);

        outlierPanel.createPanelContent();

        Component[] components = outlierPanel.getComponents();
        // Verify that the panel has at least one component
        assertTrue(components.length > 0);

        // Verify that the first component is a JScrollPane
        assertTrue(components[0] instanceof JScrollPane);
        JScrollPane scrollPane = (JScrollPane) components[0];
        Component viewportComponent = scrollPane.getViewport().getView();
        assertTrue(viewportComponent instanceof JPanel);

        JPanel panel = (JPanel) viewportComponent;
        assertTrue(panel.getComponentCount() > 0);

    }
}
