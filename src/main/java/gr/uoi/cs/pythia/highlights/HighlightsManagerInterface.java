package gr.uoi.cs.pythia.highlights;

import java.util.List;

import gr.uoi.cs.pythia.model.highlights.HolisticHighlight;
import gr.uoi.cs.pythia.util.HighlightParameters;

public interface HighlightsManagerInterface {

	/**
	 * Returns a list of Highlights from the profiling of a data set
	 * Depends on a set of flags, one per type of profiling task that we wish to 
	 * evaluate as a potential highlight
	 * 
	 * @param highlightParameters a HighlightParameters object with the mode of highlight selection and the respective threshold
	 * @param descriptiveStats a boolean flag on whether to treat as highlights the results of profiling for descriptiveStats   
	 * @param histograms a boolean flag on whether to treat as highlights the results of profiling for histograms
	 * @param allPairsCorrelations a boolean flag on whether to treat as highlights the results of profiling for correlations
	 * @param decisionTrees a boolean flag on whether to treat as highlights the results of profiling for decision trees
	 * @param outlierDetection a boolean flag on whether to treat as highlights the results of profiling for outliers
	 * @param dominancePatterns a boolean flag on whether to treat as highlights the results of profiling for dominance patterns
	 * @return
	 */
	List<HolisticHighlight> extractHighlightsForStorytelling(HighlightParameters highlightParameters, boolean descriptiveStats, boolean histograms,
			boolean allPairsCorrelations, boolean decisionTrees, boolean outlierDetection, boolean dominancePatterns);

}