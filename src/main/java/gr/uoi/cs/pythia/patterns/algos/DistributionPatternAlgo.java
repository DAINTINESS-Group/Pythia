package gr.uoi.cs.pythia.patterns.algos;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.stat.KernelDensity;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class DistributionPatternAlgo implements IPatternAlgo {

	@Override
	public void identify(
			Dataset<Row> dataset, 
			String measurementColName, 
			String xCoordinateColName)
					throws IOException {
		// TODO Actually check for a distribution highlight
		// Currently this method has an example of usage of the KernelDensity estimate method
		SparkContext sc =SparkContext.getOrCreate();
		JavaSparkContext jsc = JavaSparkContext.fromSparkContext(sc);
		
		// Just a dummy JavaRDD
		JavaRDD<Double> data = jsc.parallelize(
				  Arrays.asList(1.0, 1.0, 1.0, 2.0, 3.0, 4.0, 5.0, 5.0, 6.0, 7.0, 8.0, 9.0, 9.0));
		
		List<Double> datasetColumn = dataset.select("mileage")
				.collectAsList().stream()
				.map(s -> Double.parseDouble(s.get(0) == null ? "0"  : s.get(0).toString()))
				.collect(Collectors.toList());
		
		JavaRDD<Double> datasetColumnRDD = jsc.parallelize(datasetColumn);
		// Construct the density estimator with the sample data
		// and a standard deviation for the Gaussian kernels
		KernelDensity kd = new KernelDensity().setSample(datasetColumnRDD).setBandwidth(3.0);

		// Find density estimates for the given values
		double[] densities = kd.estimate(new double[]{-1.0, 2.0, 5.0});

		System.out.println(Arrays.toString(densities));
		
	}

	@Override
	public void identify(
			Dataset<Row> dataset, 
			String measurementColName, 
			String xCoordinateColName,
			String yCoordinateColName) 
					throws IOException {
		// TODO Auto-generated method stub
		
	}

}
