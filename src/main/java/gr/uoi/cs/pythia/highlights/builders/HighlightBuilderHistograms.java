package gr.uoi.cs.pythia.highlights.builders;

import java.util.ArrayList;
import java.util.List;

import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.highlights.ElementaryHighlight;
import gr.uoi.cs.pythia.model.highlights.HolisticHighlight;
import gr.uoi.cs.pythia.model.histogram.Bin;

public class HighlightBuilderHistograms {
	private DatasetProfile datasetProfile;
	private List<Column> columns;

	public HighlightBuilderHistograms(DatasetProfile datasetProfile) {
		this.datasetProfile = datasetProfile;
		this.columns = this.datasetProfile.getColumns();
	}

	public List<HolisticHighlight> extractHolisticHighlights() {
		
		List<HolisticHighlight> fullListHLs = new ArrayList<HolisticHighlight>();
		for(Column c: columns) {
			if(c.getHistogram() != null) {
				String columnName = c.getName();

//				//how many tuples are counted in the column
//				int totalNumberOfCountedTuples = 0;
//				for(Bin bin: c.getHistogram().getBins()) {
//					totalNumberOfCountedTuples += bin.getCount();
//				}
//				
				List<ElementaryHighlight> eHighlights = new ArrayList<ElementaryHighlight>();
				
				/*
				 * ElementaryHighlight(List<String> characterSet, 
				 *    List<String> characterTypes, String measureType, String measureValue, 
				 *    String highlightType, String scoreType, String scoreValue)
				 */
				
				for(Bin bin: c.getHistogram().getBins()) {
					ElementaryHighlight lowerBound = new ElementaryHighlight(null, null, columnName, null,
							"Histogram Bin Upper Bound", "Histogram Bin Lower Bound", Double.toString(bin.getLowerBound()));
					
					ElementaryHighlight upperBound = new ElementaryHighlight(null, null, columnName, null,
							"Histogram Bin Upper Bound", "Histogram Bin Upper Bound", Double.toString(bin.getUpperBound()));
					
					ElementaryHighlight valuesCount = new ElementaryHighlight(null, null, columnName, null,
							"Histogram Bin Values Count", "Histogram Bin Values Count", Long.toString(bin.getCount()));
					
					eHighlights.add(lowerBound);
					eHighlights.add(upperBound);
					eHighlights.add(valuesCount);
				}
				
			/*
			 	String highlightType, String mainMeasure, String highlightExtractionAlgorithm, String supportingRole,
		 		String resultingModel, 
		 		String scoreType, String scoreValue, 
		 		List<ElementaryHighlight> elementaryHighlights)
		 		
		 		The score should be some form of skewness / kurtosis / bias /giniIndex .. anyway non-uniformity of the bins
		 		   //https://en.wikipedia.org/wiki/Gini_coefficient  also within 0..1
			 */
				double columnHistoScore = c.getHistogram().getHistoSkewness(); //Double.MIN_VALUE; 
				
				HolisticHighlight hHighlight = new HolisticHighlight("Histogram", columnName, "a histogram constructor", null, 
						"True", 
						"Histogram Non-Uniformity", columnHistoScore, eHighlights);

				fullListHLs.add(hHighlight);
			}
		}
		
		
		return fullListHLs;
	}

}
