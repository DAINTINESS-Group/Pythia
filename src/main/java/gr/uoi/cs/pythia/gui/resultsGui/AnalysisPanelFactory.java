package gr.uoi.cs.pythia.gui.resultsGui;


public class AnalysisPanelFactory {
    public static AnalysisPanel createPanel(AnalysisType analysis) {
        if(analysis ==null){
            return null;
        }
        switch (analysis) {
            case DESCRIPTIVE_STATS:
                return new StatisticsPanel();
            case REGRESSION:
                return new RegressionPanel();
            case HISTOGRAMS:
                return new HistogramPanel();
            case ALL_PAIRS_CORRELATIONS:
                return new CorrelationsPanel();
            case CLUSTERING:
                return new ClusteringPanel();
            case LABELING_PARAMETERS:
                return new LabelingPanel();
            case OUTLIER_DETECTION:
                return new OutlierPanel();
            case DECISION_TREES:
                return new DecisionTreePanel();
            case DOMINANCE_PARAMETERS:
                return new DominancePanel();
            case HIGHLIGHT:
                /*
                 * Highlight functionality is not yet implemented.
                 * This section requires additional code to create and return
                 * a fully functional HighlightPanel.
                 * TODO: Implement the HighlightPanel class and its logic.
                 *   //return new HighlightPanel();
                 */
               return null;
            case TESTING:
                return null;
            default:
                throw new IllegalArgumentException("Unknown analysis type: " + analysis);
        }
    }
}