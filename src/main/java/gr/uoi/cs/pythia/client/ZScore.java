package gr.uoi.cs.pythia.client;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;



public class ZScore {
    public static void main(String[] args) {
        ZScore program = new ZScore();
        double[] values = {9967, 11281, 10752, 10576, 2366, 11882, 11798};
        program.computeZScoreAndSurvivalFunctions(
                new DescriptiveStatistics(values),
                new NormalDistribution(),
                (index, zscore, sf) -> System.out.println("Value:" + values[index] + " Z-Score:" + zscore + " P-Value:" + sf)
        );
    }

    private void computeZScoreAndSurvivalFunctions(
            DescriptiveStatistics ds,
            RealDistribution dist,
            TriConsumer<Integer, Double, Double> consumer
    ) {
        double variance = ds.getPopulationVariance();
        double sd = Math.sqrt(variance);
        double mean = ds.getMean();
        System.out.println("Mean = " + mean);
        System.out.println("Variance = " + variance);
        System.out.println("Sd = " + sd);
        System.out.println();
        for (int index = 0; index < ds.getN(); ++index) {
            double zscore = (ds.getElement(index) - mean) / sd;
            double sf = 1.0 - dist.cumulativeProbability(Math.abs(zscore));
            consumer.accept(index, zscore, sf);
        }
    }

    @FunctionalInterface
    private interface TriConsumer<T, U, V> {
        void accept(T t, U u, V v);
    }
}

