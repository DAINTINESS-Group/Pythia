package gr.uoi.cs.pythia.model.histogram;

import java.text.DecimalFormat;

public class Bin {

    private final double lowerBound;
    private final double upperBound;
    private final long count;
    private final String upperBoundIncludedText;

    public Bin(double lowerBound, double upperBound, long count, boolean isUpperBoundIncluded) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.count = count;
        this.upperBoundIncludedText = isUpperBoundIncluded ? "]" : ")";
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public double getUpperBound() {
        return upperBound;
    }

    public long getCount() {
        return count;
    }

    public String getBoundsLabel() {
        DecimalFormat df = new DecimalFormat("0.###");
        return String.format("[%s,%s%s", df.format(lowerBound),
                                         df.format(upperBound),
                                         upperBoundIncludedText);
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("0.###");
        return String.format("[%s,%s%s: %d values", df.format(lowerBound),
                                                    df.format(upperBound),
                                                    upperBoundIncludedText,
                                                    count);
    }
}
