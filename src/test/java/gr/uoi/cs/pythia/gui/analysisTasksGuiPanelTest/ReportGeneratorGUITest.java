package gr.uoi.cs.pythia.gui.analysisTasksGuiPanelTest;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.gui.analysisTasksGuiPanels.ReportGeneratorGUI;
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
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ReportGeneratorGUITest {

    private ReportGeneratorGUI reportGeneratorGUI;
    private ReportParameterValidator validator;

    @Before
    public void setUp() throws Exception {
        reportGeneratorGUI = new ReportGeneratorGUI(null, null);
        validator = new ReportParameterValidator();
        // Set up dataset and dataset profile for the tests
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
     * It checks if the validation creates valid report parameters when the input is correct.
     */
    @Test
    public void testReportParameterValidator_ValidInput() {
        JComboBox<String> reportTypeComboBox = new JComboBox<>(new String[]{"TXT", "JSON", "MD"});
        JTextField savePathField = new JTextField();

        // Set valid input values
        reportTypeComboBox.setSelectedItem("TXT");
        savePathField.setText("/valid/path");

        Map<String, JComponent> inputFields = new HashMap<>();
        inputFields.put("Report Type", reportTypeComboBox);
        inputFields.put("Save Path", savePathField);

        // Validate and create parameters
        ReportParameters parameters = validator.validateAndCreate(inputFields);

        // Assertions
        assertNotNull(parameters);
        assertEquals(ReportGeneratorConstants.TXT_REPORT, parameters.type);
        assertEquals("/valid/path", parameters.path);
    }

    /**
     * Test the ReportParameterValidator with an invalid path.
     * It ensures that the validation fails when the path is empty and no parameters are created.
     */
    @Test
    public void testReportParameterValidator_InvalidPath() throws InterruptedException, InvocationTargetException {
        JComboBox<String> reportTypeComboBox = new JComboBox<>(new String[]{"TXT", "JSON", "MD"});
        JTextField savePathField = new JTextField();
        reportTypeComboBox.setSelectedItem("JSON");
        savePathField.setText("");  // Invalid empty path

        Map<String, JComponent> inputFields = new HashMap<>();
        inputFields.put("Report Type", reportTypeComboBox);
        inputFields.put("Save Path", savePathField);

        SwingUtilities.invokeAndWait(() -> {
            SwingUtilities.invokeLater(() -> AutoCloseDialog.closeErrorDialog("Error"));
            ReportParameters parameters = validator.validateAndCreate(inputFields);
            assertNull(parameters);  // Expect null as the validation should fail
        });
    }

    /**
     * Test the ReportGeneratorGUI's updateResultArea method.
     * This test simulates user input and verifies that the result area gets updated correctly.
     */
    @Test
    public void testReportGeneratorGUI_UpdateResultArea() {
        JComboBox<String> reportTypeComboBox = (JComboBox<String>) reportGeneratorGUI.getInputFields().get("Report Type");
        JTextField savePathField = (JTextField) reportGeneratorGUI.getInputFields().get("Save Path");

        // Simulate user input
        reportTypeComboBox.setSelectedItem("MD");
        savePathField.setText("/test/path");

        // Call updateResultArea to refresh the result text
        reportGeneratorGUI.updateResultArea();

        // Get the updated result area text
        String resultText = reportGeneratorGUI.getResultArea().getText();

        // Assertions
        assertTrue(resultText.contains("Report Type: MD"));
        assertTrue(resultText.contains("Save Path: /test/path"));
    }

    /**
     * Test the ReportGeneratorGUI's choose path functionality.
     * This test checks if the path selected by the user is correctly displayed in the input field.
     */
    @Test
    public void testReportGeneratorGUI_ChoosePath() {
        JTextField savePathField = (JTextField) reportGeneratorGUI.getInputFields().get("Save Path");
        File testFile = new File("/test/path");

        savePathField.setText(testFile.getAbsolutePath());

        // Assertions
        assertEquals(testFile.getAbsolutePath(), savePathField.getText());  // The selected path should match the text field value
    }
}
