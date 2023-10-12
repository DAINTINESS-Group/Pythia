package gr.uoi.cs.pythia.storytelling;

import java.util.List;

import org.apache.log4j.Logger;

public class HistogramsHighlightsReporter implements IModelHighlightsReporter{

	private final Logger logger = Logger.getLogger(HistogramsHighlightsReporter.class);
	
	public HistogramsHighlightsReporter() {
	}

	@Override
	public String getModelHighlightsString(HolisticHighlight holisticHighlight) {
		
		String highlightToString = buildHistogramHighlightsReport(holisticHighlight);
		logger.info(String.format("%s", highlightToString));
		return highlightToString;
	}
	
	private String buildHistogramHighlightsReport(HolisticHighlight holisticHighlight) {
		List<ElementaryHighlight> eHighlights = holisticHighlight.getElementaryHighlights();
		
		String highlightToString = "The Highlight Type " + holisticHighlight.getHighlightType() +
				" for the column " + holisticHighlight.getMainMeasure() +
				" tested via " + holisticHighlight.getHighlightExtractionAlgorithm() +
				" fits under the model " + holisticHighlight.getResultingModel() +
				", with Score Type " + holisticHighlight.getScoreType() +
				", Score Value " + holisticHighlight.getScoreValue() +
				", with the following Elementary Highlights: \n";
		
		for(int i=0; i<eHighlights.size(); i++) {
			highlightToString += "For column name " + eHighlights.get(i).getMeasureType() +
					" the value " + eHighlights.get(i).getScoreValue() + 
					" serves as a " + eHighlights.get(i).getScoreType() + "\n";
		}
		return highlightToString;
	}

}
