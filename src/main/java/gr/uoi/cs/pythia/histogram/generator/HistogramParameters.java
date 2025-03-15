package gr.uoi.cs.pythia.histogram.generator;

public class HistogramParameters{
    private final HistogramGeneratorType type;
    private final int numberOfBins;

    public HistogramParameters(HistogramGeneratorType type, int numberOfBins) {
        this.type = type;
        this.numberOfBins = numberOfBins;
    }

    public int getNumberOfBins() {
        return numberOfBins;
    }
    public HistogramGeneratorType getType() {
        return type;
    }


}
