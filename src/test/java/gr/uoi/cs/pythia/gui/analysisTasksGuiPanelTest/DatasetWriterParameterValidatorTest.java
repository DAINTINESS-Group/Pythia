package gr.uoi.cs.pythia.gui.analysisTasksGuiPanelTest;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.gui.analysisTasksGuiPanels.DatasetWriterParameterValidator;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.writer.DatasetWriterConstants;
import gr.uoi.cs.pythia.writer.DatasetWriterParameters;
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

import static gr.uoi.cs.pythia.gui.analysisTasksGuiPanelTest.AutoCloseDialog.closeErrorDialog;
import static org.junit.Assert.*;

public class DatasetWriterParameterValidatorTest {

    private DatasetWriterParameterValidator validator;
    private Map<String, JComponent> inputFields;

    /**
     * Sets up the test environment before each test case.
     * Initializes the validator, input fields, and a fake dataset for testing.
     *
     * @throws Exception if there is an issue setting up the test environment.
     */
    @Before
    public void setUp() throws Exception {
        validator = new DatasetWriterParameterValidator();
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
     * Tests the validation and creation of dataset writer parameters with valid inputs.
     * Ensures that the validator correctly creates a DatasetWriterParameters object.
     */
    @Test
    public void testValidateAndCreate_ValidInputs() {
        // Create JTextField and JComboBox with valid data
        JTextField datasetAliasField = new JTextField("my_dataset");
        JComboBox<String> writerTypeComboBox = new JComboBox<>(new String[]{DatasetWriterConstants.HADOOP, DatasetWriterConstants.NAIVE});
        writerTypeComboBox.setSelectedItem(DatasetWriterConstants.HADOOP);
        JTextField outputPathField = new JTextField("/path/to/output");

        // Add the components to the inputFields map
        inputFields.put("Dataset Alias", datasetAliasField);
        inputFields.put("Writer Type", writerTypeComboBox);
        inputFields.put("Output Path", outputPathField);

        // Call the validateAndCreate method
        DatasetWriterParameters parameters = validator.validateAndCreate(inputFields);

        // Verify that the DatasetWriterParameters object was created correctly
        assertNotNull(parameters);
        assertEquals("my_dataset", parameters.alias);
        assertEquals(DatasetWriterConstants.HADOOP, parameters.writerType);
        assertEquals("/path/to/output", parameters.path);
    }

    /**
     * Tests the validation of dataset writer parameters with an empty alias.
     * Ensures that the validator returns null when the alias is empty.
     *
     * @throws InterruptedException if the thread is interrupted.
     * @throws InvocationTargetException if an exception occurs during invocation.
     */
    @Test
    public void testValidateAndCreate_EmptyAlias() throws InterruptedException, InvocationTargetException {
        // Create JTextField and JComboBox with an empty alias
        JTextField datasetAliasField = new JTextField(""); // Empty alias
        JComboBox<String> writerTypeComboBox = new JComboBox<>(new String[]{DatasetWriterConstants.HADOOP, DatasetWriterConstants.NAIVE});
        writerTypeComboBox.setSelectedItem(DatasetWriterConstants.HADOOP);
        JTextField outputPathField = new JTextField("/path/to/output");

        // Add the components to the inputFields map
        inputFields.put("Dataset Alias", datasetAliasField);
        inputFields.put("Writer Type", writerTypeComboBox);
        inputFields.put("Output Path", outputPathField);

        SwingUtilities.invokeAndWait(() -> {
            SwingUtilities.invokeLater(() ->closeErrorDialog("Error"));
            DatasetWriterParameters parameters = validator.validateAndCreate(inputFields);
            assertNull(parameters);
        });
    }

    /**
     * Tests the validation of dataset writer parameters with an empty output path.
     * Ensures that the validator returns null when the output path is empty.
     *
     * @throws InterruptedException if the thread is interrupted.
     * @throws InvocationTargetException if an exception occurs during invocation.
     */
    @Test
    public void testValidateAndCreate_EmptyPath() throws InterruptedException, InvocationTargetException {
        // Create JTextField and JComboBox with an empty output path
        JTextField datasetAliasField = new JTextField("my_dataset");
        JComboBox<String> writerTypeComboBox = new JComboBox<>(new String[]{DatasetWriterConstants.HADOOP, DatasetWriterConstants.NAIVE});
        writerTypeComboBox.setSelectedItem(DatasetWriterConstants.HADOOP);
        JTextField outputPathField = new JTextField(""); // Empty path

        // Add the components to the inputFields map
        inputFields.put("Dataset Alias", datasetAliasField);
        inputFields.put("Writer Type", writerTypeComboBox);
        inputFields.put("Output Path", outputPathField);
        SwingUtilities.invokeAndWait(() -> {
            SwingUtilities.invokeLater(() ->closeErrorDialog("Error"));
            DatasetWriterParameters parameters = validator.validateAndCreate(inputFields);
            assertNull(parameters);
        });
    }
}