package gr.uoi.cs.pythia.model.histogram;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.descriptive.moment.Skewness;

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
    
    public double getHistoSkewness() {
        Skewness skew = new Skewness(); 
        List<Double> sizes = new ArrayList<Double>();
        for(Bin bin: bins)
        	sizes.add((double)(long)bin.getCount());
        //convert Li<Double> to array of double, double[]
        double[] arr = sizes.stream().mapToDouble(d -> d).toArray();
        return skew.evaluate(arr);
    }
}
