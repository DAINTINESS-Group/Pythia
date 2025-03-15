package gr.uoi.cs.pythia.gui.guiButtonPanelTest;

import gr.uoi.cs.pythia.gui.analysisTasksGuiPanels.AnalysisTabsPanel;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class AnalysisTabsPanelTest {

    private AnalysisTabsPanel panel;
    private List<String> selectedAnalyses;

    @Before
    public void setUp() {
        // Initialize the list of selected analyses before each test
        selectedAnalyses = new ArrayList<>();
    }

    /**
     * Test for the initial setup of the AnalysisTabsPanel.
     * It checks that the tab list and cards panel are correctly initialized and present.
     */
    @Test
    public void testInitialTabs() {
        // Set up initial state with selected analyses
        selectedAnalyses.add("Descriptive Stats");
        selectedAnalyses.add("Clustering");
        panel = new AnalysisTabsPanel(selectedAnalyses, null, null, null);

        // Check if the necessary components are present in the panel
        Component[] components = panel.getComponents();
        boolean foundTabList = false;
        boolean foundCardsPanel = false;

        for (Component component : components) {
            if (component instanceof JPanel) {
                JPanel mainPanel = (JPanel) component;
                Component[] mainPanelComponents = mainPanel.getComponents();

                for (Component mainPanelComponent : mainPanelComponents) {
                    if (mainPanelComponent instanceof JScrollPane) {
                        JScrollPane scrollPane = (JScrollPane) mainPanelComponent;
                        if (scrollPane.getViewport().getView() instanceof JList) {
                            foundTabList = true;
                        }
                    } else if (mainPanelComponent instanceof JPanel) {
                        JPanel innerPanel = (JPanel) mainPanelComponent;
                        if (innerPanel.getLayout() instanceof CardLayout) {
                            foundCardsPanel = true;
                        }
                    }
                }
            }
        }

        // Assertions to ensure the tab list and cards panel are present
        assertTrue("Tab list should be present", foundTabList);
        assertTrue("Cards panel should be present", foundCardsPanel);
    }

    /**
     * Test for adding a new tab to the panel.
     * It checks if the new tab is correctly added to the cards panel.
     */
    @Test
    public void testAddTab() {
        // Set up initial state with selected analyses
        selectedAnalyses.add("Descriptive Stats");
        panel = new AnalysisTabsPanel(selectedAnalyses, null, null, null);

        // Create new tab content
        JPanel newTabContent = new JPanel();
        panel.addTab("New Tab", newTabContent);

        // Check if the new tab was added to the cards panel
        Component[] cards = panel.getCardsPanel().getComponents();
        boolean foundNewTab = false;

        for (Component card : cards) {
            if (card == newTabContent) {
                foundNewTab = true;
                break;
            }
        }

        // Assert that the new tab was added correctly
        assertTrue("New tab should be added to cards panel", foundNewTab);
    }

    /**
     * Test for removing tabs from the panel.
     * It ensures that when tabs are removed, the panel updates correctly.
     */
    @Test
    public void testRemoveTab() {
        // Set up initial state with selected analyses
        selectedAnalyses.add("Descriptive Stats");
        selectedAnalyses.add("New Tab");
        panel = new AnalysisTabsPanel(selectedAnalyses, null, null, null);

        // Remove some tabs from the panel
        panel.removeTab("Descriptive Stats");
        panel.removeTab("New Tab");
        panel.removeTab("Highlight");

        // Assert that no tabs remain in the cards panel
        Component[] cards = panel.getCardsPanel().getComponents();
        assertEquals("No tabs should remain", 0, cards.length);
    }

    /**
     * Test for the visibility of the "Compute All" button after removing the last tab.
     * It checks if the button becomes visible when no tabs remain.
     */
    @Test
    public void testComputeAllButtonVisibility_AfterRemovingLastTab() {
        // Set up initial state with no selected analyses
        selectedAnalyses = new ArrayList<>();
        panel = new AnalysisTabsPanel(selectedAnalyses, null, null, null);

        // Add two tabs to the panel
        panel.addTab("Tab 1", new JPanel());
        panel.addTab("Tab 2", new JPanel());

        // Initially, the "Compute All" button should be hidden
        assertFalse(panel.computeAllButton.isVisible());

        // Remove one tab and check if the button is still hidden
        panel.removeTab("Tab 2");
        assertFalse(panel.computeAllButton.isVisible());

        // Remove the last tab and check if the button is now visible
        panel.removeTab("Tab 1");
        panel.removeTab("Highlight");
        assertTrue(panel.computeAllButton.isVisible());
    }

    /**
     * Test for the visibility of the "Compute All" button after adding and removing tabs.
     * It ensures that the button behaves correctly when tabs are added and removed.
     */
    @Test
    public void testVisibilityAfterAddingAndRemovingTabs() {
        // Set up initial state with no selected analyses
        selectedAnalyses = new ArrayList<>();
        panel = new AnalysisTabsPanel(selectedAnalyses, null, null, null);

        // Add two tabs to the panel
        panel.addTab("Tab 1", new JPanel());
        panel.addTab("Tab 2", new JPanel());

        // Initially, the "Compute All" button should be hidden
        assertFalse(panel.computeAllButton.isVisible());

        // Remove the tabs and check if the button becomes visible
        panel.removeTab("Tab 1");
        panel.removeTab("Tab 2");
        panel.removeTab("Highlight");

        // When no tabs are left, the button should be visible
        assertTrue(panel.computeAllButton.isVisible());
    }
}
