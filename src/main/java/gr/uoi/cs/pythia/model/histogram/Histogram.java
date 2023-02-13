package gr.uoi.cs.pythia.model.histogram;

import java.util.List;
import java.util.stream.Collectors;

public class Histogram {

    private final String columnName;
    private final List<Bin> bins;

    public Histogram(String columnName, List<Bin> bins) {
        this.columnName = columnName;
        this.bins = bins;
    }

    public String getColumnName() {
        return columnName;
    }

    public List<Bin> getBins() {
        return bins;
    }

    @Override
    public String toString() {
        return bins.stream()
                   .map(Bin::toString)
                   .collect(Collectors.joining("\n"))
                + "\n";
    }
}
