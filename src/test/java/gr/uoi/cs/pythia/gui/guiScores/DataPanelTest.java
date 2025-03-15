package gr.uoi.cs.pythia.gui.guiScores;


import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
public class DataPanelTest {
    private DataPanel dataPanel;
    private final String[] columnNames = {"Column1", "Column2", "Column3"};
    private SparkSession spark;

    @Before
    public void setUp() {
        // Initialize SparkSession to work with Dataset<Row>
        spark = SparkSession.builder()
                .appName("DataPanelTest")
                .master("local[*]") // Run locally with all available cores
                .getOrCreate();

        // Create sample data for the Dataset
        List<Row> data = Arrays.asList(
                RowFactory.create("Data1", "Data2", "Data3"),
                RowFactory.create("Data4", "Data5", "Data6")
        );

        // Create Dataset<Row> using the data and schema
        Dataset<Row> dataset = spark.createDataFrame(data, createSchema());

        // Create DataPanel with the dataset and column names
        dataPanel = new DataPanel(dataset, columnNames);
    }

    private StructType createSchema() {
        // Define the schema for the dataset
        return new StructType()
                .add("Column1", "string")
                .add("Column2", "string")
                .add("Column3", "string");
    }

    /**
     * Test to ensure that the table in the DataPanel is created successfully.
     * It checks that the table component is not null.
     */
    @Test
    public void testTableIsNotNull() {
        JTable table = dataPanel.getTable();
        assertNotNull("Table should be created", table);
    }

    /**
     * Test to verify that the table has the correct number of rows.
     * It ensures that the dataset is correctly populated into the table.
     */
    @Test
    public void testTableHasCorrectNumberOfRows() {
        DefaultTableModel model = (DefaultTableModel) dataPanel.getTable().getModel();
        assertEquals("Table should have 2 rows", 2, model.getRowCount());
    }

    /**
     * Test to verify that the table has the correct number of columns.
     * It checks if the number of columns matches the dataset schema.
     */
    @Test
    public void testTableHasCorrectNumberOfColumns() {
        DefaultTableModel model = (DefaultTableModel) dataPanel.getTable().getModel();
        assertEquals("Table should have 3 columns", 3, model.getColumnCount());
    }

    /**
     * Test to verify that the data in the table matches the expected values.
     * It ensures that each cell in the table contains the correct value from the dataset.
     */
    @Test
    public void testTableDataIsCorrect() {
        DefaultTableModel model = (DefaultTableModel) dataPanel.getTable().getModel();

        // Verify the first row
        assertEquals("First row, first column should have value 'Data1'", "Data1", model.getValueAt(0, 0));
        assertEquals("First row, second column should have value 'Data2'", "Data2", model.getValueAt(0, 1));
        assertEquals("First row, third column should have value 'Data3'", "Data3", model.getValueAt(0, 2));

        // Verify the second row
        assertEquals("Second row, first column should have value 'Data4'", "Data4", model.getValueAt(1, 0));
        assertEquals("Second row, second column should have value 'Data5'", "Data5", model.getValueAt(1, 1));
        assertEquals("Second row, third column should have value 'Data6'", "Data6", model.getValueAt(1, 2));
    }

    /**
     * Clean up after each test.
     * Stops the Spark session to release resources.
     */
    @After
    public void tearDown() {
        if (spark != null) {
            spark.stop(); // Stop the SparkSession
        }
    }
}

