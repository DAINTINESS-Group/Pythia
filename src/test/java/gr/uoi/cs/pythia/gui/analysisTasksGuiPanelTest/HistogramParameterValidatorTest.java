package gr.uoi.cs.pythia.gui.analysisTasksGuiPanelTest;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.gui.analysisTasksGuiPanels.HistogramParameterValidator;
import gr.uoi.cs.pythia.histogram.generator.HistogramGeneratorType;
import gr.uoi.cs.pythia.histogram.generator.HistogramParameters;
import gr.uoi.cs.pythia.model.DatasetProfile;
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

public class HistogramParameterValidatorTest {

    private HistogramParameterValidator validator;
    private Map<String, JComponent> inputFields;

    /**
     * Sets up the test environment before each test case.
     * Initializes the validator, input fields, and a fake dataset for testing.
     *
     * @throws Exception if there is an issue setting up the test environment.
     */
    @Before
    public void setUp() throws Exception {
        validator = new HistogramParameterValidator();
        inputFields = new HashMap<>();
        setDatasetProfile(new DatasetProfile());
        setDataset(createFakeDataset());
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
     * Tests the validation and creation of histogram parameters with valid inputs.
     * Ensures that the validator correctly creates a HistogramParameters object.
     */
    @Test
    public void testValidateAndCreate_ValidInputs() {
        // Create JComboBox and JTextField with valid data
        JComboBox<String> histogramTypeComboBox = new JComboBox<>(new String[]{"KEEP_NANS", "SKIP_NANS"});
        histogramTypeComboBox.setSelectedItem("KEEP_NANS");

        JTextField numberOfBinsField = new JTextField("10");

        // Add the components to the inputFields map
        inputFields.put("Histogram Type", histogramTypeComboBox);
        inputFields.put("Number of Bins", numberOfBinsField);

        // Call the validateAndCreate method
        HistogramParameters parameters = validator.validateAndCreate(inputFields);

        // Verify that the HistogramParameters object was created correctly
        assertNotNull(parameters);
        assertEquals(HistogramGeneratorType.KEEP_NANS, parameters.getType());
        assertEquals(10, parameters.getNumberOfBins());
    }

    /**
     * Tests the validation of histogram parameters with an invalid number of bins.
     * Ensures that the validator returns null and handles the error appropriately.
     *
     * @throws InterruptedException if the thread is interrupted.
     * @throws InvocationTargetException if an exception occurs during invocation.
     */
    @Test
    public void testValidateAndCreate_InvalidBins() throws InterruptedException, InvocationTargetException {
        // Create JComboBox and JTextField with an invalid number of bins
        JComboBox<String> histogramTypeComboBox = new JComboBox<>(new String[]{"KEEP_NANS", "SKIP_NANS"});
        histogramTypeComboBox.setSelectedItem("SKIP_NANS");

        JTextField numberOfBinsField = new JTextField("invalid"); // Invalid number of bins

        // Add the components to the inputFields map
        inputFields.put("Histogram Type", histogramTypeComboBox);
        inputFields.put("Number of Bins", numberOfBinsField);

        SwingUtilities.invokeAndWait(() -> {
            SwingUtilities.invokeLater(() -> AutoCloseDialog.closeErrorDialog("Input Error"));
            HistogramParameters parameters = validator.validateAndCreate(inputFields);
            assertNull(parameters);
        });
    }

    /**
     * Tests the validation of histogram parameters with an empty number of bins.
     * Ensures that the validator returns null and handles the error appropriately.
     *
     * @throws InterruptedException if the thread is interrupted.
     * @throws InvocationTargetException if an exception occurs during invocation.
     */
    @Test
    public void testValidateAndCreate_EmptyBins() throws InterruptedException, InvocationTargetException {
        // Create JComboBox and JTextField with an empty number of bins
        JComboBox<String> histogramTypeComboBox = new JComboBox<>(new String[]{"KEEP_NANS", "SKIP_NANS"});
        histogramTypeComboBox.setSelectedItem("SKIP_NANS");

        JTextField numberOfBinsField = new JTextField(""); // Empty number of bins

        // Add the components to the inputFields map
        inputFields.put("Histogram Type", histogramTypeComboBox);
        inputFields.put("Number of Bins", numberOfBinsField);

        SwingUtilities.invokeAndWait(() -> {
            SwingUtilities.invokeLater(() -> AutoCloseDialog.closeErrorDialog("Input Error"));
            HistogramParameters parameters = validator.validateAndCreate(inputFields);
            assertNull(parameters);
        });
    }
}