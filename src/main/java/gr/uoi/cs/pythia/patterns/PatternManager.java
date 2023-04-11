package gr.uoi.cs.pythia.patterns;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.patterns.dominance.DominanceColumnSelector;
import gr.uoi.cs.pythia.patterns.dominance.DominanceParameters;
import gr.uoi.cs.pythia.patterns.dominance.HighDominanceAlgo;
import gr.uoi.cs.pythia.patterns.dominance.LowDominanceAlgo;
import gr.uoi.cs.pythia.patterns.outlier.IOutlierAlgo;
import gr.uoi.cs.pythia.patterns.outlier.OutlierAlgoFactory;
import gr.uoi.cs.pythia.patterns.outlier.OutlierType;

// TODO maybe add logging here
public class PatternManager implements IPatternManager {
	
	private Dataset<Row> dataset;
	private DatasetProfile datasetProfile;
	private DominanceParameters dominanceAnalysisParameters;
	
	private HighDominanceAlgo highDominanceAlgo;
	private LowDominanceAlgo lowDominanceAlgo;
	private IOutlierAlgo outlierAlgo;
	
	public PatternManager(
			Dataset<Row> dataset,
			DatasetProfile datasetProfile,
			DominanceParameters dominanceAnalysisParameters) {
		this.dataset = dataset;
		this.datasetProfile = datasetProfile;
		this.dominanceAnalysisParameters = dominanceAnalysisParameters;
		initializePatternAlgos();
	}
	
	private void initializePatternAlgos() {
		highDominanceAlgo = new HighDominanceAlgo(dataset);
		lowDominanceAlgo = new LowDominanceAlgo(dataset);
		outlierAlgo = new OutlierAlgoFactory()
				.createOutlierAlgo(OutlierType.Z_SCORE);
	}
	
	@Override
	public void identifyHighlightPatterns() throws IOException {
		identifyDominance();
		identifyOutliers();
	}

	private void identifyDominance() throws IOException {
		DominanceColumnSelector columnSelector = new DominanceColumnSelector(dominanceAnalysisParameters);
		
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
		highDominanceAlgo.exportResultsToFile(
				new File(String.format("src%stest%sresources%s%s_results.md",
						File.separator, File.separator, File.separator,
						highDominanceAlgo.getPatternName())).getAbsolutePath());
		lowDominanceAlgo.exportResultsToFile(
				new File(String.format("src%stest%sresources%s%s_results.md",
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

	private void identifyOutliers() throws IOException {
		outlierAlgo.identifyOutliers(dataset, datasetProfile);
		outlierAlgo.exportResultsToFile(
				new File(String.format("src%stest%sresources%s%s_results.md",
				File.separator, File.separator, File.separator,
				outlierAlgo.getPatternName())).getAbsolutePath());
	}
	
}
