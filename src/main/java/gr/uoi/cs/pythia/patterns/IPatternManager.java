package gr.uoi.cs.pythia.patterns;

import java.io.IOException;

public interface IPatternManager {

  /**
   * This is the main method regarding highlight pattern identification in Pythia.
   * Internally, this method calls a dedicated method for each supported pattern,
   * where data preparation, such as measurement & coordinate column selection,
   * might be performed. Depending on the pattern, the selected data or the entire
   * dataset is then passed by the respective highlight identification algorithms. The
   * generated results are added to result objects lists in the PatternsProfile model
   * class.
   */
  void identifyHighlightPatterns()
          throws IOException;
}
