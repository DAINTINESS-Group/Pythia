package gr.uoi.cs.pythia.engine;

import gr.uoi.cs.pythia.util.HighlightParameters;

public class DatasetProfilerParameters {

  private final String auxiliaryDataOutputDirectory;
  private boolean shouldRunDescriptiveStats = false;
  private boolean shouldRunHistograms = false;
  private boolean shouldRunAllPairsCorrelations = false;
  private boolean shouldRunDecisionTrees = false;
  private boolean shouldRunDominancePatterns = false;
  private boolean shouldRunOutlierDetection = false;
  private boolean shouldRunRegression = false;
  private boolean shouldRunClustering = false;
  private HighlightParameters highLightsParameters = null;
  
  /**
   * Determines the dataset analysis parts that should be executed.
   *
   * @param auxiliaryDataOutputDirectory  - The directory where the auxiliary data,
   *                                      for example: images of the decision trees,
   *                                      will be generated, to later be used by the report.
   *                                      If it is empty or null, it will be generated in the dataset's folder.
   * @param shouldRunDescriptiveStats     - Boolean value that defines whether descriptive
   *                                      statistics should be calculated as part of the dataset profiling analysis.
   * @param shouldRunHistograms           - Boolean value that defines whether histograms should be
   *                                      calculated as part of the dataset profiling analysis.
   * @param shouldRunAllPairsCorrelations - Boolean value that defines whether all pairs correlations
   *                                      should be calculated as part of the dataset profiling analysis.
   * @param shouldRunDecisionTrees        - Boolean value that defines whether decision trees should
   *                                      be calculated as part of the dataset profiling analysis.
   * @param shouldRunDominancePatterns    - Boolean value that defines whether the dataset should be
   *                                      evaluated for dominance patterns as part of the dataset profiling analysis.
   * @param shoudRunRegression				- Boolean value that defines whether the dataset should be
   *                                      evaluated for regression as part of the dataset profiling analysis.
   * @param highLightsParameters		- a HighLightsParameters object with (a) the mode of highlightSelection, and, (b) the threshold
   * 
   */
  public DatasetProfilerParameters(
          String auxiliaryDataOutputDirectory,
          boolean shouldRunDescriptiveStats,
          boolean shouldRunHistograms,
          boolean shouldRunAllPairsCorrelations,
          boolean shouldRunDecisionTrees,
          boolean shouldRunDominancePatterns,
          boolean shouldRunOutlierDetection,
          boolean shouldRunRegression,
          boolean shouldRunClustering,
          HighlightParameters highLightsParameters) {
    this.auxiliaryDataOutputDirectory = auxiliaryDataOutputDirectory;
    this.shouldRunDescriptiveStats = shouldRunDescriptiveStats;
    this.shouldRunHistograms = shouldRunHistograms;
    this.shouldRunAllPairsCorrelations = shouldRunAllPairsCorrelations;
    this.shouldRunDecisionTrees = shouldRunDecisionTrees;
    this.shouldRunDominancePatterns = shouldRunDominancePatterns;
    this.shouldRunOutlierDetection = shouldRunOutlierDetection;
    this.shouldRunRegression = shouldRunRegression;
    this.shouldRunClustering = shouldRunClustering;
    this.highLightsParameters = highLightsParameters;
  }

  public String getAuxiliaryDataOutputDirectory() {
    return this.auxiliaryDataOutputDirectory;
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

  public boolean shouldRunDominancePatterns() {
    return shouldRunDominancePatterns;
  }
  
  public boolean shouldRunOutlierDetection() {
	  return shouldRunOutlierDetection;
  }

  public boolean shouldRunRegression() {
	  return shouldRunRegression;
  }
  
  public boolean shouldRunClustering() {
	  return shouldRunClustering;
  }

  public HighlightParameters getHighLightsParameters() {
		return highLightsParameters;
  }
}