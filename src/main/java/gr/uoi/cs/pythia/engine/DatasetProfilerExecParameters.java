package gr.uoi.cs.pythia.engine;


public class DatasetProfilerExecParameters{
	private String outputPath;
	private boolean shouldRunDescriptiveStats=false;
	private boolean shouldRunHistograms=false;
	private boolean shouldRunAllPairsCorrelations=false;
	private boolean shouldRunDecisionTrees=false;
	private boolean shouldRunHighlightPatterns=false;

	/**
	 * determines the parts of the profiler to be executed
	 * 
	 * @param outputPath The directory where the auxiliary data,
	 *             for example: images of the decision trees,
	 *             will be generated, to later be used by the report.
	 *             If it is empty or null, it will be generated in the dataset's folder.
	 * @param shouldRunDescriptiveStats
	 * @param shouldRunHistograms
	 * @param shouldRunAllPairsCorrelations
	 * @param shouldRunDecisionTrees
	 * @param shouldRunHighlightPatterns
	 */
	public DatasetProfilerExecParameters(String outputPath, boolean shouldRunDescriptiveStats, boolean shouldRunHistograms,
			boolean shouldRunAllPairsCorrelations, boolean shouldRunDecisionTrees, boolean shouldRunHighlightPatterns) {
		this.outputPath = outputPath;
		this.shouldRunDescriptiveStats = shouldRunDescriptiveStats;
		this.shouldRunHistograms = shouldRunHistograms;
		this.shouldRunAllPairsCorrelations = shouldRunAllPairsCorrelations;
		this.shouldRunDecisionTrees = shouldRunDecisionTrees;
		this.shouldRunHighlightPatterns = shouldRunHighlightPatterns;
	}

	public String getOutputPath() {
		return this.outputPath;
	}
	public boolean shouldRunDescriptiveStats() {
		return shouldRunDescriptiveStats;
	}

	public boolean shouldRunHistograms() {
		return shouldRunHistograms;
	}

	public boolean shouldRunAllPairsCorrelations() {
		return shouldRunAllPairsCorrelations;
	}

	public boolean shouldRunDecisionTrees() {
		return shouldRunDecisionTrees;
	}

	public boolean shouldRunHighlightPatterns() {
		return shouldRunHighlightPatterns;
	}

	//	public DatasetProfilerExecParameters setShouldRunDescriptiveStats(boolean shouldRunDescriptiveStats) {
	//		this.shouldRunDescriptiveStats = shouldRunDescriptiveStats;
	//		return this;
	//	}
	//
	//	public DatasetProfilerExecParameters setShouldRunHistograms(boolean shouldRunHistograms) {
	//		this.shouldRunHistograms = shouldRunHistograms;
	//		return this;
	//	}
	//
	//	public DatasetProfilerExecParameters setShouldRunAllPairsCorrelations(boolean shouldRunAllPairsCorrelations) {
	//		this.shouldRunAllPairsCorrelations = shouldRunAllPairsCorrelations;
	//		return this;
	//	}
	//
	//	public DatasetProfilerExecParameters setShouldRunDecisionTrees(boolean shouldRunDecisionTrees) {
	//		this.shouldRunDecisionTrees = shouldRunDecisionTrees;
	//		return this;
	//	}
	//
	//	public DatasetProfilerExecParameters setShouldRunHighlightPatterns(boolean shouldRunHighlightPatterns) {
	//		this.shouldRunHighlightPatterns = shouldRunHighlightPatterns;
	//		return this;
	//	}
	//		  

}//end DatasetProfilerExecParameters