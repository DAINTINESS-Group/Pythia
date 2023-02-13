package gr.uoi.cs.pythia.correlations;

import gr.uoi.cs.pythia.correlations.calculator.ICorrelationsCalculator;
import gr.uoi.cs.pythia.correlations.calculator.PearsonCorrelationsCalculator;

public class CorrelationsCalculatorFactory {

  public ICorrelationsCalculator createCorrelationsCalculator(CorrelationsMethod type) {
    switch (type) {
      case PEARSON:
        return new PearsonCorrelationsCalculator();
    }
    throw new IllegalArgumentException(
        String.format("Correlations Calculator %s is not a valid calculator type.", type));
  }
}
