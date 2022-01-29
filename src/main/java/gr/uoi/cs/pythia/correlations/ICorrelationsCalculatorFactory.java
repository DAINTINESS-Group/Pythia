package gr.uoi.cs.pythia.correlations;

public class ICorrelationsCalculatorFactory {

  public static ICorrelationsCalculator createCorrelationsCalculator(String type) {
    switch (type) {
      case CorrelationsSystemConstants.PEARSON:
        return new PearsonCorrelationsCalculator();
    }
    throw new IllegalArgumentException(
        String.format("Correlations Calculator %s is not a valid calculator type.", type));
  }
}
