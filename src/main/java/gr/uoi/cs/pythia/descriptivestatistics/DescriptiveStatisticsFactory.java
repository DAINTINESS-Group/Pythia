package gr.uoi.cs.pythia.descriptivestatistics;

public class DescriptiveStatisticsFactory {

    public IDescriptiveStatisticsCalculator getDefaultCalculator() {
        return new DescriptiveStatisticsCalculator();
    }
}
