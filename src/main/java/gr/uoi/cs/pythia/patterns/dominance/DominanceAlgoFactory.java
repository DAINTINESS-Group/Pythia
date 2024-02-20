package gr.uoi.cs.pythia.patterns.dominance;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

/*
    V00: The baseline dominance algorithm, used in experiments regarding dominance optimizations.

    V01: Optimized such that distinct coordinate values in double coordinate dominance
    are not fetched via query, but rather via the aggregate query result.
    (see: getDistinctValuesFromQueryResult method)

    V02: Optimized such that both high and low dominance checks are performed
    in a single algorithm execution. Also, includes optimization in version V01.
*/
public class DominanceAlgoFactory {


    public enum DominanceAlgoVersion {
        V00_HIGH,
        V00_LOW,
        V01_HIGH,
        V01_LOW,
        V02_HIGH,
        V02_LOW,
        V02_HIGH_AND_LOW,
    }

    @SuppressWarnings("deprecation")
    public IDominanceAlgo generateDominanceAlgo(DominanceAlgoVersion version, Dataset<Row> dataset) {
        switch(version) {
            case V00_HIGH: return new DominanceAlgoV00(dataset, new HighDominanceComparator());
            case V00_LOW: return new DominanceAlgoV00(dataset, new LowDominanceComparator());
            case V01_HIGH: return new DominanceAlgoV01(dataset, new HighDominanceComparator());
            case V01_LOW: return new DominanceAlgoV01(dataset, new LowDominanceComparator());
            case V02_HIGH: return new DominanceAlgoV02(dataset, new HighDominanceComparator(), null);
            case V02_LOW: return new DominanceAlgoV02(dataset, null, new LowDominanceComparator());
            case V02_HIGH_AND_LOW: {
                return new DominanceAlgoV02(dataset, new HighDominanceComparator(), new LowDominanceComparator());
            }
            default: {
                return new DominanceAlgoV02(dataset, new HighDominanceComparator(), new LowDominanceComparator());
            }
        }
    }
}
