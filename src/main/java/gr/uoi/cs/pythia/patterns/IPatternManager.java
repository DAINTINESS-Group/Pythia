package gr.uoi.cs.pythia.patterns;

import java.io.IOException;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import gr.uoi.cs.pythia.model.DatasetProfile;

public interface IPatternManager {

	// TODO implementation details of this method are likely to change so revisit javadoc
	/**
	 * This is the main method regarding pattern identification on the Pythia system.
	 * Measurement & coordinate columns are selected with the help of the ColumnSelector
	 * class. In case of no selected measurement or coordinate columns, highlight identification
	 * is halted.  Otherwise, all the measurement & coordinate column combinations are passed
	 * through each of the highlight extractor modules (pattern algorithms) for one & two 
	 * coordinates respectively. Once identification is done, results are exported. Currently, results
	 * are exported to separate files per pattern algorithm. Eventually, results will be added to the
	 * overall report produced by the Pythia system.
	 * 
	 * @param dataset	the dataset that has been registered on DatasetProfiler
	 * @param datasetProfile the DatasetProfile object corresponding to the dataset that has been 
	 * 	registered on DatasetProfiler
	 * @throws IOException	in case something goes wrong with creating or writing to the output files
	 */
	public void identifyPatternHighlights(Dataset<Row> dataset, DatasetProfile datasetProfile) 
			throws IOException;
}
