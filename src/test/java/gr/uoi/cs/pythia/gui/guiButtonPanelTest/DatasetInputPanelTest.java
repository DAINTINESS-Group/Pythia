package gr.uoi.cs.pythia.gui.guiButtonPanelTest;


import gr.uoi.cs.pythia.gui.guiButtonPanels.DatasetInputPanel;
import org.apache.spark.sql.types.StructType;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static org.junit.Assert.*;

public class DatasetInputPanelTest {

    private DatasetInputPanel panel;

    @Before
    public void setUp() {
        // Initialize DatasetInputPanel before each test
        panel = new DatasetInputPanel();
    }

    /**
     * Test for the creation of the DatasetInputPanel.
     * It checks if the panel is correctly created and if the layout and background color are set as expected.
     */
    @Test
    public void testPanelCreation() {
        assertNotNull(panel);
        assertEquals(BorderLayout.class, panel.getLayout().getClass());
        assertEquals(new Color(240, 240, 240), panel.getBackground());
    }

    /**
     * Test for the alias text field.
     * It checks if the alias field is correctly initialized and text can be set.
     */
    @Test
    public void testAliasField() {
        JTextField aliasField = (JTextField) findComponent(panel, JTextField.class, 0);
        assertNotNull(aliasField);
        aliasField.setText("TestAlias");
        assertEquals("TestAlias", aliasField.getText());
    }

    /**
     * Test for the path text field.
     * It checks if the path field is correctly initialized and text can be set.
     */
    @Test
    public void testPathField()  {
        JTextField pathField = findPathField(panel);
        assertNotNull("Path field should exist", pathField);

        String testPath = "TestPath";
        pathField.setText(testPath);
        assertEquals(testPath, pathField.getText()); // Correct usage

        String anotherPath = "/path/to/file";
        pathField.setText(anotherPath);
        assertEquals(anotherPath, pathField.getText()); // Correct usage
    }

    /**
     * Test for the schema text area.
     * It checks if the schema field is correctly initialized and is not editable.
     */
    @Test
    public void testSchemaField() {
        JTextArea schemaField = (JTextArea) findComponent(panel, JTextArea.class, 0);
        assertNotNull(schemaField);
        assertFalse(schemaField.isEditable());
    }

    /**
     * Test for the result area.
     * It checks if the result area is correctly initialized and is not editable.
     */
    @Test
    public void testResultArea() {
        JTextArea resultArea = panel.getResultArea();
        assertNotNull(resultArea);
        assertFalse(resultArea.isEditable());
    }

    /**
     * Test for the "Register Dataset" button.
     * It checks if the button is correctly created and has the expected text.
     */
    @Test
    public void testRegisterButton() {
        JButton registerButton = findButton(panel, "Register Dataset");
        assertNotNull(registerButton);
        assertEquals("Register Dataset", registerButton.getText());
    }

    /**
     * Test for the "Browse" button.
     * It checks if the button is correctly created and has the expected text.
     */
    @Test
    public void testBrowseButton() {
        JButton browseButton = findButton(panel, "Browse");
        assertNotNull(browseButton);
        assertEquals("Browse", browseButton.getText());
    }

    /**
     * Test for updating the result area when values are set in the alias and path fields.
     * It checks if the result area correctly reflects the alias and path information.
     */
    @Test
    public void testUpdateResultsArea() {
        assertNotNull(panel); // Ensure the panel is not null

        // Use getters to access components
        JTextField aliasField = panel.getAliasField();
        JTextField pathField = panel.getPathField();

        aliasField.setText("AliasTest");
        pathField.setText("PathTest");

        panel.currentStructType = new StructType();
        assertNotNull("StructType should be initialized", panel.currentStructType);

        panel.updateResultsArea();

        JTextArea resultArea = panel.getResultArea();
        assertNotNull("Result area should exist", resultArea);

        String resultText = resultArea.getText().trim();

        String expectedAlias = "Dataset Alias: AliasTest";
        String expectedPath = "Dataset Path: PathTest";

        assertTrue(resultText.contains(expectedAlias));
        assertTrue(resultText.contains(expectedPath));
    }

    /**
     * Test for the "Register Dataset" button action.
     * It checks if the button action correctly triggers a process and updates the result area.
     */
    @Test
    public void testRegisterButtonAction() {
        assertNotNull(panel);

        // Use getters to access components
        JButton registerButton = panel.getRegisterButton();
        JTextField aliasField = panel.getAliasField();
        JTextField pathField = panel.getPathField();

        // Set values in the input fields
        aliasField.setText("AliasTest");
        pathField.setText("PathTest");

        // Create a flag to verify if the process was executed
        final boolean[] processExecuted = {false}; // Use array to modify inside listener

        // Create a mock ActionListener
        ActionListener mockListener = e -> processExecuted[0] = true;

        registerButton.addActionListener(mockListener);
        for (ActionListener al : registerButton.getActionListeners()) {
            al.actionPerformed(new ActionEvent(registerButton, ActionEvent.ACTION_PERFORMED, ""));
            break;
        }

        // Check if the process was executed
        assertTrue("The process should be executed after button click", processExecuted[0]);

        // Check if the result area was updated correctly
        JTextArea resultArea = panel.getResultArea();
        assertNotNull("Result area should exist", resultArea);

        // Get the text from the result area
        String resultText = resultArea.getText().trim();

        // Expected result text
        String expectedAlias = "Dataset Alias: AliasTest";
        String expectedPath = "Dataset Path: PathTest";

        // Check if the result area text contains the expected values
        assertTrue(resultText.contains(expectedAlias));
        assertTrue(resultText.contains(expectedPath));
    }

    /**
     * Helper method to find a button by its text in the given container.
     */
    private JButton findButton(Container parent, String text) {
        for (Component c : parent.getComponents()) {
            if (c instanceof JButton && ((JButton) c).getText().equals(text)) {
                return (JButton) c;
            } else if (c instanceof Container) {
                JButton button = findButton((Container) c, text);
                if (button != null) return button;
            }
        }
        return null;
    }

    /**
     * Helper method to find a component of a specific class in the given container by its index.
     */
    private Component findComponent(Container container, Class<?> clazz, int index) {
        int count = 0;
        for (Component c : container.getComponents()) {
            if (clazz.isInstance(c)) {
                if (count == index) {
                    return c;
                }
                count++;
            }
            if (c instanceof Container) {
                Component child = findComponent((Container) c, clazz, index);
                if (child != null) {
                    return child;
                }
            }
        }
        return null;
    }

    /**
     * Helper method to find the path field in the DatasetInputPanel.
     */
    private JTextField findPathField(DatasetInputPanel panel) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JPanel) {
                for (Component subComp : ((JPanel) comp).getComponents()) {
                    if (subComp instanceof JTextField && ((JTextField) subComp).getColumns() == 20) {
                        return (JTextField) subComp;
                    }
                }
            }
        }
        return null;
    }
}
