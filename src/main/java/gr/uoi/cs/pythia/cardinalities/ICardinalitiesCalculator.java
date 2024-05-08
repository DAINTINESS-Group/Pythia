package gr.uoi.cs.pythia.cardinalities;

import gr.uoi.cs.pythia.model.CardinalitiesProfile;

public interface ICardinalitiesCalculator {

    CardinalitiesProfile createCardinalitiesProfile();
    void calculateNumberOfNullValues();
    void calculateDistincValues();
    long getNumberOfNullValues();
    long getNumberOfDistinctValues();
}
