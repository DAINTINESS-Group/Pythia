package gr.uoi.cs.pythia.patterns.dominance;

public class HighDominanceComparator implements IDominanceComparator {

    private static final String HIGH = "high";

    public String getDominanceType() {
        return HIGH;
    }

    @Override
    public boolean isDominant(double valueA, double valueB) {
        return valueA > valueB;
    }

}
