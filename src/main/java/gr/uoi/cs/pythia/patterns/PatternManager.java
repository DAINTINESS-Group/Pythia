package gr.uoi.cs.pythia.patterns;

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
	public void identifyPatternHighlights(Dataset<Row> dataset, DatasetProfile datasetProfile) {
		// TODO This is just a rough sketch for the overall pattern identification execution flow
		
		// Get the measurement column 
		List<Row> measurementColumn = determineMeasurementColumn(dataset, datasetProfile);
		
		// Get the cooridnate column(s)
		List<Row> coordinateColumn = determineCoordinateColumn(dataset, datasetProfile);
		
		// Pass the columns through each of the highlight extractor modules
		for (IPatternAlgo pattern : patterns) {
			pattern.identify(measurementColumn, coordinateColumn);
		}
		
		// TODO Decide what to do with the result, once a highlight pattern has been identified.
		// Maybe create a PatternResult object (and class) with everything required
		// for exporting on the dataset profile, visualization, etc.
		
		// TODO We probably want to identify patterns 
		// for more than one measurement-coordinate column pair
		
		// Random code - START - will delete on next commit
		Column ageCol = dataset.col("age");
		System.out.println(ageCol); 
		
		dataset.printSchema();
		
		 // Print whole dataset - only works for small datasets
		 for (Row row : dataset.collectAsList()) { 
			 for (int i=0; i<row.length(); i++) { 
				 System.out.print(row.get(i) + " "); 
			 }
		 System.out.println(); 
		 }
		// Random code - END
	}

	private List<Row> determineCoordinateColumn(
			Dataset<Row> dataset, DatasetProfile datasetProfile) {
		// TODO how do we determine coordinate column?
		String columnName = datasetProfile.getColumns().get(2).getName();
		return dataset.select(columnName).collectAsList();
	}

	private List<Row> determineMeasurementColumn(
			Dataset<Row> dataset, DatasetProfile datasetProfile) {
		// TODO how do we determine measurement column?
		String columnName = datasetProfile.getColumns().get(1).getName();
		return dataset.select(columnName).collectAsList();
	}

}
