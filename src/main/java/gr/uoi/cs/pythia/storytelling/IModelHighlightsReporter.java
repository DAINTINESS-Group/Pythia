package gr.uoi.cs.pythia.storytelling;

public interface IModelHighlightsReporter {
	
	/**
	 * Takes as input the holistic highlights of a model and returns a String with a highlight report.
	 * @param holisticHighlight
	 * @return A highlight report
	 */
	String getModelHighlightsString(HolisticHighlight holisticHighlight);

}
