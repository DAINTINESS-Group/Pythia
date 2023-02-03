package gr.uoi.cs.pythia.histogram.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Histogram {

    private final String columnName;
    private final List<Bin> bins = new ArrayList<>();

    public Histogram(String columnName) {
        this.columnName = columnName;
    }

    public List<Bin> getBins() {
        return bins;
    }

    public void addBin(Bin bin) {
        bins.add(bin);
    }

    @Override
    public String toString() {
        return String.format("----- %s -----\n", columnName)
                + bins.stream()
                      .map(Bin::toString)
                      .collect(Collectors.joining("\n"))
                + "\n";
    }
}
