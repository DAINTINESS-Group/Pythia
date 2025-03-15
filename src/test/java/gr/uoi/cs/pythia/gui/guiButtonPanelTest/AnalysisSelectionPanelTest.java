package gr.uoi.cs.pythia.gui.guiButtonPanelTest;

import gr.uoi.cs.pythia.gui.analysisTasksGuiPanelTest.AutoCloseDialog;
import gr.uoi.cs.pythia.gui.guiButtonPanels.AnalysisSelectionPanel;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.junit.Assert.*;

public class AnalysisSelectionPanelTest {

    private AnalysisSelectionPanel panel;

    @Before
    public void setUp() {
        // Initialize the panel before each test
        panel = new AnalysisSelectionPanel(null, null);
    }

    /**
     * Test for warning when no path is selected but a checkbox is checked.
     * It ensures that when a checkbox is selected without setting a path,
     * a warning dialog is shown asking for the path.
     */
    @Test
    public void testNoPathWarning() throws InterruptedException, InvocationTargetException {
        // Simulate selecting at least one checkbox but no path set
        JCheckBox[] checkBoxes = panel.getCheckBoxes();
        checkBoxes[0].setSelected(true); // Select the first checkbox

        // Ensure the path is null before clicking OK
        assertNull(panel.getPath());  // Check if path is null
        JButton okButton = findOkButton(panel);
        assertNotNull(okButton);

        SwingUtilities.invokeAndWait(() -> {
            SwingUtilities.invokeLater(() -> AutoCloseDialog.closeErrorDialog("Warning"));
            okButton.doClick();
            // Check if the warning dialog is shown and closed correctly
            boolean value = isWarningDialogShown("Give path");
            assertTrue("Expected 'Give path' warning dialog to be shown", value);
        });
    }

    /**
     * Test for valid checkbox selection and path set.
     * It ensures that when a valid path is set, the path is correctly assigned
     * and the checkbox selection is retained after clicking OK.
     */
    @Test
    public void testValidSelectionAndPath() {
        JCheckBox[] checkBoxes = panel.getCheckBoxes();
        checkBoxes[0].setSelected(true); // Select the first checkbox

        // Create a temporary directory for testing
        String testPath = Objects.requireNonNull(createTempDirectory()).getAbsolutePath();

        // Directly set the path using the setter method
        panel.setPath(testPath);  // Set the path

        JButton okButton = findOkButton(panel);
        assertNotNull(okButton);
        okButton.doClick();  // Simulate clicking the OK button

        // Assertions to ensure path and checkbox are set correctly
        assertNotNull(panel.getPath());
        assertEquals(testPath, panel.getPath());  // Check if the path is correctly set
        assertTrue(checkBoxes[0].isSelected());  // Ensure checkbox is selected

        // Cleanup: delete the temporary directory after test
        deleteDirectory(new File(testPath));
    }

    /**
     * Helper method to create a temporary directory for testing purposes.
     * The directory is created and returned as a File object.
     */
    private File createTempDirectory(){
        try {
            Path tempDir = Files.createTempDirectory("testdir");
            return tempDir.toFile();
        } catch (Exception e) {
            fail("Wrong");
            return null;
        }
    }

    /**
     * Helper method to recursively delete a directory and its contents.
     * Used for cleaning up temporary directories after tests.
     */
    private void deleteDirectory(File dir) {
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }

            if (!dir.delete()) {
                System.err.println("Wrong delete: " + dir.getAbsolutePath());
            }
        }
    }

    /**
     * Helper method to find the "OK" button in the panel.
     * It searches recursively through all components in the container.
     */
    private JButton findOkButton(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton && ((JButton) comp).getText().equals("OK")) {
                return (JButton) comp;
            } else if (comp instanceof Container) {
                JButton button = findOkButton((Container) comp);
                if (button != null) {
                    return button;
                }
            }
        }
        return null;  // Return null if no OK button is found
    }

    /**
     * Helper method to check if a warning dialog with the expected message is shown.
     * It searches for all open dialog windows and checks if the message matches the expected message.
     */
    public boolean isWarningDialogShown(String expectedMessage) {
        Window[] windows = Window.getWindows();
        for (Window window : windows) {
            if (window instanceof JDialog) {
                JDialog dialog = (JDialog) window;

                // Get the JOptionPane from the dialog
                JOptionPane optionPane = null;
                for (Component comp : dialog.getContentPane().getComponents()) {
                    if (comp instanceof JOptionPane) {
                        optionPane = (JOptionPane) comp;
                        break;
                    }
                }

                if (optionPane != null) {
                    // Debugging: Print the message
                    Object message = optionPane.getMessage();
                    System.out.println("Dialog message: " + message);

                    // Handle different message types and check if the message contains the expected text
                    if (message instanceof String) {
                        if (((String) message).contains(expectedMessage)) {
                            return true;  // Return true if the expected message is found
                        }
                    } else if (message instanceof Object[]) {
                        for (Object part : (Object[]) message) {
                            if (part instanceof String && ((String) part).contains(expectedMessage)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;  // Return false if no matching warning dialog is found
    }
}

