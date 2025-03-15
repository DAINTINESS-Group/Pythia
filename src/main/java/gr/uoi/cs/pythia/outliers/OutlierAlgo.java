package gr.uoi.cs.pythia.outliers;

import gr.uoi.cs.pythia.model.Column;
//import gr.uoi.cs.pythia.model.DatasetProfile;
//import gr.uoi.cs.pythia.model.OutlierProfile;
//import gr.uoi.cs.pythia.model.outlier.OutlierResult;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.DataTypes;

import java.util.List;
import java.util.stream.Collectors;

/**
 * An abstract class with reusable code for handling column values and stats
 * 
 * @author georgekarathanos
 *
 */
public abstract class OutlierAlgo {

	protected List<Double> getColumnValues(Dataset<Row> dataset, Column column) {
		return dataset
				.select(column.getName())
				.collectAsList()
				.stream()
				.map(s -> parseColumnValue(s.get(0)))
				.collect(Collectors.toList());
	}
	
	protected Double parseColumnValue(Object object) {
		if (object == null) return Double.NaN;
		return Double.parseDouble(object.toString());
	}

	protected Double getColumnMean(Column column) {
		return Double.parseDouble(column.getDescriptiveStatisticsProfile().getMean());
	}

	protected Double getColumnStandardDeviation(Column column) {
		return Double.parseDouble(column.getDescriptiveStatisticsProfile().getStandardDeviation());
	}
	
}
