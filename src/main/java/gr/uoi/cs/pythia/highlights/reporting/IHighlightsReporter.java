package gr.uoi.cs.pythia.highlights.reporting;

import gr.uoi.cs.pythia.model.highlights.HolisticHighlight;

@Deprecated
public interface IHighlightsReporter {
	
	/**
	 * Takes as input the holistic highlights of a model and returns a String with a highlight report.
	 * @param holisticHighlight
	 * @return A highlight report
	 */
	String getModelHighlightsString(HolisticHighlight holisticHighlight);

}
