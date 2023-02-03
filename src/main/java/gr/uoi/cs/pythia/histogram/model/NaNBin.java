package gr.uoi.cs.pythia.histogram.model;

public class NaNBin extends Bin{

    public NaNBin(long count) {
        super(Double.NaN, Double.NaN, count, false);
    }

    @Override
    public String getBoundsLabel() {
        return "NaN";
    }

    @Override
    public String toString() {
        return String.format("NaN: %d values", super.getCount());
    }
}
