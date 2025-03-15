package gr.uoi.cs.pythia.gui.guiScores;


import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CustomTableCellRendererTest {
    private CustomTableCellRenderer renderer;
    private JTable table;

    @Before
    public void setUp() {
        // Initialize the renderer and table before each test
        renderer = new CustomTableCellRenderer();
        table = new JTable(5, 3); // 5 rows, 3 columns
    }

    /**
     * Test for the background color of even rows.
     * It ensures that even rows are rendered with a light blue background color.
     */
    @Test
    public void testEvenRowBackground() {
        Component c = renderer.getTableCellRendererComponent(table, "Test", false, false, 2, 1);
        assertEquals("Even row should have light blue background", new Color(230, 230, 255), c.getBackground());
    }

    /**
     * Test for the background color of odd rows.
     * It verifies that odd rows have a white background.
     */
    @Test
    public void testOddRowBackground() {
        Component c = renderer.getTableCellRendererComponent(table, "Test", false, false, 1, 1);
        assertEquals("Odd row should have white background", Color.WHITE, c.getBackground());
    }

    /**
     * Test for the selected row background.
     * It ensures that the background color of a selected row does not change when it is selected.
     */
    @Test
    public void testSelectedRowBackgroundUnchanged() {
        Component c = renderer.getTableCellRendererComponent(table, "Test", true, false, 2, 1);
        assertNotEquals("Selected row should not change background", new Color(230, 230, 255), c.getBackground());
    }

    /**
     * Test for the first row (index 0) background color.
     * It verifies that the first row, being an even index, should have a light blue background.
     */
    @Test
    public void testFirstRowEven() {
        Component c = renderer.getTableCellRendererComponent(table, "Test", false, false, 0, 1);
        assertEquals("First row (even index) should have light blue background", new Color(230, 230, 255), c.getBackground());
    }

    /**
     * Test for the last row (index 4) background color.
     * It verifies that the last row (which is even-indexed) should also have a light blue background.
     */
    @Test
    public void testLastRowOdd() {
        Component c = renderer.getTableCellRendererComponent(table, "Test", false, false, 4, 1);
        assertEquals("Last row (even index) should have light blue background", new Color(230, 230, 255), c.getBackground());
    }

    /**
     * Test for the background color in different columns of the same row.
     * It ensures that all columns in the same row should have the same background color.
     */
    @Test
    public void testDifferentColumnsSameRow() {
        Component c1 = renderer.getTableCellRendererComponent(table, "Test1", false, false, 2, 0);
        Component c2 = renderer.getTableCellRendererComponent(table, "Test2", false, false, 2, 2);
        assertEquals("Different columns in the same row should have the same background", c1.getBackground(), c2.getBackground());
    }

    /**
     * Test for focus behavior.
     * It ensures that the row background color does not change when the row has focus.
     */
    @Test
    public void testHasFocusDoesNotAffectColor() {
        Component c = renderer.getTableCellRendererComponent(table, "Test", false, true, 3, 1);
        assertEquals("Row background should be white even if it has focus", Color.WHITE, c.getBackground());
    }
}

