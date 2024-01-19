package gr.uoi.cs.pythia.patterns.dominance;

import gr.uoi.cs.pythia.model.dominance.DominanceResult;

public interface IDominanceAlgo {

    /**
     * This is the main method responsible for identifying single coordinate dominance.
     * Internally, a query is executed based on the given measurement and coordinate columns.
     * Afterwards, the single coordinate dominance algorithm is executed on the query result.
     * The findings are stored on a newly created DominanceResult object.
     *
     * @param measurementColName - A String with the name of the measurement column.
     * @param xCoordinateColName - A String with the name of the X coordinate column.
     *
     * @return A DominanceResult object that contains the analysis findings.
     */
    DominanceResult identifyDominanceWithOneCoordinate(
            String measurementColName,
            String xCoordinateColName);

    /**
     * This is the main method responsible for identifying double coordinate dominance.
     * Internally, a query is executed based on the given measurement and coordinate columns.
     * Afterwards, the double coordinate dominance algorithm is executed on the query result.
     * The findings are stored on a newly created DominanceResult object.
     *
     * @param measurementColName - A String with the name of the measurement column.
     * @param xCoordinateColName - A String with the name of the X coordinate column.
     * @param yCoordinateColName - A String with the name of the Y coordinate column.
     *
     * @return A DominanceResult object that contains the analysis findings.
     */
    DominanceResult identifyDominanceWithTwoCoordinates(
            String measurementColName,
            String xCoordinateColName,
            String yCoordinateColName);
}
