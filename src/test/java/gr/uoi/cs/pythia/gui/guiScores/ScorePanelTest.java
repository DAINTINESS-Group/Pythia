package gr.uoi.cs.pythia.gui.guiScores;


import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
public class ScorePanelTest {
    private ScorePanel scorePanel;

    @Before
    public void setUp() {
        // Initialize test data for ScorePanel creation
        Map<String, Map<DataType, Integer>> scoresPerColumnMap = new HashMap<>();
        String[] columnNames = new String[]{"Column1", "Column2"};

        // Add data for "Column1"
        Map<DataType, Integer> column1Scores = new HashMap<>();
        column1Scores.put(DataTypes.StringType, 10);
        column1Scores.put(DataTypes.IntegerType, 5);
        scoresPerColumnMap.put("Column1", column1Scores);

        // Add data for "Column2"
        Map<DataType, Integer> column2Scores = new HashMap<>();
        column2Scores.put(DataTypes.DoubleType, 8);
        column2Scores.put(DataTypes.BooleanType, 3);
        scoresPerColumnMap.put("Column2", column2Scores);

        // Create the ScorePanel with the test data
        scorePanel = new ScorePanel(scoresPerColumnMap, columnNames);
    }

    /**
     * Test to verify that the table model in ScorePanel is created correctly.
     * It checks if the columns and data for each column are populated as expected.
     */
    @Test
    public void testCreateScoreModel() {
        // Get the table model from the ScorePanel
        TableModel model = scorePanel.getTable().getModel();

        // Check column names
        assertEquals("Column Name", model.getColumnName(0)); // First column is "Column Name"
        assertEquals("StringType", model.getColumnName(1));  // Second column is "StringType"
        assertEquals("BooleanType", model.getColumnName(2)); // Third column is "BooleanType"
        assertEquals("DateType", model.getColumnName(3));    // Fourth column is "DateType"
        assertEquals("TimestampType", model.getColumnName(4)); // Fifth column is "TimestampType"
        assertEquals("ShortType", model.getColumnName(5));    // Sixth column is "ShortType"
        assertEquals("IntegerType", model.getColumnName(6));  // Seventh column is "IntegerType"
        assertEquals("LongType", model.getColumnName(7));     // Eighth column is "LongType"
        assertEquals("FloatType", model.getColumnName(8));    // Ninth column is "FloatType"
        assertEquals("DoubleType", model.getColumnName(9));   // Tenth column is "DoubleType"
        assertEquals("DecimalType(10,0)", model.getColumnName(10)); // Eleventh column is "DecimalType"

        // Check row counts
        assertEquals(2, model.getRowCount()); // There are 2 rows (one for each column)

        // Check the data for "Column1"
        assertEquals("Column1", model.getValueAt(0, 0)); // Column name
        assertEquals(10, model.getValueAt(0, 1));        // StringType score for "Column1"
        assertEquals(0, model.getValueAt(0, 2));         // BooleanType score for "Column1" (not present, so 0)
        assertEquals(0, model.getValueAt(0, 3));         // DateType score for "Column1" (not present, so 0)
        assertEquals(0, model.getValueAt(0, 4));         // TimestampType score for "Column1" (not present, so 0)
        assertEquals(0, model.getValueAt(0, 5));         // ShortType score for "Column1" (not present, so 0)
        assertEquals(5, model.getValueAt(0, 6));         // IntegerType score for "Column1"
        assertEquals(0, model.getValueAt(0, 7));         // LongType score for "Column1" (not present, so 0)
        assertEquals(0, model.getValueAt(0, 8));         // FloatType score for "Column1" (not present, so 0)
        assertEquals(0, model.getValueAt(0, 9));         // DoubleType score for "Column1" (not present, so 0)
        assertEquals(0, model.getValueAt(0, 10));        // DecimalType score for "Column1" (not present, so 0)

        // Check the data for "Column2"
        assertEquals("Column2", model.getValueAt(1, 0)); // Column name
        assertEquals(0, model.getValueAt(1, 1));         // StringType score for "Column2" (not present, so 0)
        assertEquals(3, model.getValueAt(1, 2));         // BooleanType score for "Column2"
        assertEquals(0, model.getValueAt(1, 3));         // DateType score for "Column2" (not present, so 0)
        assertEquals(0, model.getValueAt(1, 4));         // TimestampType score for "Column2" (not present, so 0)
        assertEquals(0, model.getValueAt(1, 5));         // ShortType score for "Column2" (not present, so 0)
        assertEquals(0, model.getValueAt(1, 6));         // IntegerType score for "Column2" (not present, so 0)
        assertEquals(0, model.getValueAt(1, 7));         // LongType score for "Column2" (not present, so 0)
        assertEquals(0, model.getValueAt(1, 8));         // FloatType score for "Column2" (not present, so 0)
        assertEquals(8, model.getValueAt(1, 9));         // DoubleType score for "Column2"
        assertEquals(0, model.getValueAt(1, 10));        // DecimalType score for "Column2" (not present, so 0)
    }

    /**
     * Test to verify the structure of the table in the ScorePanel.
     * It ensures that the table has the correct number of rows and columns.
     */
    @Test
    public void testTableStructure() {
        JTable table = scorePanel.getTable();

        // Check if the table has the correct number of columns
        assertEquals(12, table.getColumnCount()); // "Column Name" + 10 data types

        // Check if the table has the correct number of rows
        assertEquals(2, table.getRowCount()); // 2 rows (one for each column)
    }

    /**
     * Test to verify the properties of the ScorePanel.
     * It ensures the layout, border, and background color are as expected.
     */
    @Test
    public void testPanelProperties() {
        // Check the layout of the ScorePanel
        assertEquals(BorderLayout.class, scorePanel.getLayout().getClass()); // The layout should be BorderLayout

        // Check if the ScorePanel has a border
        assertNotNull(scorePanel.getBorder());

        // Check if the background color is correct
        assertEquals(new Color(240, 255, 240), scorePanel.getBackground()); // Background color should be light green
    }

    /**
     * Test to verify that the ScorePanel behaves correctly with empty data.
     * It ensures the table is empty when no data is provided.
     */
    @Test
    public void testEmptyData() {
        // Create a ScorePanel with empty data
        Map<String, Map<DataType, Integer>> emptyScores = new HashMap<>();
        String[] emptyColumns = new String[]{};
        ScorePanel emptyPanel = new ScorePanel(emptyScores, emptyColumns);

        // Check if the table is empty
        assertEquals(0, emptyPanel.getTable().getRowCount()); // No rows
        assertEquals(12, emptyPanel.getTable().getColumnCount()); // 12 columns (1 column for name + 10 data types)
    }

    /**
     * Test to verify that the ScorePanel behaves correctly with null data.
     * It ensures the table is empty when null data is provided.
     */
    @Test
    public void testNullData() {
        // Create a ScorePanel with null data
        ScorePanel nullPanel = new ScorePanel(null, null);

        // Check if the table is empty
        assertEquals(0, nullPanel.getTable().getRowCount()); // No rows
        assertEquals(12, nullPanel.getTable().getColumnCount()); // 12 columns (1 column for name + 10 data types)
    }
}
