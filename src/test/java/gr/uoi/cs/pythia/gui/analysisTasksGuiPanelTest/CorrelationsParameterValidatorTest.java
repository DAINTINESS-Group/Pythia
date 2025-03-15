package gr.uoi.cs.pythia.gui.analysisTasksGuiPanelTest;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.correlations.CorrelationsMethod;
import gr.uoi.cs.pythia.correlations.CorrelationsParameters;
import gr.uoi.cs.pythia.gui.analysisTasksGuiPanels.CorrelationsParameterValidator;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class CorrelationsParameterValidatorTest {

    private CorrelationsParameterValidator validator;
    private Map<String, JComponent> inputFields;

    /**
     * Sets up the test environment before each test case.
     * Initializes the validator, input fields, and a fake dataset for testing.
     *
     * @throws Exception if there is an issue setting up the test environment.
     */
    @Before
    public void setUp() throws Exception {
        validator = new CorrelationsParameterValidator();
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
     * Tests the validation and creation of correlation parameters with a valid Pearson method.
     * Ensures that the validator correctly creates a CorrelationsParameters object.
     */
    @Test
    public void testValidateAndCreate_ValidPearsonMethod() {
        // Create a JComboBox with a valid method (PEARSON)
        JComboBox<String> correlationMethodComboBox = new JComboBox<>(new String[]{"PEARSON", ""});
        correlationMethodComboBox.setSelectedItem("PEARSON");

        // Add the JComboBox to the inputFields map
        inputFields.put("Correlation Method", correlationMethodComboBox);

        // Call the validateAndCreate method
        CorrelationsParameters parameters = validator.validateAndCreate(inputFields);

        // Verify that the CorrelationsParameters object was created correctly
        assertNotNull(parameters);
        assertEquals(CorrelationsMethod.PEARSON, parameters.method);
    }

    /**
     * Tests the validation of correlation parameters with an invalid method.
     * Ensures that the validator returns null for an invalid method.
     */
    @Test
    public void testValidateAndCreate_InvalidMethod() {
        // Create a JComboBox with an invalid method
        JComboBox<String> correlationMethodComboBox = new JComboBox<>();
        correlationMethodComboBox.setEditable(true); // Allow any input
        correlationMethodComboBox.setSelectedItem("INVALID_METHOD");

        // Add the JComboBox to the inputFields map
        inputFields.put("Correlation Method", correlationMethodComboBox);

        // Call the validateAndCreate method
        CorrelationsParameters parameters = validator.validateAndCreate(inputFields);

        // Verify that the validator returns null for an invalid method
        assertNull(parameters);
    }

    /**
     * Tests the validation of correlation parameters with an empty method.
     * Ensures that the validator returns null when no method is selected.
     */
    @Test
    public void testValidateAndCreate_EmptyMethod() {
        // Create a JComboBox with an empty method
        JComboBox<String> correlationMethodComboBox = new JComboBox<>(new String[]{"PEARSON", ""});
        correlationMethodComboBox.setSelectedItem("");

        // Add the JComboBox to the inputFields map
        inputFields.put("Correlation Method", correlationMethodComboBox);

        // Call the validateAndCreate method
        CorrelationsParameters parameters = validator.validateAndCreate(inputFields);

        // Verify that the validator returns null for an empty method
        assertNull(parameters);
    }
}
