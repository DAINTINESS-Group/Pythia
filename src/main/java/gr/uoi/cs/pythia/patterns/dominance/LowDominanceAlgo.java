package gr.uoi.cs.pythia.patterns.dominance;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class LowDominanceAlgo extends DominanceAlgo {

	private static final String LOW = "low";
	
	public LowDominanceAlgo(Dataset<Row> dataset) {
		super(dataset);
	}
	
	@Override
	public String getDominanceType() {
		return LOW;
	}
	
	@Override
	protected boolean isDominant(double valueA, double valueB) {
		return valueA < valueB;
	}

}
