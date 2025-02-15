package gr.uoi.cs.pythia.valueDistributionStatistics;

import gr.uoi.cs.pythia.histogram.generator.IHistogramGenerator;
import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DescriptiveStatisticsProfile;
import gr.uoi.cs.pythia.model.DistributionsValues.QuartilesProfile;
import gr.uoi.cs.pythia.model.DistributionsValuesProfile;
import gr.uoi.cs.pythia.model.histogram.Bin;
import gr.uoi.cs.pythia.model.histogram.Histogram;
import org.apache.spark.ml.feature.Bucketizer;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.util.*;

public class QuartilesHistogramGenerator implements IHistogramGenerator{
    private final Dataset<Row> dataset;
    private final Column column;

    public QuartilesHistogramGenerator(Dataset<Row> dataset, Column column){
        this.dataset = dataset;
        this.column = column;
    }

    @Override
    public Histogram generateHistogram(int bins){
        /*double[] splits = getSplits();*/
        double[] splits = getSplits();
        Dataset<Row> binsToCounts = getBinsToCounts(splits);
        return createHistogram(binsToCounts, splits);
    }

    private double[] getSplits(){
        DescriptiveStatisticsProfile descriptiveStatistics = column.getDescriptiveStatisticsProfile();
        if(descriptiveStatistics==null){
            return null;
        }
        double min = Double.parseDouble(descriptiveStatistics.getMin());
        double max = Double.parseDouble(descriptiveStatistics.getMax());
        double epsilon = 0.0001; // Μικρό περιθώριο για αποφυγή διπλοτύπων

        if(min==max){
            return new double[]{min-epsilon, min, min+epsilon};
        }

        //QuartilesProfile quartilesProfile =column.getQuartilesProfile();
        DistributionsValuesProfile profile = column.getDistributionsValuesProfile();
        if(profile==null){
            return null;
        }
        QuartilesProfile quartilesProfile = profile.getQuartileProfile();
        if(quartilesProfile==null){
            return null;
        }
        double[] splits = new double[]{
                min,
                quartilesProfile.getQ1().doubleValue(),
                quartilesProfile.getQ2().doubleValue(),
                quartilesProfile.getQ3().doubleValue(),
                max
        };

        // Εξασφαλίζουμε ότι δεν υπάρχουν διπλότυπα, προσθέτοντας epsilon
        for(int i = 1; i < splits.length; i++){
            if(splits[i] <= splits[i-1]){
                splits[i] = splits[i-1]+epsilon;
            }
        }

        return splits;
    }


    private Dataset<Row> getBinsToCounts(double[] splits){
        if(splits==null){
            return SparkSession.builder().getOrCreate().emptyDataFrame();
        }
        Bucketizer bucketizer = new Bucketizer()
                .setInputCol(column.getName())
                .setOutputCol("bin")
                .setHandleInvalid("skip")
                .setSplits(splits);

        return bucketizer
                .transform(dataset.select(column.getName()))
                .groupBy("bin")
                .count()
                .orderBy("bin");
    }


    private Histogram createHistogram(Dataset<Row> binsToCounts, double[] splits){
        if(binsToCounts.isEmpty()){
            return new Histogram(column.getName(), new ArrayList<>());
        }
        List<Bin> bins = new ArrayList<>();
        Map<Double, Long> lowerBoundToCount = new HashMap<>();
        Iterator<Row> rowIterator = binsToCounts.toLocalIterator();

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            int binNumber = new Double(row.getDouble(0)).intValue();
            lowerBoundToCount.put(splits[binNumber], row.getLong(1));
        }

        for(int i = 0; i < splits.length-1; i++){
            long count = 0;
            if(lowerBoundToCount.containsKey(splits[i])){
                count = lowerBoundToCount.get(splits[i]);
            }
            boolean isUpperBoundIncluded = i==splits.length-2;
            Bin bin = new Bin(splits[i], splits[i+1], count, isUpperBoundIncluded);
            bins.add(bin);
        }
        return new Histogram(column.getName(), bins);
    }
}

