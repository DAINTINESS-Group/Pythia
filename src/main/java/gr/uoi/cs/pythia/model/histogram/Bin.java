package gr.uoi.cs.pythia.model.histogram;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Bin {

    private final double lowerBound;
    private final double upperBound;
    private final long count;
    private final String upperBoundIncludedText;
    private final DecimalFormat decimalFormat = new DecimalFormat("#.###",
    		new DecimalFormatSymbols(Locale.ENGLISH));
    
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
        return String.format("[%s,%s%s", decimalFormat.format(lowerBound),
        		decimalFormat.format(upperBound),
                                         upperBoundIncludedText);
    }

    @Override
    public String toString() {
        return String.format("[%s,%s%s: %d values", decimalFormat.format(lowerBound),
        		decimalFormat.format(upperBound),
        		upperBoundIncludedText,
        		count);
    }
}
