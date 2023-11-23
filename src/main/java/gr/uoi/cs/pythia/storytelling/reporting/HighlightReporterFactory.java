package gr.uoi.cs.pythia.storytelling.reporting;

public class HighlightReporterFactory {

	public enum HighlightReporterType{
		CORREL, DEC_TREE, DESCR_STATS, HISTO, OUTLIER
	}
	
	public IHighlightsReporter createHighlightReporter(HighlightReporterType type) {
		switch(type) {
			case CORREL: return new CorrelationsHighlightsReporter(); 
			case DEC_TREE: return new DecisionTreesHighlightsReporter();
			case DESCR_STATS: return new DescriptiveStatsHighlightsReporter();
			case HISTO: return new HistogramsHighlightsReporter();
			case OUTLIER: return new OutlierHighlightsReporter();
			default: return null;
		}
	}
}
