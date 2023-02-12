package gr.uoi.cs.pythia.descriptivestatistics;

import gr.uoi.cs.pythia.descriptivestatistics.generator.DescriptiveStatisticsGenerator;
import gr.uoi.cs.pythia.descriptivestatistics.generator.IDescriptiveStatisticsGenerator;

public class DescriptiveStatisticsFactory {

    public IDescriptiveStatisticsGenerator getDefaultGenerator() {
        return new DescriptiveStatisticsGenerator();
    }
}
