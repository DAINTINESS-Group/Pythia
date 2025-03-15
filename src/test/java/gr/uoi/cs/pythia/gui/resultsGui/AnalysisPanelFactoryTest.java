package gr.uoi.cs.pythia.gui.resultsGui;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import org.junit.AfterClass;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class AnalysisPanelFactoryTest {

    /**
     * Cleanup after all tests are run.
     * This method resets the dataset and dataset profile in the AppController class.
     */
    @AfterClass
    public static void tearDown() throws Exception {
        setDataset(); // Reset the dataset
        setDatasetProfile(); // Reset the dataset profile
    }

    /**
     * Helper method to set the dataset in AppController.
     * This method uses reflection to set a private field in AppController.
     *
     * @throws Exception if there is an issue accessing the field
     */
    private static void setDataset() throws Exception {
        Field field = AppController.class.getDeclaredField("dataset");
        field.setAccessible(true);
        field.set(AppController.getInstance(), null);
    }

    /**
     * Helper method to set the dataset profile in AppController.
     * This method uses reflection to set a private field in AppController.
     *
     * @throws Exception if there is an issue accessing the field
     */
    private static void setDatasetProfile() throws Exception {
        Field field = AppController.class.getDeclaredField("datasetProfile");
        field.setAccessible(true);
        field.set(AppController.getInstance(), null);
    }

    /**
     * Test to verify that the correct panel is created for Descriptive Statistics.
     * It checks if the created panel is an instance of StatisticsPanel.
     */
    @Test
    public void testCreatePanel_DescriptiveStats() {
        AnalysisPanel panel = AnalysisPanelFactory.createPanel(AnalysisType.DESCRIPTIVE_STATS);
        assertTrue(panel instanceof StatisticsPanel); // Check if the panel is of type StatisticsPanel
    }

    /**
     * Test to verify that the correct panel is created for Regression analysis.
     * It checks if the created panel is an instance of RegressionPanel.
     */
    @Test
    public void testCreatePanel_Regression() {
        AnalysisPanel panel = AnalysisPanelFactory.createPanel(AnalysisType.REGRESSION);
        assertTrue(panel instanceof RegressionPanel); // Check if the panel is of type RegressionPanel
    }

    /**
     * Test to verify that the correct panel is created for Histogram analysis.
     * It checks if the created panel is an instance of HistogramPanel.
     */
    @Test
    public void testCreatePanel_Histograms() {
        AnalysisPanel panel = AnalysisPanelFactory.createPanel(AnalysisType.HISTOGRAMS);
        assertTrue(panel instanceof HistogramPanel); // Check if the panel is of type HistogramPanel
    }

    /**
     * Test to verify that the correct panel is created for All Pairs Correlations.
     * It checks if the created panel is an instance of CorrelationsPanel.
     */
    @Test
    public void testCreatePanel_AllPairsCorrelations() {
        AnalysisPanel panel = AnalysisPanelFactory.createPanel(AnalysisType.ALL_PAIRS_CORRELATIONS);
        assertTrue(panel instanceof CorrelationsPanel); // Check if the panel is of type CorrelationsPanel
    }

    /**
     * Test to verify that the correct panel is created for Clustering analysis.
     * It checks if the created panel is an instance of ClusteringPanel.
     */
    @Test
    public void testCreatePanel_Clustering() {
        AnalysisPanel panel = AnalysisPanelFactory.createPanel(AnalysisType.CLUSTERING);
        assertTrue(panel instanceof ClusteringPanel); // Check if the panel is of type ClusteringPanel
    }

    /**
     * Test to verify that the correct panel is created for Labeling Parameters.
     * It checks if the created panel is an instance of LabelingPanel.
     */
    @Test
    public void testCreatePanel_LabelingParameters() {
        AnalysisPanel panel = AnalysisPanelFactory.createPanel(AnalysisType.LABELING_PARAMETERS);
        assertTrue(panel instanceof LabelingPanel); // Check if the panel is of type LabelingPanel
    }

    /**
     * Test to verify that the correct panel is created for Outlier Detection.
     * It checks if the created panel is an instance of OutlierPanel.
     */
    @Test
    public void testCreatePanel_OutlierDetection() {
        AnalysisPanel panel = AnalysisPanelFactory.createPanel(AnalysisType.OUTLIER_DETECTION);
        assertTrue(panel instanceof OutlierPanel); // Check if the panel is of type OutlierPanel
    }

    /**
     * Test to verify that the correct panel is created for Decision Trees.
     * It checks if the created panel is an instance of DecisionTreePanel.
     */
    @Test
    public void testCreatePanel_DecisionTrees() {
        AnalysisPanel panel = AnalysisPanelFactory.createPanel(AnalysisType.DECISION_TREES);
        assertTrue(panel instanceof DecisionTreePanel); // Check if the panel is of type DecisionTreePanel
    }

    /**
     * Test to verify that the correct panel is created for Dominance Parameters.
     * It checks if the created panel is an instance of DominancePanel.
     */
    @Test
    public void testCreatePanel_DominanceParameters() {
        AnalysisPanel panel = AnalysisPanelFactory.createPanel(AnalysisType.DOMINANCE_PARAMETERS);
        assertTrue(panel instanceof DominancePanel); // Check if the panel is of type DominancePanel
    }

    /**
     * Test to verify that no panel is created for the "Highlight" analysis type.
     * It checks if the created panel is null.
     */
    @Test
    public void testCreatePanel_Highlight() {
        AnalysisPanel panel = AnalysisPanelFactory.createPanel(AnalysisType.HIGHLIGHT);
         assertNull(panel); // Panel should be null for "Highlight" type
    }

    /**
     * Test to verify that no panel is created when the analysis type is null.
     * It checks if the created panel is null.
     */
    @Test
    public void testCreatePanel_NullAnalysisType() {
        assertNull(AnalysisPanelFactory.createPanel(null)); // Panel should be null for null analysis type
    }

    /**
     * Test to verify that no panel is created for an unknown analysis type.
     * It checks if the created panel is null when an unknown analysis type is provided.
     */
    @Test
    public void testCreatePanel_UNKNOWAnalysisType() {
        assertNull(AnalysisPanelFactory.createPanel(AnalysisType.TESTING)); // Panel should be null for unknown analysis type
    }
}
