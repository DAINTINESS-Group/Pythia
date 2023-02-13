package gr.uoi.cs.pythia.descriptivestatistics;

import gr.uoi.cs.pythia.descriptivestatistics.calculator.DescriptiveStatisticsCalculator;
import gr.uoi.cs.pythia.descriptivestatistics.calculator.IDescriptiveStatisticsCalculator;

public class DescriptiveStatisticsFactory {

    public IDescriptiveStatisticsCalculator getDefaultCalculator() {
        return new DescriptiveStatisticsCalculator();
    }
}
