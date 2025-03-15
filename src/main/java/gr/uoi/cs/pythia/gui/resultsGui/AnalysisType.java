package gr.uoi.cs.pythia.gui.resultsGui;

public enum AnalysisType {
    DESCRIPTIVE_STATS("Descriptive Stats"),
    REGRESSION("Regression"),
    HISTOGRAMS("Histograms"),
    ALL_PAIRS_CORRELATIONS("All Pairs Correlations"),
    CLUSTERING("Clustering"),
    LABELING_PARAMETERS("Labeling Parameters"),
    OUTLIER_DETECTION("Outlier Detection"),
    DECISION_TREES("Decision Trees"),
    DOMINANCE_PARAMETERS("Dominance Patterns"),
    HIGHLIGHT("Highlight Parameters"),
    TESTING("Testing");

    // Consistent naming

    private final String label;

    AnalysisType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static AnalysisType fromString(String text) {
        for (AnalysisType type : AnalysisType.values()) {
            if (type.label.equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown analysis type: " + text);
    }
}