package gr.uoi.cs.pythia.model;

public class CardinalitiesProfile {
    private long numberOfDistinctValues;
    private long numberOfNullValues;


    public CardinalitiesProfile(long numberOfDistinctValues, long numberOfNullValues ) {

        this.numberOfDistinctValues = numberOfDistinctValues;
        this.numberOfNullValues = numberOfNullValues;
    }
    public long getNumberOfDistinctValues() {
        return numberOfDistinctValues;
    }

    public void setNumberOfDistinctValues(long numberOfDistinctValues) {
        this.numberOfDistinctValues = numberOfDistinctValues;
    }

    public long getNumberOfNullValues() {
        return numberOfNullValues;
    }

    public void setNumberOfNullValues(long numberOfNullValues) {
        this.numberOfNullValues = numberOfNullValues;
    }

    @Override
    public String toString() {
        return "numberOfDistinctValues: " + numberOfDistinctValues + "\n" +
                "numberOfNullValues: " + numberOfNullValues;
    }
}
