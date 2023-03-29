package gr.uoi.cs.pythia.patterns;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import gr.uoi.cs.pythia.config.AnalysisParameters;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.patterns.algos.dominance.DominanceColumnSelector;
import gr.uoi.cs.pythia.patterns.algos.dominance.HighDominanceAlgo;
import gr.uoi.cs.pythia.patterns.algos.dominance.LowDominanceAlgo;
import gr.uoi.cs.pythia.patterns.algos.outlier.ZScoreOutlierAlgo;

// TODO maybe add logging here
public class PatternManager implements IPatternManager {
	
	private Dataset<Row> dataset;
	private DatasetProfile datasetProfile;
	private AnalysisParameters analysisParameters;
	
	private HighDominanceAlgo highDominanceAlgo;
	private LowDominanceAlgo lowDominanceAlgo;
	private ZScoreOutlierAlgo zScoreOutlierAlgo;
	
	public PatternManager(
			Dataset<Row> dataset,
			DatasetProfile datasetProfile,
			AnalysisParameters analysisParameters) {
		this.dataset = dataset;
		this.datasetProfile = datasetProfile;
		this.analysisParameters = analysisParameters;
		initializePatternAlgos();
	}
	
	private void initializePatternAlgos() {
		highDominanceAlgo = new HighDominanceAlgo(dataset);
		lowDominanceAlgo = new LowDominanceAlgo(dataset);
		zScoreOutlierAlgo = new ZScoreOutlierAlgo();
	}
	
	@Override
	public void identifyHighlightPatterns() throws IOException {
		identifyDominance();
		identifyZScoreOutlier();
	}

	private void identifyDominance() throws IOException {
		DominanceColumnSelector columnSelector = new DominanceColumnSelector(analysisParameters);
		
		// Select the measurement & coordinate columns for dominance highlight identification
		List<String> measurementColumns = columnSelector.selectMeasurementColumns(datasetProfile);
		List<String> coordinateColumns = columnSelector.selectCoordinateColumns(datasetProfile);
		
		// Highlight identification can not proceed with no measurement/coordinate
		if (measurementColumns.isEmpty()) return;
		if (coordinateColumns.isEmpty()) return;
		
		// Pass all the measurement & coordinate column combinations 
		// through low & high dominance identification algorithms
		// for one & two coordinates respectively.
		identifyDominanceWithOneCoordinate(measurementColumns, coordinateColumns);
		identifyDominanceWithTwoCoordinates(measurementColumns, coordinateColumns);

		// Once identification is done, export the results.
		highDominanceAlgo.exportResultsToFile(new File(String.format(
				"src%stest%sresources%s%s_results.md",
				File.separator, File.separator, File.separator,
				highDominanceAlgo.getPatternName())).getAbsolutePath());
		lowDominanceAlgo.exportResultsToFile(new File(String.format(
				"src%stest%sresources%s%s_results.md",
				File.separator, File.separator, File.separator,
				lowDominanceAlgo.getPatternName())).getAbsolutePath());
		
		// TODO do we want to keep pattern results objects here?
	}
	
	private void identifyDominanceWithOneCoordinate(
			List<String> measurementColumns, List<String> coordinateColumns) {
		for (String measurement : measurementColumns) {
			for (String xCoordinate : coordinateColumns) {
				highDominanceAlgo.identifyDominanceWithOneCoordinate(measurement, xCoordinate);
				lowDominanceAlgo.identifyDominanceWithOneCoordinate(measurement, xCoordinate);
			}
		}
	}
	
	private void identifyDominanceWithTwoCoordinates(
			List<String> measurementColumns, List<String> coordinateColumns) {
		for (String measurement : measurementColumns) {
			for (String xCoordinate : coordinateColumns) {
				for (String yCoordinate : coordinateColumns) {
					if (xCoordinate.equals(yCoordinate)) continue;
					highDominanceAlgo.identifyDominanceWithTwoCoordinates(
							measurement, xCoordinate, yCoordinate);
					lowDominanceAlgo.identifyDominanceWithTwoCoordinates(
							measurement, xCoordinate, yCoordinate);
				}
			}
		}
	}

	private void identifyZScoreOutlier() {
		// TODO
		zScoreOutlierAlgo.identifyZScoreOutliers();
	}
	
}
