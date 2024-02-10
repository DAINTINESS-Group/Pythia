package gr.uoi.cs.pythia.patterns.dominance;

import gr.uoi.cs.pythia.model.dominance.DominanceResult;

import java.util.Map;

public interface IDominanceAlgo {

    /**
     * This is the main method responsible for identifying single coordinate dominance.
     * Internally, a query is executed based on the given measurement and coordinate columns.
     * Afterwards, the single coordinate dominance algorithm is executed on the query result.
     * The findings are stored on a newly created DominanceResult object.
     *
     * @param measurement - A String with the name of the measurement column.
     * @param xCoordinate - A String with the name of the X coordinate column.
     *
     * @return A HashMap of DominanceResult objects that contains the analysis findings for high
     * and/or low dominance, under the keys "high" and "low" respectively.
     */
    Map<String, DominanceResult> identifySingleCoordinateDominance(
            String measurement,
            String xCoordinate);

    /**
     * This is the main method responsible for identifying double coordinate dominance.
     * Internally, a query is executed based on the given measurement and coordinate columns.
     * Afterwards, the double coordinate dominance algorithm is executed on the query result.
     * The findings are stored on a newly created DominanceResult object.
     *
     * @param measurement - A String with the name of the measurement column.
     * @param xCoordinate - A String with the name of the X coordinate column.
     * @param yCoordinate - A String with the name of the Y coordinate column.
     *
     * @return A HashMap of DominanceResult objects that contains the analysis findings for high
     * and/or low dominance, under the keys "high" and "low" respectively.
     */
    Map<String, DominanceResult> identifyDoubleCoordinateDominance(
            String measurement,
            String xCoordinate,
            String yCoordinate);
}
