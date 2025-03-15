package gr.uoi.cs.pythia.gui.resultsGui;

import gr.uoi.cs.pythia.Appcontroller.AppController;
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
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LabelingPanelTest {

    private LabelingPanel labelingPanel;

    /**
     * Setup method before each test.
     * Initializes the LabelingPanel and sets up a fake dataset for testing.
     */
    @Before
    public void setUp() throws Exception {
        labelingPanel = new LabelingPanel();
        setDataset();
        setDatasetProfile();
        Dataset<Row> fakeDataset = createFakeDataset();
        setPrivateField(AppController.getInstance(), fakeDataset);
    }

    /**
     * Helper method to set private fields using reflection.
     * This method sets the private field in the given target object for testing purposes.
     *
     * @param targetObject the object whose field is to be set
     * @param value        the value to assign to the field
     * @throws NoSuchFieldException   if the field cannot be found
     * @throws IllegalAccessException if the field is not accessible
     */
    private void setPrivateField(Object targetObject, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = targetObject.getClass().getDeclaredField("dataset");
        field.setAccessible(true); // Make private field accessible
        field.set(targetObject, value);
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

        // Create fake data rows
        List<Row> rows = Arrays.asList(
                RowFactory.create("John", 25, "M"),
                RowFactory.create("Jane", 30, "F"),
                RowFactory.create("Doe", 22, "M")
        );

        // Define schema for the fake dataset
        StructType schema = new StructType(new StructField[] {
                new StructField("Name", DataTypes.StringType, false, Metadata.empty()),
                new StructField("Age", DataTypes.IntegerType, false, Metadata.empty()),
                new StructField("Gender", DataTypes.StringType, false, Metadata.empty())
        });

        return spark.createDataFrame(rows, schema);
    }

    /**
     * Test to verify that the panel content is correctly created with a fake dataset.
     * It ensures the panel contains a JTable populated with the expected dataset values.
     */
    @Test
    public void testCreatePanelContent_WithFakeDataset(){
        // Call the method that creates the panel content
        labelingPanel.createPanelContent();

        // Extract the components of the panel
        Component[] components = labelingPanel.getComponents();

        // Verify that the panel has at least one component
        assertTrue(components.length > 0);

        // Verify that the first component is a JScrollPane (for the table)
        assertTrue(components[1] instanceof JScrollPane);

        JScrollPane scrollPane = (JScrollPane) components[1];
        Component viewportComponent = scrollPane.getViewport().getView();

        // Verify that the component inside the JScrollPane is a JTable
        assertTrue(viewportComponent instanceof JTable);

        JTable table = (JTable) viewportComponent;

        // Verify that the table has the correct columns
        assertEquals(3, table.getColumnCount());  // Name, Age, Gender
        assertEquals("Name", table.getColumnName(0));
        assertEquals("Age", table.getColumnName(1));
        assertEquals("Gender", table.getColumnName(2));

        // Verify that the table has the correct data
        assertEquals("John", table.getValueAt(0, 0));
        assertEquals(25, table.getValueAt(0, 1));
        assertEquals("M", table.getValueAt(0, 2));
    }

    /**
     * Test to verify that the panel content is correctly created when no dataset is available.
     * It ensures that the panel displays a "No data available" message when the dataset is null.
     */
    @Test
    public void testCreatePanelContent_WithNoDataset() throws Exception {
        // Set the dataset to null
        setDataset();

        // Call the method that creates the panel content
        labelingPanel.createPanelContent();

        // Extract the components of the panel
        Component[] components = labelingPanel.getComponents();

        // Verify that the panel has at least one component
        assertTrue(components.length > 0);

        // Verify that the panel contains a JLabel with the message "No data available."
        assertTrue(components[0] instanceof JLabel);
        JLabel label = (JLabel) components[0];
        assertEquals("No data available.", label.getText());
    }
}

