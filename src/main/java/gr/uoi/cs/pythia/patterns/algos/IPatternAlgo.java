package gr.uoi.cs.pythia.patterns.algos;

import java.io.IOException;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public interface IPatternAlgo {
		
	/**
	 * This method performs the check for dominance with one 
	 * measurement & one coordinate column. A group-by query is executed
	 * on the dataset based on the given columns. Afterwards, the dominance 
	 * check is performed on the query results. Note that the method checks
	 * for both low & high dominance. All results, including any identified 
	 * highlights, are added to the list of results of the specific pattern.
	 * 
	 * @param dataset	the dataset that has been registered on DatasetProfiler
	 * @param measurementColumn	a string with the name of the measurement column
	 * @param xCoordinateColumn		a string with the name of coordinate X column
	 */
	void identifyPatternWithOneCoordinate(
			Dataset<Row> dataset, 
			String measurementColumn, 
			String xCoordinateColumn);
	
	/**
	 * This method performs the check for dominance with one 
	 * measurement & two coordinate columns. A group-by query is executed
	 * on the dataset based on the given columns. Afterwards, the dominance 
	 * check is performed on the query results. Note that the method checks
	 * for both low & high dominance. All results, including any identified 
	 * highlights, are added to the list of results of the specific pattern.
	 * 
	 * @param dataset	the dataset that has been registered on DatasetProfiler
	 * @param measurementColumn a string with the name of the measurement column
	 * @param xCoordinateColumn		a string with the name of coordinate X column
	 * @param yCoordinateColumn		a string with the name of coordinate Y column
	 */
	void identifyPatternWithTwoCoordinates(
			Dataset<Row> dataset, 
			String measurementColumn, 
			String xCoordinateColumn,
			String yCoordinateColumn);
	
	/**
	 * This method writes all the identification results of the specific pattern to a file
	 * located at the given path. The name of the file is generated w.r.t. the name of
	 * the pattern. This method is a step towards integrating all pattern
	 * identification results to the overall report. Therefore, it will most likely get 
	 * deleted or moved in a future commit.
	 * 
	 * @param path a string corresponding to the absolute path of the file to be created
	 * @throws IOException	in case something goes wrong with creating or writing to the file
	 */
	void exportResultsToFile(String path) throws IOException;
	
	/**
	 * Returns a string from PatternConstants with the name of the pattern.
	 * 
	 * @return a string with the name of the pattern.
	 */
	String getPatternName();
	
	
}
