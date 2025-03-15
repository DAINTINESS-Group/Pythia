package gr.uoi.cs.pythia.gui.resultsGui;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.CorrelationsProfile;
import gr.uoi.cs.pythia.model.DatasetProfile;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CorrelationsPanelTest {

    private CorrelationsPanel correlationsPanel;

    /**
     * Setup method before each test.
     * Initializes the CorrelationsPanel and sets up a fake dataset and profile for testing.
     */
    @Before
    public void setUp() throws Exception {
        correlationsPanel = new CorrelationsPanel();

        // Initialize with a fake dataset and dataset profile
        setDataset(null);
        setDatasetProfile(null);
        setDataset(createFakeDataset());
        setDatasetProfile(createFakeDatasetProfile());
    }

    /**
     * Tear down method after each test.
     * Resets the dataset and dataset profile to null after each test.
     */
    @After
    public void tearDown() throws Exception {
        setDataset(null); // Reset the dataset
        setDatasetProfile(null); // Reset the dataset profile
    }

    /**
     * Helper method to set the dataset in AppController using reflection.
     * This injects the dataset into AppController for testing purposes.
     *
     * @param dataset the dataset to be set in AppController
     * @throws Exception if there is an issue accessing the field
     */
    private void setDataset(Dataset<Row> dataset) throws Exception {
        Field field = AppController.class.getDeclaredField("dataset");
        field.setAccessible(true);
        field.set(AppController.getInstance(), dataset);
    }

    /**
     * Helper method to set the dataset profile in AppController using reflection.
     * This injects the dataset profile into AppController for testing purposes.
     *
     * @param datasetProfile the dataset profile to be set in AppController
     * @throws Exception if there is an issue accessing the field
     */
    private void setDatasetProfile(DatasetProfile datasetProfile) throws Exception {
        Field field = AppController.class.getDeclaredField("datasetProfile");
        field.setAccessible(true);
        field.set(AppController.getInstance(), datasetProfile);
    }

    /**
     * Creates a fake dataset for testing purposes.
     * This dataset contains three rows with three features each.
     *
     * @return a fake dataset
     */
    private Dataset<Row> createFakeDataset() {
        SparkSession spark = SparkSession.builder().master("local").appName("FakeDataset").getOrCreate();
        List<Row> data = Arrays.asList(
                RowFactory.create(1.0, 2.0, 3.0),
                RowFactory.create(4.0, 5.0, 6.0),
                RowFactory.create(7.0, 8.0, 9.0)
        );
        StructType schema = new StructType(new StructField[] {
                new StructField("feature1", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("feature2", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("feature3", DataTypes.DoubleType, false, Metadata.empty())
        });
        return spark.createDataFrame(data, schema);
    }

    /**
     * Creates a fake DatasetProfile for testing purposes.
     * The profile contains columns, some with correlation profiles.
     *
     * @return a fake dataset profile
     */
    private DatasetProfile createFakeDatasetProfile() {
        List<Column> columns = new ArrayList<>();

        // Column 1 with CorrelationsProfile
        CorrelationsProfile correlations1 = new CorrelationsProfile();
        Map<String, Double> correlationsMap1 = new LinkedHashMap<>();
        correlationsMap1.put("feature2", 0.8);
        correlationsMap1.put("feature3", 0.5);
        correlations1.setCorrelations(correlationsMap1);

        Column column1 = new Column(0, "feature1", DataTypes.DoubleType.toString());
        column1.setCorrelationsProfile(correlations1);

        // Column 2 without CorrelationsProfile
        Column column2 = new Column(1, "feature2", DataTypes.DoubleType.toString());
        column2.setCorrelationsProfile(null);

        columns.add(column1);
        columns.add(column2);

        DatasetProfile datasetProfile = new DatasetProfile();
        datasetProfile.setColumns(columns);

        return datasetProfile;
    }

    /**
     * Test to verify that the correlation panels are displayed correctly.
     * It checks that only one correlation panel is shown for columns with correlations.
     */
    @Test
    public void testCorrelationPanelsExist() {
        correlationsPanel.createPanelContent();

        JPanel mainPanel = (JPanel) correlationsPanel.getComponent(0);
        JScrollPane scrollPane = (JScrollPane) mainPanel.getComponent(0);
        JPanel correlationsPanelContainer = (JPanel) scrollPane.getViewport().getView();

        // Should only display one panel for the column with correlations
        assertEquals(1, correlationsPanelContainer.getComponentCount());

        JPanel correlationPanel = (JPanel) correlationsPanelContainer.getComponent(0);
        TitledBorder border = (TitledBorder) correlationPanel.getBorder();
        assertEquals("Correlations for feature1", border.getTitle());
    }

    /**
     * Test to verify that the correct message is displayed when there are no columns or correlations.
     * It ensures the panel shows an appropriate message when there is no data to display.
     */
    @Test
    public void testNoColumnsOrCorrelationsMessage() throws Exception {
        // Create an empty DatasetProfile with no columns or correlations
        DatasetProfile emptyProfile = new DatasetProfile();
        emptyProfile.setColumns(new ArrayList<>());
        setDatasetProfile(emptyProfile);

        correlationsPanel = new CorrelationsPanel();
        correlationsPanel.createPanelContent();

        Component mainPanel = correlationsPanel.getComponent(0);
        JLabel label = (JLabel) mainPanel;
        assertEquals("No columns or correlations profile found.", label.getText());
    }

    /**
     * Test to verify that the correlation table data is displayed correctly.
     * This test ensures that the table for correlations shows the correct values.
     */
    @Test
    public void testCorrelationTableData() {
        correlationsPanel.createPanelContent();

        JPanel mainPanel = (JPanel) correlationsPanel.getComponent(0);
        JScrollPane scrollPane = (JScrollPane) mainPanel.getComponent(0);
        JPanel correlationsPanelContainer = (JPanel) scrollPane.getViewport().getView();

        JPanel correlationPanel = (JPanel) correlationsPanelContainer.getComponent(0);
        JScrollPane tableScrollPane = (JScrollPane) correlationPanel.getComponent(0);
        JTable table = (JTable) tableScrollPane.getViewport().getView();

        DefaultTableModel model = (DefaultTableModel) table.getModel();

        // Check that the table has 2 rows (for the 2 columns)
        assertEquals(2, model.getRowCount());

        // Check the correlation values for each feature
        assertEquals("feature2", model.getValueAt(0, 0));
        assertEquals(0.8, model.getValueAt(0, 1));
        assertEquals("feature3", model.getValueAt(1, 0));
        assertEquals(0.5, model.getValueAt(1, 1));
    }
}
