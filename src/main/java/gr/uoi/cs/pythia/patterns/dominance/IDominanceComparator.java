package gr.uoi.cs.pythia.patterns.dominance;

public interface IDominanceComparator {

    /**
     * @return A String that describes the dominance type (high or low).
     */
    String getDominanceType();

    /**
     * Method responsible for determining whether value A is dominant compared to value B.
     *
     * @param valueA - a double with measurement value A.
     * @param valueB - a double with measurement value B.
     *
     * @return A boolean that states whether value A is dominant over value B.
     */
    boolean isDominant(double valueA, double valueB);
}
