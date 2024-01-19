package gr.uoi.cs.pythia.patterns.dominance;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class DominanceAlgoFactory {

    public enum DominanceAlgoVersion {
        INITIAL_HIGH,
        INITIAL_LOW,
        OPTIMIZED_HIGH,
        OPTIMIZED_LOW,
    }

    public IDominanceAlgo generateDominanceAlgo(DominanceAlgoVersion version, Dataset<Row> dataset) {
        switch(version) {
            case INITIAL_HIGH: return new HighDominanceAlgo(dataset);
            case INITIAL_LOW: return new LowDominanceAlgo(dataset);
            case OPTIMIZED_HIGH: return new OptimizedHighDominanceAlgo(dataset);
            case OPTIMIZED_LOW: return new OptimizedLowDominanceAlgo(dataset);
            default: return new OptimizedHighDominanceAlgo(dataset);
        }
    }
}
