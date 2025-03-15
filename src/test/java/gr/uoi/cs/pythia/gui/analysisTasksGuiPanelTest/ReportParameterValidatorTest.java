package gr.uoi.cs.pythia.gui.analysisTasksGuiPanelTest;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.gui.analysisTasksGuiPanels.ReportParameterValidator;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.report.ReportGeneratorConstants;
import gr.uoi.cs.pythia.report.ReportParameters;
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

public class ReportParameterValidatorTest {

    private ReportParameterValidator validator;
    private Map<String, JComponent> inputFields;

    @Before
    public void setUp() throws Exception {
        validator = new ReportParameterValidator();
        inputFields = new HashMap<>();
        // Set up the AppController with a fake dataset and dataset profile for the tests
        setDatasetProfile(new DatasetProfile());
        setDataset(createFakeDataset());
    }

    // Helper method to set the dataset using reflection
    private void setDataset(Dataset<Row> dataset) throws Exception {
        Field field = AppController.class.getDeclaredField("dataset");
        field.setAccessible(true);
        field.set(AppController.getInstance(), dataset);
    }

    // Helper method to create a fake dataset
    private Dataset<Row> createFakeDataset() {
        SparkSession spark = SparkSession.builder().master("local").appName("FakeDataset").getOrCreate();
        List<Row> data = Arrays.asList(
                RowFactory.create("feature1", 1.0),
                RowFactory.create("feature2", 2.0),
                RowFactory.create("feature3", 3.0)
        );
        StructType schema = new StructType(new StructField[] {
                new StructField("feature1", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("feature2", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("feature3", DataTypes.DoubleType, false, Metadata.empty())
        });
        return spark.createDataFrame(data, schema);
    }

    // Helper method to set the dataset profile using reflection
    private void setDatasetProfile(DatasetProfile datasetProfile) throws Exception {
        Field field = AppController.class.getDeclaredField("datasetProfile");
        field.setAccessible(true);
        field.set(AppController.getInstance(), datasetProfile);
    }

    /**
     * Test the ReportParameterValidator with valid input.
     * It ensures that valid report parameters are created when correct input is provided.
     */
    @Test
    public void testValidateAndCreate_ValidInput() {
        // Create input fields with valid data
        JComboBox<String> reportTypeComboBox = new JComboBox<>(new String[]{"TXT", "JSON", "MD"});
        JTextField savePathField = new JTextField();

        // Set valid values for input fields
        reportTypeComboBox.setSelectedItem("TXT");
        savePathField.setText("/valid/path");

        // Add input fields to the map
        inputFields.put("Report Type", reportTypeComboBox);
        inputFields.put("Save Path", savePathField);

        // Validate and create report parameters
        ReportParameters parameters = validator.validateAndCreate(inputFields);

        // Assertions
        assertNotNull(parameters);
        assertEquals(ReportGeneratorConstants.TXT_REPORT, parameters.type);
        assertEquals("/valid/path", parameters.path);
    }

    /**
     * Test the ReportParameterValidator with an empty path.
     * This test checks if the validation correctly fails when the path is empty.
     */
    @Test
    public void testValidateAndCreate_EmptyPath() throws InterruptedException, InvocationTargetException {
        JComboBox<String> reportTypeComboBox = new JComboBox<>(new String[]{"TXT", "JSON", "MD"});
        JTextField savePathField = new JTextField();
        reportTypeComboBox.setSelectedItem("JSON");
        savePathField.setText("");  // Empty path field
        inputFields.put("Report Type", reportTypeComboBox);
        inputFields.put("Save Path", savePathField);

        SwingUtilities.invokeAndWait(() -> {
            SwingUtilities.invokeLater(() -> AutoCloseDialog.closeErrorDialog("Error"));
            // Validate and ensure parameters are not created due to invalid input
            ReportParameters parameters = validator.validateAndCreate(inputFields);
            assertNull(parameters);
        });
    }

    /**
     * Test the ReportParameterValidator with null input fields.
     * This test ensures that null input fields result in null report parameters.
     */
    @Test
    public void testValidateAndCreate_NullInputFields() {
        // Validate with null input fields
        ReportParameters parameters = validator.validateAndCreate(null);
        assertNull(parameters);  // Expecting null as there are no input fields
    }

    /**
     * Test the ReportParameterValidator with missing report type selection.
     * It ensures that the validation fails when the report type is not selected.
     */
    @Test
    public void testValidateAndCreate_MissingReportTypeSelection() throws InterruptedException, InvocationTargetException {
        JTextField savePathField = new JTextField();
        savePathField.setText("/valid/path");

        // Create combo box with no report type selected
        JComboBox<String> reportTypeComboBox = new JComboBox<>(new String[]{"TXT", "JSON", "MD"});
        reportTypeComboBox.setSelectedItem(null); // No selection

        // Add input fields to the map (missing report type selection)
        inputFields.put("Save Path", savePathField);
        inputFields.put("Report Type", reportTypeComboBox);

        SwingUtilities.invokeAndWait(() -> {
            SwingUtilities.invokeLater(() -> AutoCloseDialog.closeErrorDialog("Error"));
            // Validate and ensure parameters are not created due to missing report type
            ReportParameters parameters = validator.validateAndCreate(inputFields);
            assertNull(parameters);
        });
    }

    /**
     * Test the ReportParameterValidator with missing save path.
     * This ensures that validation fails when the save path is missing.
     */
    @Test
    public void testValidateAndCreate_MissingSavePath() throws InterruptedException, InvocationTargetException {
        JTextField savePathField = new JTextField();
        JComboBox<String> reportTypeComboBox = new JComboBox<>(new String[]{"TXT", "JSON", "MD"});
        reportTypeComboBox.setSelectedItem("MD");

        // Add input fields but leave the save path empty
        inputFields.put("Report Type", reportTypeComboBox);
        inputFields.put("Save Path", savePathField);

        SwingUtilities.invokeAndWait(() -> {
            SwingUtilities.invokeLater(() -> AutoCloseDialog.closeErrorDialog("Error"));
            // Validate and ensure parameters are not created due to missing save path
            ReportParameters parameters = validator.validateAndCreate(inputFields);
            assertNull(parameters);
        });
    }
}
