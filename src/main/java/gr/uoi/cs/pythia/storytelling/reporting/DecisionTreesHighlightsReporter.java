package gr.uoi.cs.pythia.storytelling.reporting;

import org.apache.log4j.Logger;

import gr.uoi.cs.pythia.storytelling.highlights.HolisticHighlight;

public class DecisionTreesHighlightsReporter implements IHighlightsReporter{

	private final Logger logger = Logger.getLogger(DecisionTreesHighlightsReporter.class);
	public DecisionTreesHighlightsReporter() {
	}

	@Override
	public String getModelHighlightsString(HolisticHighlight holisticHighlight) {
		// TODO Auto-generated method stub
		
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
