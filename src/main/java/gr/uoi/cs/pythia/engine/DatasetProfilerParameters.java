package gr.uoi.cs.pythia.engine;


public class DatasetProfilerParameters {

  private final String auxiliaryDataOutputDirectory;
  private boolean shouldRunDescriptiveStats = false;
  private boolean shouldRunHistograms = false;
  private boolean shouldRunAllPairsCorrelations = false;
  private boolean shouldRunDecisionTrees = false;
  private boolean shouldRunDominancePatterns = false;
  private boolean shouldRunOutlierDetection = false;

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
   */
  public DatasetProfilerParameters(
          String auxiliaryDataOutputDirectory,
          boolean shouldRunDescriptiveStats,
          boolean shouldRunHistograms,
          boolean shouldRunAllPairsCorrelations,
          boolean shouldRunDecisionTrees,
          boolean shouldRunDominancePatterns,
          boolean shouldRunOutlierDetection) {
    this.auxiliaryDataOutputDirectory = auxiliaryDataOutputDirectory;
    this.shouldRunDescriptiveStats = shouldRunDescriptiveStats;
    this.shouldRunHistograms = shouldRunHistograms;
    this.shouldRunAllPairsCorrelations = shouldRunAllPairsCorrelations;
    this.shouldRunDecisionTrees = shouldRunDecisionTrees;
    this.shouldRunDominancePatterns = shouldRunDominancePatterns;
    this.shouldRunOutlierDetection = shouldRunOutlierDetection;
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

}