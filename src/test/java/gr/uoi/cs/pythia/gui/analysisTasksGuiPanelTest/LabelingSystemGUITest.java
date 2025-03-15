package gr.uoi.cs.pythia.gui.analysisTasksGuiPanelTest;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.gui.analysisTasksGuiPanels.AnalysisTabsPanel;
import gr.uoi.cs.pythia.gui.analysisTasksGuiPanels.LabelingSystemGUI;
import gr.uoi.cs.pythia.labeling.Rule;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class LabelingSystemGUITest {
    private LabelingSystemGUI labelingSystemGUI;

    @Before
    public void setUp() throws Exception {
        // Initialize GUI with an empty AnalysisTabsPanel
        AnalysisTabsPanel tabsPanel = new AnalysisTabsPanel(new ArrayList<>(), null, null, null);
        labelingSystemGUI = new LabelingSystemGUI(tabsPanel, null, null);

        // Set dataset profile using reflection
        try {
            setDatasetProfile(new DatasetProfile());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Set a fake dataset using Spark
        try {
            setDataset(createFakeDataset());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Helper method to set dataset using reflection
    private void setDataset(Dataset<Row> dataset) throws Exception {
        Field field = AppController.class.getDeclaredField("dataset");
        field.setAccessible(true);
        field.set(AppController.getInstance(), dataset);
    }

    // Create a fake dataset for testing
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

    // Helper method to set dataset profile using reflection
    private void setDatasetProfile(DatasetProfile datasetProfile) throws Exception {
        Field field = AppController.class.getDeclaredField("datasetProfile");
        field.setAccessible(true);
        field.set(AppController.getInstance(), datasetProfile);
    }

    /**
     * Test adding a valid rule.
     * Ensures that all inputs are correctly set and the rule is added successfully.
     */
    @Test
    public void testAddRule_ValidInput() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            // Set input values
            JTextField targetField = (JTextField) labelingSystemGUI.getInputFields().get("Target Column");
            JComboBox<?> operatorBox = (JComboBox<?>) labelingSystemGUI.getInputFields().get("Operator");
            JTextField limitField = (JTextField) labelingSystemGUI.getInputFields().get("Limit");
            JTextField labelField = (JTextField) labelingSystemGUI.getInputFields().get("Label");
            JTextField newColumnNameField = (JTextField) labelingSystemGUI.getInputFields().get("New Column Name");

            assertNotNull(targetField);
            assertNotNull(operatorBox);
            assertNotNull(limitField);
            assertNotNull(labelField);
            assertNotNull(newColumnNameField);

            targetField.setText("feature1");
            operatorBox.setSelectedItem(">");
            limitField.setText("18");
            labelField.setText("Adult");
            newColumnNameField.setText("Age Category");

            labelingSystemGUI.addRule();

            // Validate rule creation
            List<Rule> rules = labelingSystemGUI.getParameters().getRules();
            assertEquals(1, rules.size());
            assertEquals("feature1", rules.get(0).getTargetColumnName());
            assertEquals(">", rules.get(0).getSparkOperator());
            assertEquals(18.0, (Double) rules.get(0).getLimit(), 0.001);
            assertEquals("Adult", rules.get(0).getLabel());
            assertEquals("Age Category", labelingSystemGUI.getParameters().getNewColumnName());
        });
    }

    /**
     * Test adding a rule with empty input.
     * Ensures no rule is added if required fields are missing.
     */
    @Test
    public void testAddRule_EmptyInput() {
        ((JTextField) labelingSystemGUI.getInputFields().get("Target Column")).setText("");
        ((JComboBox<?>) labelingSystemGUI.getInputFields().get("Operator")).setSelectedItem(">");
        ((JTextField) labelingSystemGUI.getInputFields().get("Limit")).setText("18");
        ((JTextField) labelingSystemGUI.getInputFields().get("Label")).setText("Adult");
        ((JTextField) labelingSystemGUI.getInputFields().get("New Column Name")).setText("Age Category");

        try {
            SwingUtilities.invokeAndWait(() -> {
                SwingUtilities.invokeLater(() -> AutoCloseDialog.closeErrorDialog("Error"));
                labelingSystemGUI.addRule();
                List<Rule> rules = labelingSystemGUI.getParameters().getRules();
                assertEquals(0, rules.size());
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Test adding a rule with an invalid limit value.
     * Ensures the system prevents non-numeric values for limit.
     */
    @Test
    public void testAddRule_InvalidLimit() throws Exception {
        ((JTextField) labelingSystemGUI.getInputFields().get("Target Column")).setText("Age");
        ((JComboBox<?>) labelingSystemGUI.getInputFields().get("Operator")).setSelectedItem(">");
        ((JTextField) labelingSystemGUI.getInputFields().get("Limit")).setText("not_a_number");
        ((JTextField) labelingSystemGUI.getInputFields().get("Label")).setText("Adult");
        ((JTextField) labelingSystemGUI.getInputFields().get("New Column Name")).setText("Age Category");

        SwingUtilities.invokeAndWait(() -> {
            SwingUtilities.invokeLater(() -> AutoCloseDialog.closeErrorDialog("Error"));
            labelingSystemGUI.addRule();
            List<Rule> rules = labelingSystemGUI.getParameters().getRules();
            assertEquals(0, rules.size());
        });
    }

    /**
     * Test if the rules area is updated correctly after adding a rule.
     */
    @Test
    public void testAddRule_RulesAreaUpdate() throws Exception {
        // Set valid inputs
        ((JTextField) labelingSystemGUI.getInputFields().get("Target Column")).setText("Age");
        ((JComboBox<?>) labelingSystemGUI.getInputFields().get("Operator")).setSelectedItem(">");
        ((JTextField) labelingSystemGUI.getInputFields().get("Limit")).setText("18");
        ((JTextField) labelingSystemGUI.getInputFields().get("Label")).setText("Adult");
        ((JTextField) labelingSystemGUI.getInputFields().get("New Column Name")).setText("Age Category");

        SwingUtilities.invokeAndWait(() -> labelingSystemGUI.addRule());

        // Verify the rulesArea was updated
        String rulesText = labelingSystemGUI.getResultArea().getText();
        assertTrue(rulesText.contains("Age"));
        assertTrue(rulesText.contains(">"));
        assertTrue(rulesText.contains("18"));
        assertTrue(rulesText.contains("Adult"));
        assertTrue(rulesText.contains("Age Category"));
    }
}