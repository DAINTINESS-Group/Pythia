package gr.uoi.cs.pythia.patterns.outlier;

import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.outlier.OutlierResult;

public interface IOutlierAlgo {

	/**
	 * A simple method that returns a string stating the specific outlier algorithm 
	 * implementation. Used for reporting purposes.
	 * 
	 * @return A string that states the name of the implemented outlier algorithm.
	 */
	String getOutlierType();
	
	/**
	 * This method is responsible for identifying outliers among the applicable columns of the dataset that is
	 * accepted as a parameter. Column validity regarding outlier detection is performed separately -within the
	 * method- for each specific outlier algorithm implementation.

	 * @param dataset - A Dataset object corresponding to the dataset that is registered into the system.
	 * @param datasetProfile - A DatasetProfile object corresponding to the registered dataset. The DatasetProfile
	 * object is required to access information such as column names & data types which is needed to determine 
	 * column validity for the given outlier algorithm implementation.
	 * @return A list of OutlierResult objects, containing all the identified outliers for all applicable columns.
	 */
	List<OutlierResult> identifyOutliers(Dataset<Row> dataset, DatasetProfile datasetProfile);
	
}
