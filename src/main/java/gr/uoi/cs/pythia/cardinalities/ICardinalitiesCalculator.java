package gr.uoi.cs.pythia.cardinalities;

import gr.uoi.cs.pythia.model.CardinalitiesProfile;

/**
 * Interface for calculating cardinalities profile.
 * <p>
 * Implementations of this interface are responsible for computing the
 * cardinalities profile of a given Column. The cardinalities profile
 * typically includes information about the distinct counts of values,
 * the number of null values.
 * </p>
 */
public interface ICardinalitiesCalculator {
    /**
     * @return a {@link CardinalitiesProfile} object containing the
     * cardinalities information for the column, including distinct counts
     * and null value counts.
     */
    public CardinalitiesProfile computeCardinalityProfile();
}
