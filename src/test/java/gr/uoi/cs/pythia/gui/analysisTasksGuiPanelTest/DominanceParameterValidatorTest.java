package gr.uoi.cs.pythia.gui.analysisTasksGuiPanelTest;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.gui.analysisTasksGuiPanels.DominanceParameterValidator;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.patterns.dominance.DominanceColumnSelectionMode;
import gr.uoi.cs.pythia.patterns.dominance.DominanceParameters;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
public class DominanceParameterValidatorTest {

    private DominanceParameterValidator validator;
    private Map<String, JComponent> inputFields;

    /**
     * Setup method before each test.
     * Initializes the validator and inputFields map, and sets the dataset and dataset profile.
     */
    @Before
    public void setUp() throws Exception {
        validator = new DominanceParameterValidator();
        inputFields = new HashMap<>();

        // Set dataset profile and dataset for testing purposes
        setDatasetProfile(new DatasetProfile());
        setDataset(createFakeDataset());
    }

    /**
     * Helper method to set the dataset in the AppController using reflection.
     *
     * @param dataset the dataset to be set
     * @throws Exception if there is an issue setting the dataset field
     */
    private void setDataset(Dataset<Row> dataset) throws Exception {
        Field field = AppController.class.getDeclaredField("dataset");
        field.setAccessible(true);
        field.set(AppController.getInstance(), dataset);
    }

    /**
     * Helper method to create a fake dataset for testing.
     *
     * @return a fake dataset with some sample data
     */
    private Dataset<Row> createFakeDataset() {
        SparkSession spark = SparkSession.builder().master("local").appName("FakeDataset").getOrCreate();
        List<Row> data = Arrays.asList(
                RowFactory.create("feature1", 1.0),
                RowFactory.create("feature2", 2.0),
                RowFactory.create("feature3", 3.0)
        );
        StructType schema = new StructType(new StructField[]{
                new StructField("column1", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("column2", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("column3", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("column4", DataTypes.DoubleType, false, Metadata.empty())
        });
        return spark.createDataFrame(data, schema);
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
     * Test the validation and creation of DominanceParameters with valid inputs.
     * It verifies that the DominanceParameters are correctly created with the expected values.
     */
    @Test
    public void testValidateAndCreate_ValidInputs() {
        // Create JComboBox and JTextField with valid inputs
        JComboBox<DominanceColumnSelectionMode> selectionModeComboBox = new JComboBox<>(DominanceColumnSelectionMode.values());
        selectionModeComboBox.setSelectedItem(DominanceColumnSelectionMode.USER_SPECIFIED_ONLY);

        JTextField measurementColumnsField = new JTextField("column1,column2");
        JTextField coordinateColumnsField = new JTextField("column4,column3");

        inputFields.put("Selection Mode", selectionModeComboBox);
        inputFields.put("Measurement Columns", measurementColumnsField);
        inputFields.put("Coordinate Columns", coordinateColumnsField);

        DominanceParameters parameters = validator.validateAndCreate(inputFields);

        // Verify that the parameters are correctly set
        assertNotNull(parameters);
        assertEquals(DominanceColumnSelectionMode.USER_SPECIFIED_ONLY, parameters.getColumnSelectionMode());
        assertArrayEquals(new String[]{"column1", "column2"}, parameters.getMeasurementColumns());
        assertArrayEquals(new String[]{"column4", "column3"}, parameters.getCoordinateColumns());
    }

    /**
     * Test the validation and creation of DominanceParameters when measurement columns are empty.
     * It verifies that the parameters are not created and null is returned.
     */
    @Test
    public void testValidateAndCreate_EmptyMeasurementColumns() throws InterruptedException, InvocationTargetException {
        // Create JComboBox and JTextField with empty measurement columns
        JComboBox<DominanceColumnSelectionMode> selectionModeComboBox = new JComboBox<>(DominanceColumnSelectionMode.values());
        selectionModeComboBox.setSelectedItem(DominanceColumnSelectionMode.USER_SPECIFIED_ONLY);

        JTextField measurementColumnsField = new JTextField(""); // Empty measurement columns
        JTextField coordinateColumnsField = new JTextField("col3, col4");

        inputFields.put("Selection Mode", selectionModeComboBox);
        inputFields.put("Measurement Columns", measurementColumnsField);
        inputFields.put("Coordinate Columns", coordinateColumnsField);

        SwingUtilities.invokeAndWait(() -> {
            SwingUtilities.invokeLater(() -> AutoCloseDialog.closeErrorDialog("Input Error"));
            DominanceParameters parameters = validator.validateAndCreate(inputFields);
            assertNull(parameters);  // Verify that parameters are null due to invalid input
        });
    }

    /**
     * Test the validation and creation of DominanceParameters when coordinate columns are empty.
     * It verifies that the parameters are not created and null is returned.
     */
    @Test
    public void testValidateAndCreate_EmptyCoordinateColumns() throws InterruptedException, InvocationTargetException {
        // Create JComboBox and JTextField with empty coordinate columns
        JComboBox<DominanceColumnSelectionMode> selectionModeComboBox = new JComboBox<>(DominanceColumnSelectionMode.values());
        selectionModeComboBox.setSelectedItem(DominanceColumnSelectionMode.USER_SPECIFIED_ONLY);

        JTextField measurementColumnsField = new JTextField("col1, col2");
        JTextField coordinateColumnsField = new JTextField(""); // Empty coordinate columns

        inputFields.put("Selection Mode", selectionModeComboBox);
        inputFields.put("Measurement Columns", measurementColumnsField);
        inputFields.put("Coordinate Columns", coordinateColumnsField);

        SwingUtilities.invokeAndWait(() -> {
            SwingUtilities.invokeLater(() -> AutoCloseDialog.closeErrorDialog("Input Error"));
            DominanceParameters parameters = validator.validateAndCreate(inputFields);
            assertNull(parameters);  // Verify that parameters are null due to invalid input
        });
    }

    /**
     * Test the validation and creation of DominanceParameters when measurement columns are invalid.
     * It verifies that the parameters are not created and null is returned.
     */
    @Test
    public void testValidateAndCreate_InvalidMeasurementColumns() throws InterruptedException, InvocationTargetException {
        // Create JComboBox and JTextField with invalid measurement columns
        JComboBox<DominanceColumnSelectionMode> selectionModeComboBox = new JComboBox<>(DominanceColumnSelectionMode.values());
        selectionModeComboBox.setSelectedItem(DominanceColumnSelectionMode.USER_SPECIFIED_ONLY);

        JTextField measurementColumnsField = new JTextField(",,,"); // Invalid measurement columns
        JTextField coordinateColumnsField = new JTextField("col3, col4");

        inputFields.put("Selection Mode", selectionModeComboBox);
        inputFields.put("Measurement Columns", measurementColumnsField);
        inputFields.put("Coordinate Columns", coordinateColumnsField);

        SwingUtilities.invokeAndWait(() -> {
            SwingUtilities.invokeLater(() -> AutoCloseDialog.closeErrorDialog("Input Error"));
            DominanceParameters parameters = validator.validateAndCreate(inputFields);
            assertNull(parameters);  // Verify that parameters are null due to invalid input
        });
    }

    /**
     * Test the validation and creation of DominanceParameters when coordinate columns are invalid.
     * It verifies that the parameters are not created and null is returned.
     */
    @Test
    public void testValidateAndCreate_InvalidCoordinateColumns() throws InterruptedException, InvocationTargetException {
        // Create JComboBox and JTextField with invalid coordinate columns
        JComboBox<DominanceColumnSelectionMode> selectionModeComboBox = new JComboBox<>(DominanceColumnSelectionMode.values());
        selectionModeComboBox.setSelectedItem(DominanceColumnSelectionMode.USER_SPECIFIED_ONLY);

        JTextField measurementColumnsField = new JTextField("col1, col2");
        JTextField coordinateColumnsField = new JTextField(",,,"); // Invalid coordinate columns

        inputFields.put("Selection Mode", selectionModeComboBox);
        inputFields.put("Measurement Columns", measurementColumnsField);
        inputFields.put("Coordinate Columns", coordinateColumnsField);

        SwingUtilities.invokeAndWait(() -> {
            SwingUtilities.invokeLater(() -> AutoCloseDialog.closeErrorDialog("Input Error"));
            DominanceParameters parameters = validator.validateAndCreate(inputFields);
            assertNull(parameters);  // Verify that parameters are null due to invalid input
        });
    }
}
