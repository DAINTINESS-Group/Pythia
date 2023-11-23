package gr.uoi.cs.pythia.storytelling.reporting;

import org.apache.log4j.Logger;

import gr.uoi.cs.pythia.storytelling.highlights.HolisticHighlight;

public class DescriptiveStatsHighlightsReporter implements IHighlightsReporter{

	private final Logger logger = Logger.getLogger(DescriptiveStatsHighlightsReporter.class);
			
	public DescriptiveStatsHighlightsReporter() {
	}

	@Override
	public String getModelHighlightsString(HolisticHighlight holisticHighlight) {
		holisticHighlight.setSupportingText(null);
		
		String highlightToString = "The Highlight Type " + holisticHighlight.getHighlightType() +
				" for the column " + holisticHighlight.getMainMeasure() +
				" tested via " + holisticHighlight.getHighlightExtractionAlgorithm() +
				" fits under the model " + holisticHighlight.getResultingModel() +
				" and with Score Value " + holisticHighlight.getScoreValue();
		
		logger.info(String.format("%s", highlightToString));
		
		return highlightToString;
	}

}
