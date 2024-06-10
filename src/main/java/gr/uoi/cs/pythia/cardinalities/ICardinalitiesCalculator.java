package gr.uoi.cs.pythia.cardinalities;

import gr.uoi.cs.pythia.model.CardinalitiesProfile;

/**
 * 
 * @author pvassil
 *
 */
public interface ICardinalitiesCalculator {

    public CardinalitiesProfile createCardinalitiesProfile();
    public long getNumberOfNullValues();
    public long getNumberOfDistinctValues();
    //    public void calculateNumberOfNullValues();
    //    public void calculateDistincValues();

}
