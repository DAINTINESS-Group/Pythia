package gr.uoi.cs.pythia.patterns;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.patterns.algos.IPatternAlgo;
import gr.uoi.cs.pythia.patterns.algos.IPatternAlgoFactory;

// TODO maybe add logging here
public class PatternManager implements IPatternManager {
	
	// TODO Is this the best way to keep track of all supported pattern algos?
	private final IPatternAlgo[] patternAlgos = { 
			new IPatternAlgoFactory().createPattern(PatternConstants.HIGH_DOMINANCE),
			new IPatternAlgoFactory().createPattern(PatternConstants.LOW_DOMINANCE),
			new IPatternAlgoFactory().createPattern(PatternConstants.DISTRIBUTION)
	};
	
	private ColumnSelector columnSelector;
	private List<String> measurementColumns;
	private List<String> coordinateColumns;
	
	public PatternManager(
			ColumnSelectionMode columnSelectionMode, 
			String[] measurementColumns, 
			String[] coordinateColumns) {
		this.columnSelector = new ColumnSelector(
				columnSelectionMode, 
				measurementColumns, 
				coordinateColumns);
	}
	
	@Override
	public void identifyPatternHighlights(Dataset<Row> dataset, DatasetProfile datasetProfile) 
			throws IOException {
		// Select the measurement & coordinate columns for pattern identification
		measurementColumns = columnSelector.selectMeasurementColumns(datasetProfile);
		coordinateColumns = columnSelector.selectCoordinateColumns(datasetProfile);
		
		// Highlight identification can not proceed with no measurement/coordinate
		if (measurementColumns.isEmpty()) return;
		if (coordinateColumns.isEmpty()) return;
		
		// Pass all the measurement & coordinate column combinations 
		// through each of the highlight extractor modules (pattern algorithms)
		// for one & two coordinates respectively.
		identifyPatternHighlightsWithOneCoordinate(dataset);				
		identifyPatternHighlightsWithTwoCoordinates(dataset);
				
		// Once identification is done, export the results.
		exportResultsToFile();
		
		// TODO do we want to keep pattern results objects here?
	}

	private void identifyPatternHighlightsWithOneCoordinate(Dataset<Row> dataset) {
		for (String measurement : measurementColumns) {
			for (String xCoordinate : coordinateColumns) {
				for (IPatternAlgo algo : patternAlgos) {
					algo.identifyPatternWithOneCoordinate(dataset, measurement, xCoordinate);
				}
			}
		}
	}
	
	private void identifyPatternHighlightsWithTwoCoordinates(Dataset<Row> dataset) {
		for (String measurement : measurementColumns) {
			for (String xCoordinate : coordinateColumns) {
				for (String yCoordinate : coordinateColumns) {
					if (xCoordinate.equals(yCoordinate)) continue;
					for (IPatternAlgo algo : patternAlgos) {
						algo.identifyPatternWithTwoCoordinates(
								dataset, measurement, xCoordinate, yCoordinate);
					}
				}
			}
		}
	}

	private void exportResultsToFile() throws IOException {
		for (IPatternAlgo algo : patternAlgos) {
			algo.exportResultsToFile(new File(String.format(
				"src%stest%sresources%s%s_results.md",
				File.separator, File.separator, File.separator,
				algo.getPatternName())).getAbsolutePath());
		}
	}
	
}
