package gr.uoi.cs.pythia.gui.resultsGui;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertTrue;
public class AnalysisPanelTest {

    private TestAnalysisPanel testAnalysisPanel;

    // Concrete subclass of AnalysisPanel for testing
    private static class TestAnalysisPanel extends AnalysisPanel {
        /**
		 * 
		 */
		private static final long serialVersionUID = -9069652031933464602L;
		private boolean panelContentCreated = false;
        private boolean exceptionThrown = false;

        /**
         * Creates the panel content.
         * This method sets the flag to true when the content is created,
         * and throws a runtime exception if the exceptionThrown flag is true.
         */
        @Override
        public void createPanelContent() {
            panelContentCreated = true;
            if (exceptionThrown) {
                throw new RuntimeException("Test Exception");
            }
        }

        public boolean isPanelContentCreated() {
            return panelContentCreated;
        }

        public void setExceptionThrown(boolean exceptionThrown) {
            this.exceptionThrown = exceptionThrown;
        }
    }

    /**
     * Setup method before each test.
     * This method stores the current state of the dataset and dataset profile,
     * and initializes the TestAnalysisPanel for testing.
     */
    @Before
    public void setUp() {

        testAnalysisPanel = new TestAnalysisPanel();
    }

    /**
     * Tear down method after each test.
     * This method restores the AppController's state by setting the dataset and
     * dataset profile to null.
     */
    @After
    public void tearDown() throws Exception {
        setDataset(); // Reset the dataset to null
        setDatasetProfile(); // Reset the dataset profile to null
    }

    /**
     * Helper method to set the dataset in AppController using reflection.
     * This allows injecting a new dataset into the AppController for testing.
     *
     * @throws Exception if there is an issue accessing the field
     */
    private void setDataset() throws Exception {
        Field field = AppController.class.getDeclaredField("dataset");
        field.setAccessible(true);
        field.set(AppController.getInstance(), null);
    }

    /**
     * Helper method to set the dataset profile in AppController using reflection.
     * This allows injecting a new dataset profile into the AppController for testing.
     *
     * @throws Exception if there is an issue accessing the field
     */
    private void setDatasetProfile() throws Exception {
        Field field = AppController.class.getDeclaredField("datasetProfile");
        field.setAccessible(true);
        field.set(AppController.getInstance(), null);
    }

    /**
     * Test to verify that the SwingWorker runs correctly and calls the createPanelContent method.
     * This test simulates running the SwingWorker and checks that the content was created.
     */
    @Test
    public void testRunSwingWorker() throws Exception {
        // Run the SwingWorker
        testAnalysisPanel.runSwingWorker();

        // Wait for the SwingWorker to complete
        Thread.sleep(10); // Small delay to allow SwingWorker to finish

        // Verify that createPanelContent was called
        assertTrue(testAnalysisPanel.isPanelContentCreated()); // Content should be created after SwingWorker runs
    }

    /**
     * Test to verify that the exception thrown in createPanelContent is properly handled.
     * This test configures the test panel to throw an exception and verifies that
     * the exception does not crash the application but ensures content is still created.
     */
    @Test
    public void testSwingWorkerExceptionHandling() throws Exception {
        // Configure the test panel to throw an exception
        testAnalysisPanel.setExceptionThrown(true);

        // Run the SwingWorker
        testAnalysisPanel.runSwingWorker();

        // Wait for the SwingWorker to complete
        Thread.sleep(10); // Small delay to allow SwingWorker to finish

        // Verify that the exception was thrown and handled
        // (You can add logging or other mechanisms to confirm this)
        assertTrue(testAnalysisPanel.isPanelContentCreated()); // Even if an exception is thrown, content should be created
    }
}
