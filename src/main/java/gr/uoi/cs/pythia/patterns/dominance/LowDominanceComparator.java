package gr.uoi.cs.pythia.patterns.dominance;

public class LowDominanceComparator implements IDominanceComparator {

    private static final String LOW = "low";

    public String getDominanceType() {
        return LOW;
    }

    @Override
    public boolean isDominant(double valueA, double valueB) {
        return valueA < valueB;
    }

}
