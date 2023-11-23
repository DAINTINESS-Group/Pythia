package gr.uoi.cs.pythia.storytelling.reporting;

import org.apache.log4j.Logger;

import gr.uoi.cs.pythia.storytelling.highlights.HolisticHighlight;

public class CorrelationsHighlightsReporter implements IHighlightsReporter{

	private final Logger logger = Logger.getLogger(CorrelationsHighlightsReporter.class);
	
	public CorrelationsHighlightsReporter() {
		
	}

	@Override
	public String getModelHighlightsString(HolisticHighlight holisticHighlight) {
		holisticHighlight.setSupportingText(" with respect to the measure column");
		
		String highlightToString = "The Highlight Type " + holisticHighlight.getHighlightType() +
				" for the column " + holisticHighlight.getMainMeasure() +
				" tested via " + holisticHighlight.getHighlightExtractionAlgorithm() +
				holisticHighlight.getSupportingText() + " " + holisticHighlight.getSupportingRole() +
				" fits under the model " + holisticHighlight.getResultingModel() +
				" with Score Type " + holisticHighlight.getScoreType() +
				" and with Score Value " + holisticHighlight.getScoreValue();
		
		
		logger.info(String.format("%s", highlightToString));
		
		return highlightToString;
	}

}
