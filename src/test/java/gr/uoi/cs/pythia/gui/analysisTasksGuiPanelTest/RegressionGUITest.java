package gr.uoi.cs.pythia.gui.analysisTasksGuiPanelTest;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.gui.analysisTasksGuiPanels.RegressionGUI;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.regression.RegressionType;
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
import java.util.List;

import static org.junit.Assert.assertEquals;

public class RegressionGUITest{

    private final RegressionGUI regressionGUI = new RegressionGUI(null,null,null);

    @Before
    public void setUp() throws Exception{
        // Set up a fake dataset and dataset profile for testing
        setDatasetProfile(new DatasetProfile());
        setDataset(createFakeDataset());
    }

    private void setDataset(Dataset<Row> dataset) throws Exception {
        Field field = AppController.class.getDeclaredField("dataset");
        field.setAccessible(true);
        field.set(AppController.getInstance(), dataset);
    }

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

    private void setDatasetProfile(DatasetProfile datasetProfile) throws Exception {
        Field field = AppController.class.getDeclaredField("datasetProfile");
        field.setAccessible(true);
        field.set(AppController.getInstance(), datasetProfile);
    }

    /**
     * Test the `addRegression` method with various valid input configurations.
     * It checks if the regression information is correctly added to the result area.
     */
    @Test
    public void testAddRegression(){
        // Get input fields
        JTextField dependentVariableField = (JTextField) regressionGUI.getInputFields().get("Dependent Variable");
        JTextField independentVariablesField = (JTextField) regressionGUI.getInputFields().get("Independent Variables (comma-separated)");
        JComboBox<RegressionType> regressionTypeComboBox = (JComboBox<RegressionType>) regressionGUI.getInputFields().get("Regression Type");
        JTextField precisionField = (JTextField) regressionGUI.getInputFields().get("Precision");

        // Set test data and add regression
        dependentVariableField.setText("feature1");
        independentVariablesField.setText("feature1,feature2");
        regressionTypeComboBox.setSelectedItem(RegressionType.LINEAR);
        precisionField.setText("");
        regressionGUI.addRegression();

        // Assertions
        JTextArea regressionsArea = regressionGUI.getResultArea();
        String expectedText = "Added Regressions:\n"+"Dependent Variable: feature1\n"+"Independent Variables: [feature1, feature2]\n"+"Regression Type: LINEAR\n"+"Precision: null\n"+
                "----------------------------\n";
        assertEquals(expectedText, regressionsArea.getText());

        //Test with precision for multiple linear regression
        dependentVariableField.setText("feature1");
        independentVariablesField.setText("feature1,feature2");
        regressionTypeComboBox.setSelectedItem(RegressionType.MULTIPLE_LINEAR);
        precisionField.setText("0.05");

        regressionGUI.addRegression();

        // Assertions
        regressionsArea = regressionGUI.getResultArea();
        expectedText = expectedText+"Dependent Variable: feature1\n"+"Independent Variables: [feature1, feature2]\n"+"Regression Type: MULTIPLE_LINEAR\n"+"Precision: 0.05\n"+
                "----------------------------\n";
        assertEquals(expectedText, regressionsArea.getText());

        //Test for polynomial regression type requiring precision
        dependentVariableField.setText("feature1");
        independentVariablesField.setText("feature1,feature2");
        regressionTypeComboBox.setSelectedItem(RegressionType.POLYNOMIAL);
        precisionField.setText("0.05");

        regressionGUI.addRegression();

        // Assertions
        regressionsArea = regressionGUI.getResultArea();
        expectedText = expectedText+"Dependent Variable: feature1\n"+"Independent Variables: [feature1, feature2]\n"+"Regression Type: POLYNOMIAL\n"+"Precision: 0.05\n"+
                "----------------------------\n";
        assertEquals(expectedText, regressionsArea.getText());
    }

    /**
     * Test the `addRegression` method when the dependent variable is missing.
     * The test ensures that an error dialog is shown and no regression is added.
     */
    @Test
    public void testAddRegression_MissingDependentVariable() throws InterruptedException, InvocationTargetException {
        JTextField independentVariablesField = (JTextField) regressionGUI.getInputFields().get("Independent Variables (comma-separated)");
        JComboBox<RegressionType> regressionTypeComboBox = (JComboBox<RegressionType>) regressionGUI.getInputFields().get("Regression Type");
        JTextField precisionField = (JTextField) regressionGUI.getInputFields().get("Precision");

        // Set test data with missing dependent variable
        independentVariablesField.setText("x1,x2");
        regressionTypeComboBox.setSelectedItem(RegressionType.LINEAR);
        precisionField.setText("");
        SwingUtilities.invokeAndWait(() -> {
            SwingUtilities.invokeLater(()->AutoCloseDialog.closeErrorDialog("Error"));
            regressionGUI.addRegression();
            JTextArea regressionsArea = regressionGUI.getResultArea();
            assertEquals("Added Regressions:\nNo regressions added yet.", regressionsArea.getText()); // No regression should be added
        });
    }

    /**
     * Test the `addRegression` method when independent variables are missing.
     * The test ensures that an error dialog is shown and no regression is added.
     */
    @Test
    public void testAddRegression_MissingIndependentVariables() throws InterruptedException, InvocationTargetException {
        JTextField dependentVariableField = (JTextField) regressionGUI.getInputFields().get("Dependent Variable");
        JComboBox<RegressionType> regressionTypeComboBox = (JComboBox<RegressionType>) regressionGUI.getInputFields().get("Regression Type");
        JTextField precisionField = (JTextField) regressionGUI.getInputFields().get("Precision");

        // Set test data with missing independent variables
        dependentVariableField.setText("y");
        regressionTypeComboBox.setSelectedItem(RegressionType.LINEAR);
        precisionField.setText("");
        SwingUtilities.invokeAndWait(() -> {
            SwingUtilities.invokeLater(()->AutoCloseDialog.closeErrorDialog("Error"));
            regressionGUI.addRegression();
            JTextArea regressionsArea = regressionGUI.getResultArea();
            assertEquals("Added Regressions:\nNo regressions added yet.", regressionsArea.getText()); // No regression should be added
        });
    }

    /**
     * Test the `addRegression` method when precision is missing for polynomial regression.
     * It checks that the method does not add the regression and shows an error.
     */
    @Test
    public void testAddRegression_MissingPrecision_ForPolynomial() throws InterruptedException, InvocationTargetException {
        JTextField dependentVariableField = (JTextField) regressionGUI.getInputFields().get("Dependent Variable");
        JTextField independentVariablesField = (JTextField) regressionGUI.getInputFields().get("Independent Variables (comma-separated)");
        JComboBox<RegressionType> regressionTypeComboBox = (JComboBox<RegressionType>) regressionGUI.getInputFields().get("Regression Type");
        JTextField precisionField = (JTextField) regressionGUI.getInputFields().get("Precision");

        // Set test data with missing precision for polynomial regression
        dependentVariableField.setText("y");
        independentVariablesField.setText("x1,x2");
        regressionTypeComboBox.setSelectedItem(RegressionType.POLYNOMIAL);
        precisionField.setText(""); // Missing precision
        SwingUtilities.invokeAndWait(() -> {
            SwingUtilities.invokeLater(()->AutoCloseDialog.closeErrorDialog("Input Error"));
            regressionGUI.addRegression();
            JTextArea regressionsArea = regressionGUI.getResultArea();
            assertEquals("Added Regressions:\nNo regressions added yet.", regressionsArea.getText()); // No regression should be added
        });
    }

    /**
     * Test the `addRegression` method when the precision value is invalid.
     * It ensures that no regression is added and an error dialog is shown.
     */
    @Test
    public void testAddRegression_InvalidPrecision() throws InterruptedException, InvocationTargetException {
        JTextField dependentVariableField = (JTextField) regressionGUI.getInputFields().get("Dependent Variable");
        JTextField independentVariablesField = (JTextField) regressionGUI.getInputFields().get("Independent Variables (comma-separated)");
        JComboBox<RegressionType> regressionTypeComboBox = (JComboBox<RegressionType>) regressionGUI.getInputFields().get("Regression Type");
        JTextField precisionField = (JTextField) regressionGUI.getInputFields().get("Precision");

        // Set test data with invalid precision
        dependentVariableField.setText("y");
        independentVariablesField.setText("x1,x2");
        regressionTypeComboBox.setSelectedItem(RegressionType.POLYNOMIAL);
        precisionField.setText("abc"); // Invalid precision
        SwingUtilities.invokeAndWait(() -> {
            SwingUtilities.invokeLater(()->AutoCloseDialog.closeErrorDialog("Input Error"));
            regressionGUI.addRegression();
            JTextArea regressionsArea = regressionGUI.getResultArea();
            assertEquals("Added Regressions:\nNo regressions added yet.", regressionsArea.getText()); // No regression should be added
        });
    }
}
