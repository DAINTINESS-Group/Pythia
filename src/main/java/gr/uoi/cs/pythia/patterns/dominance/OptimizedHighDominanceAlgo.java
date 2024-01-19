package gr.uoi.cs.pythia.patterns.dominance;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class OptimizedHighDominanceAlgo extends OptimizedDominanceAlgo {

	private static final String HIGH = "high";
	
	public OptimizedHighDominanceAlgo(Dataset<Row> dataset) {
		super(dataset);
	}
	
	@Override
	public String getDominanceType() {
		return HIGH;
	}
	
	@Override
	protected boolean isDominant(double valueA, double valueB) {
		return valueA > valueB;
	}

}
