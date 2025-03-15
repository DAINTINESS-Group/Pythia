package gr.uoi.cs.pythia.gui.guiButtonPanelTest;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.gui.analysisTasksGuiPanels.DatasetWriterGUI;
import gr.uoi.cs.pythia.gui.analysisTasksGuiPanels.ReportGeneratorGUI;
import gr.uoi.cs.pythia.gui.guiButtonPanels.AnalysisSelectionPanel;
import gr.uoi.cs.pythia.gui.guiButtonPanels.ApplicationNavigationPanel;
import gr.uoi.cs.pythia.gui.guiButtonPanels.DatasetInputPanel;
import gr.uoi.cs.pythia.gui.guiButtonPanels.MainWindow;
import gr.uoi.cs.pythia.gui.resultsGui.ResultsPanelManager;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static org.junit.Assert.*;
public class ApplicationNavigationPanelTest {

    private ApplicationNavigationPanel panel;
    private MainWindow mainWindow;

    @Before
    public void setUp() {
        // Initialize MainWindow and ApplicationNavigationPanel before each test
        mainWindow = new MainWindow();
        panel = new ApplicationNavigationPanel(null, null);
    }

    /**
     * Test for the creation of the ApplicationNavigationPanel.
     * It checks if the panel is correctly created and if the layout and background color are set as expected.
     */
    @Test
    public void testApplicationNavigationPanelCreation() {
        assertNotNull(panel);
        assertEquals(BoxLayout.class, panel.getLayout().getClass());
        assertEquals(new Color(245, 245, 245), panel.getBackground());
    }

    /**
     * Test for the createButton method in ApplicationNavigationPanel.
     * It checks if the button is created correctly with the specified text, color, and properties.
     */
    @Test
    public void testCreateButton() {
        String text = "Test Button";
        Color color = Color.BLUE;
        boolean[] actionPerformed = {false};

        // Create a button using the method under test
        JButton button = panel.createButton(text, color, () -> actionPerformed[0] = true);

        assertNotNull(button);
        assertEquals(text, button.getText());
        assertEquals(color, button.getBackground());
        assertEquals(Color.WHITE, button.getForeground());
        assertEquals(200, button.getPreferredSize().width);
        assertEquals(40, button.getPreferredSize().height);

        // Simulate clicking the button
        for (ActionListener al : button.getActionListeners()) {
            al.actionPerformed(new ActionEvent(button, ActionEvent.ACTION_PERFORMED, ""));
            break;
        }
        assertTrue(actionPerformed[0]);
    }

    /**
     * Test for the "Register Dataset" button.
     * It checks if the correct panel (DatasetInputPanel) is displayed when the button is clicked.
     */
    @Test
    public void testRegisterDatasetButton() {
        JButton registerDatasetButton = findButton(panel, "Register Dataset");
        assertNotNull(registerDatasetButton);
        for (ActionListener al : registerDatasetButton.getActionListeners()) {
            al.actionPerformed(new ActionEvent(registerDatasetButton, ActionEvent.ACTION_PERFORMED, ""));
            break;
        }
        Container cardPanel = mainWindow.getCardPanel();
        Component component = findComponent(cardPanel, DatasetInputPanel.class);
        assertNotNull("DatasetInputPanel should be added to MainWindow", component);
    }

    /**
     * Test for the "Write Data" button.
     * It checks if the correct panel (DatasetWriterGUI) is displayed when the button is clicked.
     */
    @Test
    public void testWriteDataButton() {
        JButton writeDataButton = findButton(panel, "Write Data");
        assertNotNull(writeDataButton);
        for (ActionListener al : writeDataButton.getActionListeners()) {
            al.actionPerformed(new ActionEvent(writeDataButton, ActionEvent.ACTION_PERFORMED, ""));
            break;
        }
        Container cardPanel = mainWindow.getCardPanel();
        Component component = findComponent(cardPanel, DatasetWriterGUI.class);
        assertNotNull("DatasetWriterGUI should be added to MainWindow", component);
    }

    /**
     * Test for the "Generate Report" button.
     * It checks if the correct panel (ReportGeneratorGUI) is displayed when the button is clicked.
     */
    @Test
    public void testGenerateReportButton() {
        JButton generateReportButton = findButton(panel, "Generate Report");
        assertNotNull(generateReportButton);
        for (ActionListener al : generateReportButton.getActionListeners()) {
            al.actionPerformed(new ActionEvent(generateReportButton, ActionEvent.ACTION_PERFORMED, ""));
            break;
        }
        Container cardPanel = mainWindow.getCardPanel();
        Component component = findComponent(cardPanel, ReportGeneratorGUI.class);
        assertNotNull("ReportGeneratorGUI should be added to MainWindow", component);
    }

    /**
     * Test for the "Analysis Tasks Profile" button.
     * It checks if the correct panel (AnalysisSelectionPanel) is displayed when the button is clicked.
     */
    @Test
    public void testAnalysisProfileButton() {
        panel = new ApplicationNavigationPanel(null, null);
        mainWindow.getContentPane().add(panel);
        JButton analysisProfileButton = findButton(panel, "Analysis Tasks Profile");
        assertNotNull(analysisProfileButton);
        for (ActionListener al : analysisProfileButton.getActionListeners()) {
            al.actionPerformed(new ActionEvent(analysisProfileButton, ActionEvent.ACTION_PERFORMED, ""));
            break;
        }
        Container cardPanel = mainWindow.getCardPanel();
        Component component = findComponent(cardPanel, AnalysisSelectionPanel.class);
        assertNotNull("AnalysisSelectionPanel should be added to MainWindow", component);
    }

    /**
     * Test for the "Show Results" button.
     * It checks if the correct panel (ResultsPanelManager) is displayed when the button is clicked.
     */
    @Test
    public void testSetShowResults() {
        panel.analysisPanel = new AnalysisSelectionPanel(null, null);
        panel.setOnShowResultsButton();
        JButton showResultsButton = findButton(panel, "Show Results");
        assertNotNull(showResultsButton);
        for (ActionListener al : showResultsButton.getActionListeners()) {
            al.actionPerformed(new ActionEvent(showResultsButton, ActionEvent.ACTION_PERFORMED, ""));
            break;
        }
        Container cardPanel = mainWindow.getCardPanel(); // Use the cardPanel of MainWindow
        Component component = findComponent(cardPanel, ResultsPanelManager.class);
        assertNotNull(component);
    }

    /**
     * Test for the "Edit DataType" button.
     * It checks if the button is functional when a dataset is available.
     */
    @Test
    public void testSetEditDatatypesButton() {
        AppController appController = AppController.getInstance();
        Dataset<Row> dataset = appController.getDataset();
        if (dataset != null) {
            panel.setOnEditDatatypesButton();
            JButton editDataTypeButton = findButton(panel, "EditDataType");
            assertNotNull(editDataTypeButton);
            for (ActionListener al : editDataTypeButton.getActionListeners()) {
                al.actionPerformed(new ActionEvent(editDataTypeButton, ActionEvent.ACTION_PERFORMED, ""));
                break;
            }
        }
    }

    /**
     * Helper method to find a button by its text in the given container.
     */
    private JButton findButton(Container parent, String text) {
        for (Component c : parent.getComponents()) {
            if (c instanceof JButton && ((JButton) c).getText().equals(text)) {
                return (JButton) c;
            }
        }
        return null;
    }

    /**
     * Helper method to find a component of a specific class in the given container.
     */
    private Component findComponent(Container parent, Class<?> componentClass) {
        for (Component c : parent.getComponents()) {
            if (componentClass.isInstance(c)) {
                return c;
            }
        }
        return null;
    }
}
