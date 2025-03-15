package gr.uoi.cs.pythia.outliers;

import gr.uoi.cs.pythia.model.outlier.OutlierType;

public class OutlierParameters{
    public final OutlierType type;
    public final double threshold;

    public OutlierParameters(OutlierType type, double threshold) {
        this.type = type;
        this.threshold = threshold;
    }

    @Override
    public String toString() {
        return "Type: " + type + "\nThreshold: " + threshold;
    }
}