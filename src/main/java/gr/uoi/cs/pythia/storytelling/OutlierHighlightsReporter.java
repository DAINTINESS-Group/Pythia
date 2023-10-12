package gr.uoi.cs.pythia.storytelling;

import org.apache.log4j.Logger;

public class OutlierHighlightsReporter implements IModelHighlightsReporter{

	private final Logger logger = Logger.getLogger(OutlierHighlightsReporter.class);
	public OutlierHighlightsReporter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getModelHighlightsString(HolisticHighlight holisticHighlight) {
		// TODO Auto-generated method stub

		String highlightToString = "The Highlight Type " + holisticHighlight.getHighlightType() +
				" for the column " + holisticHighlight.getMainMeasure() +
				holisticHighlight.getSupportingText() +
				" tested via " + holisticHighlight.getHighlightExtractionAlgorithm() +
				" fits under the model " + holisticHighlight.getResultingModel() +
				" with Score Type " + holisticHighlight.getScoreType() +
				" and with Score Value " + holisticHighlight.getScoreValue();
		
		logger.info(String.format("%s", highlightToString));
		
		return highlightToString;

	}

}
