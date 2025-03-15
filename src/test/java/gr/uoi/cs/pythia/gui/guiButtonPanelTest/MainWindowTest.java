package gr.uoi.cs.pythia.gui.guiButtonPanelTest;

import gr.uoi.cs.pythia.gui.guiButtonPanels.ApplicationNavigationPanel;
import gr.uoi.cs.pythia.gui.guiButtonPanels.MainWindow;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;

import static org.junit.Assert.*;
public class MainWindowTest {

    private MainWindow mainWindow;

    @Before
    public void setUp() {
        // Initialize MainWindow before each test
        mainWindow = new MainWindow();
    }

    /**
     * Test for the creation of the MainWindow.
     * It checks if the main window is correctly initialized with the expected title, dimensions, and close operation.
     */
    @Test
    public void testMainWindowCreation() {
        assertNotNull(mainWindow);
        assertEquals("Pythia Data Profiling", mainWindow.getTitle()); // Ensure the title is set correctly
        assertEquals(1200, mainWindow.getWidth()); // Check if the width is as expected
        assertEquals(600, mainWindow.getHeight()); // Check if the height is as expected
        assertEquals(JFrame.EXIT_ON_CLOSE, mainWindow.getDefaultCloseOperation()); // Verify the default close operation
    }

    /**
     * Test for the showCard method.
     * It verifies if the correct panel is added to the card layout when showCard is called.
     */
    @Test
    public void testShowCard() {
        JPanel testPanel = new JPanel();
        String cardName = "testCard";
        mainWindow.showCard(testPanel, cardName);

        // Check if the testPanel is added as the second component (first one is the placeholder)
        assertEquals(testPanel, mainWindow.getCardPanel().getComponent(1));
    }

    /**
     * Test for the getMainWindow method.
     * It ensures that the method returns the same instance of MainWindow each time.
     */
    @Test
    public void testGetMainWindow() {
        MainWindow instance1 = MainWindow.getMainWindow(); // Get the first instance
        MainWindow instance2 = MainWindow.getMainWindow(); // Get the second instance

        // Verify both instances are not null
        assertNotNull(instance1);
        assertNotNull(instance2);

        // Ensure both instances are the same (singleton behavior)
        assertSame(instance1, instance2);
    }

    /**
     * Test for the getNavigationPanel method.
     * It checks if the ApplicationNavigationPanel is correctly initialized and accessible.
     */
    @Test
    public void testGetNavigationPanel() {
        ApplicationNavigationPanel navigationPanel = mainWindow.getNavigationPanel();
        assertNotNull(navigationPanel); // Ensure the navigation panel is not null
    }

}

