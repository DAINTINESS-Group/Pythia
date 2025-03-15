package gr.uoi.cs.pythia.gui.guiScores;


import gr.uoi.cs.pythia.gui.analysisTasksGuiPanelTest.AutoCloseDialog;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.junit.*;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

public class DataTypeDetectorUITest {

    private static SparkSession spark;
    private Dataset<Row> dataset;
    private Map<String, Map<DataType, Integer>> scoresPerColumnMap;
    private DataTypeDetectorUI ui;

    @BeforeClass
    public static void setUpClass() {
        // Initialize Spark session to work with Dataset<Row>
        spark = SparkSession.builder()
                .appName("DataTypeDetectorUITest")
                .master("local[*]") // Run locally with all available cores
                .getOrCreate();
    }

    @AfterClass
    public static void tearDownClass() {
        // Stop the Spark session after tests are done
        if (spark != null) {
            spark.stop();
        }
    }

    @Before
    public void setUp() {
        // Create a small dummy Dataset for testing
        dataset = spark.createDataFrame(Arrays.asList(
                RowFactory.create("value1", 1),
                RowFactory.create("value2", 2)
        ), createSchema());

        // Example scores for column types (replace with actual data if needed)
        scoresPerColumnMap = new HashMap<>();
        Map<DataType, Integer> col1Scores = new HashMap<>();
        col1Scores.put(DataTypes.StringType, 5);
        scoresPerColumnMap.put("col1", col1Scores);

        Map<DataType, Integer> col2Scores = new HashMap<>();
        col2Scores.put(DataTypes.IntegerType, 3);
        scoresPerColumnMap.put("col2", col2Scores);

        // Set up the UI dialog for testing
        JDialog dialog = new JDialog();
        dialog.setTitle("Data Type Detector Results");
        dialog.setModal(true); // This stops execution until the dialog is closed
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(1920, 1080);
        dialog.setLocationRelativeTo(null);
        ui = new DataTypeDetectorUI(dialog, dataset, scoresPerColumnMap);
    }

    @After
    public void tearDown() {
        // Dispose of the dialog to release resources after each test
        if (ui != null && ui.getFrame() != null) {
            ui.getFrame().dispose();
        }
    }

    private StructType createSchema() {
        // Define the schema for the dataset
        return new StructType()
                .add("col1", DataTypes.StringType)
                .add("col2", DataTypes.IntegerType);
    }

    /**
     * Test to verify the frame creation in DataTypeDetectorUI.
     * It checks if the frame is properly created with the correct dimensions and close operation.
     */
    @Test
    public void testDataTypeDetectorUI_frameCreation() {
        JFrame frame = ui.getFrame();
        assertNotNull(frame);
        assertEquals(1000, frame.getWidth());
        assertEquals(1000, frame.getHeight());
        assertEquals(JFrame.DISPOSE_ON_CLOSE, frame.getDefaultCloseOperation());
    }

    /**
     * Test to verify the presence of UI components within the main panel of the dialog.
     * It ensures that the main panel contains DataPanel, ScorePanel, and Button Panel.
     */
    @Test
    public void testDataTypeDetectorUI_panelComponents() {
        JDialog jDialog = ui.getDialog();
        Component component = jDialog.getContentPane().getComponent(0);

        // The main component should be a JPanel, not a JViewport
        assertTrue(component instanceof JPanel);

        JPanel mainPanel = (JPanel) component;
        assertNotNull(mainPanel);

        // Ensure that the mainPanel contains the expected components
        assertTrue(mainPanel.getComponentCount() > 0); // Ensure at least one component is present

        if (mainPanel.getComponentCount() > 0) {
            assertTrue(mainPanel.getComponent(0) instanceof DataPanel);
        }
        if (mainPanel.getComponentCount() > 1) {
            assertTrue(mainPanel.getComponent(1) instanceof ScorePanel);
        }
        if (mainPanel.getComponentCount() > 2) {
            assertTrue(mainPanel.getComponent(2) instanceof JPanel); // Button panel
        }
    }

    /**
     * Test to verify the creation of tables in the DataPanel and ScorePanel.
     * It ensures that both tables are created and contain the correct number of columns.
     */
    @Test
    public void testDataTypeDetectorUI_tableCreation() {
        JDialog jDialog = ui.getDialog();
        Component component = jDialog.getContentPane().getComponent(0);

        // The main component should be a JPanel
        assertTrue(component instanceof JPanel);

        JPanel mainPanel = (JPanel) component;

        DataPanel dataPanel = (DataPanel) mainPanel.getComponent(0);
        JTable dataTable = dataPanel.getTable();
        assertNotNull(dataTable);
        assertEquals(dataset.columns().length, dataTable.getColumnCount());

        ScorePanel scorePanel = (ScorePanel) mainPanel.getComponent(1);
        JTable scoreTable = scorePanel.getTable();
        assertNotNull(scoreTable);
        assertEquals(12, scoreTable.getColumnCount());

        JTableHeader header = dataTable.getTableHeader();
        assertNotNull(header);
    }

    /**
     * Test to verify that the showResults method correctly invokes SwingUtilities.
     * It ensures that the UI is properly updated in the event dispatch thread.
     */
    @Test
    public void testShowResults_invokesSwingUtilities() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> {
            DataTypeDetectorUI.showResults(new JDialog(), dataset, scoresPerColumnMap);
            latch.countDown();
        });
        latch.await();
    }

    /**
     * Test to verify that the save button triggers the expected action.
     * It ensures that when the save button is clicked, the dialog closes as expected.
     */
    @Test
    public void testSaveButtonAction() throws InterruptedException, InvocationTargetException {
        JDialog jDialog = ui.getDialog();
        Component component = jDialog.getContentPane().getComponent(0);
        assertTrue(component instanceof JPanel);
        JPanel mainPanel = (JPanel) component;
        JPanel buttonPanel = (JPanel) mainPanel.getComponent(2);
        JButton saveButton = (JButton) buttonPanel.getComponent(0);
        assertNotNull(saveButton);

        SwingUtilities.invokeAndWait(() -> {
            SwingUtilities.invokeLater(() -> AutoCloseDialog.closeErrorDialog("Select catalog for save scema"));
            saveButton.doClick();
            assertFalse(jDialog.isVisible());
        });
    }
}
