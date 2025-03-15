package gr.uoi.cs.pythia.gui.analysisTasksGuiPanels;


import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.engine.DatasetProfilerParameters;
import gr.uoi.cs.pythia.util.HighlightParameters;

import javax.swing.*;
import java.awt.*;

public class HighlightParametersGUI extends AnalysisParametersGUI<HighlightParameters> {

	private final JComboBox<HighlightParameters.HighlightExtractionMode> extractionModeComboBox = new JComboBox<>(HighlightParameters.HighlightExtractionMode.values());
    private final JTextField numericLimitField = new JTextField(10);

    public HighlightParametersGUI(AnalysisTabsPanel tabsGUI,String path,JPanel cardPanel, CardLayout cardLayout) {
        super("Highlight", tabsGUI, new HighlightParameterValidator(),
                p -> {
                    boolean shouldRunDescriptiveStats = tabsGUI.getAnalysisFlags().get("Descriptive Stats");
                    boolean shouldRunHistograms = tabsGUI.getAnalysisFlags().get("Histograms");
                    boolean shouldRunAllPairsCorrelations = tabsGUI.getAnalysisFlags().get("All Pairs Correlations");
                    boolean shouldRunDecisionTrees = tabsGUI.getAnalysisFlags().get("Decision Trees");
                    boolean shouldRunDominancePatterns = tabsGUI.getAnalysisFlags().get("Dominance Patterns");
                    boolean shouldRunOutlierDetection = tabsGUI.getAnalysisFlags().get("Outlier Detection");
                    boolean shouldRunRegression = tabsGUI.getAnalysisFlags().get("Regression");
                    boolean shouldRunClustering = tabsGUI.getAnalysisFlags().get("Clustering");

                    DatasetProfilerParameters datasetProfilerParameters = new DatasetProfilerParameters(path, shouldRunDescriptiveStats, shouldRunHistograms, shouldRunAllPairsCorrelations,
                            shouldRunDecisionTrees, shouldRunDominancePatterns, shouldRunOutlierDetection, shouldRunRegression, shouldRunClustering,p);

                    AppController.getInstance().createDatasetProfileParameters(datasetProfilerParameters);

                },cardPanel,cardLayout);

        addInputField("Extraction Mode", extractionModeComboBox);
        addInputField("Numeric Limit", numericLimitField);

    }
    @Override
    public void updateResultArea() {
        StringBuilder sb = new StringBuilder("Current Highlight Parameters:\n");
        HighlightParameters.HighlightExtractionMode mode = (HighlightParameters.HighlightExtractionMode) extractionModeComboBox.getSelectedItem();
        String numericLimitStr = numericLimitField.getText();

        sb.append("Extraction Mode: ").append(mode!= null? mode.toString(): "").append("\n");
        sb.append("Numeric Limit: ").append(numericLimitStr).append("\n");

        resultArea.setText(sb.toString());
    }
}