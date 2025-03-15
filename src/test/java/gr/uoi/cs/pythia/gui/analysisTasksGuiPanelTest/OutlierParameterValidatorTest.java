package gr.uoi.cs.pythia.gui.analysisTasksGuiPanelTest;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.gui.analysisTasksGuiPanels.OutlierParameterValidator;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.outlier.OutlierType;
import gr.uoi.cs.pythia.outliers.OutlierParameters;
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

public class OutlierParameterValidatorTest {

    /**
     * Sets up the test environment by initializing the validator,
     * input fields, and creating a fake dataset profile.
     */
    @Before
    public void setUp() throws Exception {

        // Set up AppController with a test dataset profile and dataset
        setDatasetProfile(new DatasetProfile());
        setDataset(createFakeDataset()); // Create and set a fake dataset
    }

    /**
     * Tests the validation and creation of outlier parameters with valid input.
     * Ensures the returned parameters match the expected values.
     */
    @Test
    public void testValidateAndCreate_ValidInput() {
        // Prepare input fields
        Map<String, JComponent> inputFields = new HashMap<>();
        JComboBox<String> outlierTypeComboBox = new JComboBox<>(new String[]{"Z_SCORE", "NORMALIZED_SCORE"});
        JTextField thresholdField = new JTextField("2.5");

        inputFields.put("Select Outlier Type", outlierTypeComboBox);
        inputFields.put("Enter Threshold", thresholdField);

        // Perform validation
        OutlierParameterValidator validator = new OutlierParameterValidator();
        OutlierParameters result = validator.validateAndCreate(inputFields);

        // Verify result
        assertNotNull(result);
        assertEquals(OutlierType.Z_SCORE, result.type);
        assertEquals(2.5, result.threshold, 0.01);
    }

    /**
     * Helper method to set the dataset using reflection.
     */
    private void setDataset(Dataset<Row> dataset) throws Exception {
        Field field = AppController.class.getDeclaredField("dataset");
        field.setAccessible(true);
        field.set(AppController.getInstance(), dataset);
    }

    /**
     * Creates a fake dataset for testing purposes.
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
     * Helper method to set the dataset profile using reflection.
     */
    private void setDatasetProfile(DatasetProfile datasetProfile) throws Exception {
        Field field = AppController.class.getDeclaredField("datasetProfile");
        field.setAccessible(true);
        field.set(AppController.getInstance(), datasetProfile);
    }

    /**
     * Tests validation with an invalid threshold value (non-numeric input).
     * Ensures that the validation fails and returns a null result.
     */
    @Test
    public void testValidateAndCreate_InvalidThreshold() throws InterruptedException, InvocationTargetException {
        // Prepare invalid input fields
        Map<String, JComponent> inputFields = new HashMap<>();
        JComboBox<String> outlierTypeComboBox = new JComboBox<>(new String[]{"Z_SCORE", "NORMALIZED_SCORE"});
        JTextField thresholdField = new JTextField("invalid");
        inputFields.put("Select Outlier Type", outlierTypeComboBox);
        inputFields.put("Enter Threshold", thresholdField);

        OutlierParameterValidator validator = new OutlierParameterValidator();

        SwingUtilities.invokeAndWait(() -> {
            SwingUtilities.invokeLater(() -> AutoCloseDialog.closeErrorDialog("Input Error"));
            OutlierParameters result = validator.validateAndCreate(inputFields);
            assertNull(result);
        });
    }
}
