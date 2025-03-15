package gr.uoi.cs.pythia.gui.analysisTasksGuiPanelTest;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.gui.analysisTasksGuiPanels.HighlightParameterValidator;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.util.HighlightParameters;
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

public class HighlightParameterValidatorTest {

    private HighlightParameterValidator validator;
    private Map<String, JComponent> inputFields;

    /**
     * Sets up the test environment before each test case.
     * Initializes the validator, input fields, and a fake dataset for testing.
     *
     * @throws Exception if there is an issue setting up the test environment.
     */
    @Before
    public void setUp() throws Exception {
        validator = new HighlightParameterValidator();
        inputFields = new HashMap<>();

        // Set up the AppController with a dataset profile and dataset for testing
        setDatasetProfile(new DatasetProfile());
        setDataset(createFakeDataset()); // Create and set a fake dataset
    }

    /**
     * Helper method to set the dataset in the AppController using reflection.
     *
     * @param dataset the dataset to set.
     * @throws Exception if there is an issue accessing or setting the dataset field.
     */
    private void setDataset(Dataset<Row> dataset) throws Exception {
        Field field = AppController.class.getDeclaredField("dataset");
        field.setAccessible(true);
        field.set(AppController.getInstance(), dataset);
    }

    /**
     * Creates a fake dataset for testing purposes.
     *
     * @return a Dataset<Row> containing fake data.
     */
    private Dataset<Row> createFakeDataset() {
        SparkSession spark = SparkSession.builder().master("local").appName("FakeDataset").getOrCreate();
        List<Row> data = Arrays.asList(
                RowFactory.create("feature1", 1.0),
                RowFactory.create("feature2", 2.0),
                RowFactory.create("feature3", 3.0)
        );
        StructType schema = new StructType(new StructField[]{
                new StructField("feature1", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("feature2", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("feature3", DataTypes.DoubleType, false, Metadata.empty())
        });
        return spark.createDataFrame(data, schema);
    }

    /**
     * Helper method to set the dataset profile in the AppController using reflection.
     *
     * @param datasetProfile the dataset profile to set.
     * @throws Exception if there is an issue accessing or setting the dataset profile field.
     */
    private void setDatasetProfile(DatasetProfile datasetProfile) throws Exception {
        Field field = AppController.class.getDeclaredField("datasetProfile");
        field.setAccessible(true);
        field.set(AppController.getInstance(), datasetProfile);
    }

    /**
     * Tests the validation and creation of highlight parameters with valid inputs.
     * Ensures that the validator correctly creates a HighlightParameters object.
     */
    @Test
    public void testValidateAndCreate_ValidInputs() {
        // Create JComboBox and JTextField with valid data
        JComboBox<HighlightParameters.HighlightExtractionMode> extractionModeComboBox = new JComboBox<>(HighlightParameters.HighlightExtractionMode.values());
        extractionModeComboBox.setSelectedItem(HighlightParameters.HighlightExtractionMode.TOP);

        JTextField numericLimitField = new JTextField("10.5");

        // Add the components to the inputFields map
        inputFields.put("Extraction Mode", extractionModeComboBox);
        inputFields.put("Numeric Limit", numericLimitField);

        // Call the validateAndCreate method
        HighlightParameters parameters = validator.validateAndCreate(inputFields);

        // Verify that the HighlightParameters object was created correctly
        assertNotNull(parameters);
        assertEquals(HighlightParameters.HighlightExtractionMode.TOP, parameters.getHighlightExtractionMode());
        assertEquals(10.5, parameters.getNumericLimit(), 0.001); // Use delta for double comparison
    }

    /**
     * Tests the validation of highlight parameters with an invalid numeric limit.
     * Ensures that the validator returns null and handles the error appropriately.
     *
     * @throws InterruptedException if the thread is interrupted.
     * @throws InvocationTargetException if an exception occurs during invocation.
     */
    @Test
    public void testValidateAndCreate_InvalidNumericLimit() throws InterruptedException, InvocationTargetException {
        // Create JComboBox and JTextField with an invalid numeric limit
        JComboBox<HighlightParameters.HighlightExtractionMode> extractionModeComboBox = new JComboBox<>(HighlightParameters.HighlightExtractionMode.values());
        extractionModeComboBox.setSelectedItem(HighlightParameters.HighlightExtractionMode.TOP);

        JTextField numericLimitField = new JTextField("invalid"); // Invalid numeric limit

        // Add the components to the inputFields map
        inputFields.put("Extraction Mode", extractionModeComboBox);
        inputFields.put("Numeric Limit", numericLimitField);

        SwingUtilities.invokeAndWait(() -> {
            SwingUtilities.invokeLater(() ->AutoCloseDialog.closeErrorDialog("Input Error"));
            HighlightParameters parameters = validator.validateAndCreate(inputFields);
            assertNull(parameters);
        });
    }

    /**
     * Tests the validation of highlight parameters with an empty numeric limit.
     * Ensures that the validator returns null and handles the error appropriately.
     *
     * @throws InterruptedException if the thread is interrupted.
     * @throws InvocationTargetException if an exception occurs during invocation.
     */
    @Test
    public void testValidateAndCreate_EmptyNumericLimit() throws InterruptedException, InvocationTargetException {
        // Create JComboBox and JTextField with an empty numeric limit
        JComboBox<HighlightParameters.HighlightExtractionMode> extractionModeComboBox = new JComboBox<>(HighlightParameters.HighlightExtractionMode.values());
        extractionModeComboBox.setSelectedItem(HighlightParameters.HighlightExtractionMode.TOP);

        JTextField numericLimitField = new JTextField(""); // Empty numeric limit

        // Add the components to the inputFields map
        inputFields.put("Extraction Mode", extractionModeComboBox);
        inputFields.put("Numeric Limit", numericLimitField);

        SwingUtilities.invokeAndWait(() -> {
            SwingUtilities.invokeLater(() ->AutoCloseDialog.closeErrorDialog("Input Error"));
            HighlightParameters parameters = validator.validateAndCreate(inputFields);
            assertNull(parameters);
        });
    }
}
