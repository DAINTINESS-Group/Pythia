package gr.uoi.cs.pythia.patterns;

import java.io.IOException;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.patterns.algos.IPatternAlgo;
import gr.uoi.cs.pythia.patterns.algos.IPatternAlgoFactory;

public class PatternManager implements IPatternManager {

	private final IPatternAlgo[] patternAlgos;
	
	private String measurementColName;
	private String xCoordinateColName;
	private String yCoordinateColName;
	
	public PatternManager() {
		// TODO Is this the best way to keep track of all supported patterns?
		patternAlgos = new IPatternAlgo[] { 
			new IPatternAlgoFactory().createPattern(PatternConstants.DOMINANCE),
			new IPatternAlgoFactory().createPattern(PatternConstants.DISTRIBUTION)
		};
	}
	
	@Override
	public void identifyPatternHighlights(Dataset<Row> dataset, DatasetProfile datasetProfile) 
			throws IOException {
		determineColumnNames(datasetProfile);

		// Pass the columns through each of the highlight extractor modules
		for (IPatternAlgo algo : patternAlgos) {
			// Identify pattern highlights with one coordinate column
			algo.identify(dataset, measurementColName, xCoordinateColName);
			
			// Identify pattern highlights with two coordinate columns
			algo.identify(dataset, measurementColName, xCoordinateColName, yCoordinateColName);
		}
		
		// TODO do we want to keep pattern results objects here?
	}
	
	// TODO We probably want to identify patterns 
	// for different measurement and coordinate(s) columns
	private void determineColumnNames(DatasetProfile datasetProfile) {
		// TODO how do we determine the measurement column?
		// Currently "mileage" column of the "cars" dataset is hard-coded.
		measurementColName = datasetProfile.getColumns().get(5).getName();
		
		// TODO how do we determine the coordinate X column?
		// Currently "model" column of the "cars" dataset is hard-coded.
		xCoordinateColName = datasetProfile.getColumns().get(1).getName();
		
		// TODO how do we determine the coordinate Y column?
		// Currently "year" column of the "cars" dataset is hard-coded.
		yCoordinateColName = datasetProfile.getColumns().get(2).getName();
	}	
}
