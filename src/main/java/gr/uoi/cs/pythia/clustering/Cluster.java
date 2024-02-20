package gr.uoi.cs.pythia.clustering;

import java.util.List;

public class Cluster {
	
	private int id;
	private int numOfPoints;
	private List<Double> mean;
	private List<Double> standardDeviations;
	private List<Double> median;
	private List<Double> min;
	private List<Double> max;
	private double error;
	
	
	public Cluster(int id, int numOfPoints, List<Double> mean, List<Double> standardDeviations,
			List<Double> median, List<Double> min, List<Double> max, double error) {
		super();
		this.id = id;
		this.numOfPoints = numOfPoints;
		this.mean = mean;
		this.standardDeviations = standardDeviations;
		this.median = median;
		this.min = min;
		this.max = max;
		this.error  = error;
	}

	public double getError() {
		return error;
	}



	public void setError(double error) {
		this.error = error;
	}



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNumOfPoints() {
		return numOfPoints;
	}

	public List<Double> getMean() {
		return mean;
	}

	public List<Double> getMedian() {
		return median;
	}

	public List<Double> getMin() {
		return min;
	}

	public List<Double> getMax() {
		return max;
	}

	public List<Double> getStandardDeviations() {
		return standardDeviations;
	}

	public void setNumOfPoints(int numOfPoints) {
		this.numOfPoints = numOfPoints;
	}

	public void setMean(List<Double> mean) {
		this.mean = mean;
	}

	public void setMedian(List<Double> median) {
		this.median = median;
	}

	public void setMin(List<Double> min) {
		this.min = min;
	}

	public void setMax(List<Double> max) {
		this.max = max;
	}

	public void setStandardDeviations(List<Double> standardDeviations) {
		this.standardDeviations = standardDeviations;
	}
	
	
}
