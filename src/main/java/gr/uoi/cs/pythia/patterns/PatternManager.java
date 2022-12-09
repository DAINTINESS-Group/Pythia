package gr.uoi.cs.pythia.patterns;

import java.io.IOException;
import java.util.List;

import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.patterns.algos.IPatternAlgo;
import gr.uoi.cs.pythia.patterns.algos.IPatternAlgoFactory;

public class PatternManager implements IPatternManager {

	private final IPatternAlgo[] patterns;
	
	public PatternManager() {
		// TODO Is this the best way to keep track of all supported patterns?
		patterns = new IPatternAlgo[] { 
			new IPatternAlgoFactory().createPattern(PatternConstants.DOMINANCE),
			new IPatternAlgoFactory().createPattern(PatternConstants.DISTRIBUTION)
		};
	}
	
	@Override
	public void identifyPatternHighlights(Dataset<Row> dataset, DatasetProfile datasetProfile) 
			throws IOException {
		// TODO This class is most likely missing responsibilities 
		// e.g. measurement/coordinates columns selection
		// e.g. initialization of highlight pattern result objects
		// These responsibilites are currently in the DominancePatternAlgo class
		

		// Pass the columns through each of the highlight extractor modules
		for (IPatternAlgo pattern : patterns) {
			pattern.identify(dataset, datasetProfile);
		}
		
		// TODO Decide what to do with the result, once a highlight pattern has been identified.
		// Maybe create a PatternResult object (and class) with everything required
		// for exporting on the dataset profile, visualization, etc.
		
		// TODO We probably want to identify patterns 
		// for more than one measurement-coordinate column pair
	}
	
}
