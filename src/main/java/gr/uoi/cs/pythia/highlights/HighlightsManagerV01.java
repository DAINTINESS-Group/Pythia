package gr.uoi.cs.pythia.highlights;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.LabeledColumn;
import gr.uoi.cs.pythia.model.decisiontree.DecisionTree;
import gr.uoi.cs.pythia.model.highlights.ElementaryHighlight;
import gr.uoi.cs.pythia.model.highlights.HighlightsProfile;
import gr.uoi.cs.pythia.model.highlights.HolisticHighlight;
import gr.uoi.cs.pythia.model.histogram.Bin;
import gr.uoi.cs.pythia.model.outlier.OutlierResult;
import gr.uoi.cs.pythia.util.HighlightParameters;
import gr.uoi.cs.pythia.util.HighlightParameters.HighlightExtractionMode;




public class HighlightsManagerV01 implements HighlightsManagerInterface {
	
	private final Logger logger = Logger.getLogger(HighlightsManagerV01.class);
	private DatasetProfile datasetProfile;
	private List<Column> columns;
	private List<HolisticHighlight> holisticHighlights;
	//private HighlightReporterFactory highlightReporterFactory; 
	
	
	public HighlightsManagerV01(DatasetProfile datasetProfile) {
		this.datasetProfile = datasetProfile;
		this.columns = this.datasetProfile.getColumns();
		this.holisticHighlights = new ArrayList<HolisticHighlight>();
		//this.highlightReporterFactory = new HighlightReporterFactory();
	}
	
	@Override
	public List<HolisticHighlight> extractHighlightsForStorytelling(HighlightParameters highlightParameters, 
			boolean descriptiveStats, boolean histograms,
			boolean allPairsCorrelations, boolean decisionTrees, 
			boolean dominancePatterns,
			boolean outlierDetection) {
		
		List<HolisticHighlight> descriptiveStatsHHs = null;
		List<HolisticHighlight> histogramsHHs = null;
		List<HolisticHighlight> correlationsHHs = null;
		List<HolisticHighlight> decisionTreesHHs= null;
		List<HolisticHighlight> outliersHHs= null;
//		System.out.println(" &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& " + 
//				" descr: " +  descriptiveStats +  " histo: " +   histograms +
//				" corr: " +   allPairsCorrelations + " decTr: " +   decisionTrees +
//				" descr: " +   outlierDetection +
//				" dom: " + dominancePatterns	);

		if(descriptiveStats) 
			descriptiveStatsHHs = extractDescriptiveStatsHighlights();
		if(histograms) 
			histogramsHHs = extractHistogramHighlights(); 
		if(allPairsCorrelations) 
			correlationsHHs = extractCorrelationsHighlights();
		if(decisionTrees) 
			decisionTreesHHs = extractDecisionTreesHighlights();
		if(outlierDetection) { 
			outliersHHs = extractOutlierHighlights();
		}
		if(dominancePatterns)	//TODO fix 
			extractDominanceHighlights();

		//process the bloody highlights, select the ones that should be selected, and make a highlights profile
		List<HolisticHighlight> selectedHighlights = new ArrayList<HolisticHighlight>();
		HighlightsProfile highlightsProfile = selectHighlights(highlightParameters, 
				selectedHighlights,
				descriptiveStatsHHs, 
				histogramsHHs,
				correlationsHHs, 
				decisionTreesHHs,
				outliersHHs);
		this.datasetProfile.setHighlightsProfile(highlightsProfile);
		
		
		//now report
		reportHighlightsAsString();
		
		//return a defensive copy of the highlight list
		List<HolisticHighlight> holisticHighlightsDefenseCopy = new ArrayList<HolisticHighlight>(this.holisticHighlights);
		return holisticHighlightsDefenseCopy;
	}
	
	private HighlightsProfile selectHighlights(HighlightParameters highlightParameters, 
			List<HolisticHighlight> selectedHighlights,
			List<HolisticHighlight> descriptiveStatsHHs, 
			List<HolisticHighlight> histogramsHHs,
			List<HolisticHighlight> correlationsHHs, 
			List<HolisticHighlight> decisionTreesHHs,
			List<HolisticHighlight> outliersHHs) {
		HighlightsProfile highlightsProfile = new HighlightsProfile(
				descriptiveStatsHHs, 
				histogramsHHs,
				correlationsHHs, 
				decisionTreesHHs,
				outliersHHs, selectedHighlights); 
		if(HighlightExtractionMode.NONE == highlightParameters.getHighlightExtractionMode())
			return highlightsProfile;
		
		if(HighlightExtractionMode.ALL == highlightParameters.getHighlightExtractionMode()) {
			selectedHighlights.addAll(this.holisticHighlights);
		}
		if(HighlightExtractionMode.ABOVE_THRESHOLD == highlightParameters.getHighlightExtractionMode()) {
			double threshold = highlightParameters.getNumericLimit().doubleValue();
			for(HolisticHighlight hh: this.holisticHighlights) {
				if(hh.getScoreValue() > threshold)
					selectedHighlights.add(hh);
			}
		}
		if(HighlightExtractionMode.TOP == highlightParameters.getHighlightExtractionMode()) {
			//sort by score
			Collections.sort(this.holisticHighlights, new Comparator<HolisticHighlight>() {
				public int compare(HolisticHighlight o1, HolisticHighlight o2) {
					return o2.getScoreValue().compareTo(o1.getScoreValue());
				}
			});
			Collections.sort(this.holisticHighlights, Collections.reverseOrder());
			int limit = highlightParameters.getNumericLimit().intValue();
			selectedHighlights.addAll(this.holisticHighlights.subList(0,limit));
		}	    
	    
		return highlightsProfile;
	}
	
	private String reportHighlightsAsString() {
		
		String result = "";
		for (HolisticHighlight h: this.holisticHighlights) {
			String currentString = h.toString();
			logger.info(currentString);
			result += currentString;
		}
		return result;
	}

	private List<HolisticHighlight> extractDescriptiveStatsHighlights() {
		List<HolisticHighlight> descriptiveStatsHolisticHLs = new ArrayList<HolisticHighlight>();
		for(Column c: columns) {
			if(c.getDescriptiveStatisticsProfile() != null) {
				String columnName = c.getName();
				
				Double countValue = Double.MIN_VALUE;
				try{ 
					countValue = Double.parseDouble(c.getDescriptiveStatisticsProfile().getCount());
				}catch(NumberFormatException nfe) {
					;
				}
				catch(NullPointerException npe) {
					;
				}
				Double meanValue = Double.MIN_VALUE;
				try{ 
					meanValue = Double.parseDouble(c.getDescriptiveStatisticsProfile().getMean());
				}catch(NumberFormatException nfe) {
					;
				}
				catch(NullPointerException npe) {
					;
				}
				Double standDevValue  = Double.MIN_VALUE;
				try{
					standDevValue  = Double.parseDouble(c.getDescriptiveStatisticsProfile().getStandardDeviation());
				}catch(NumberFormatException nfe) {
					;
				}
				catch(NullPointerException npe) {
					;
				}
				Double medianValue  = Double.MIN_VALUE;
				try{
					medianValue = Double.parseDouble(c.getDescriptiveStatisticsProfile().getMedian());
				}catch(NumberFormatException nfe) {
					;
				}
				catch(NullPointerException npe) {
					;
				}
				Double minValue= Double.MIN_VALUE;
				try{ 
					minValue = Double.parseDouble(c.getDescriptiveStatisticsProfile().getMin());
				}catch(NumberFormatException nfe) {
					;
				}
				catch(NullPointerException npe) {
					;
				}
				Double maxValue = Double.MIN_VALUE;
				try{ 
					maxValue = Double.parseDouble(c.getDescriptiveStatisticsProfile().getMax());
				}catch(NumberFormatException nfe) {
					;
				}
				catch(NullPointerException npe) {
					;
				}	
				
				double descriptiveHighlightsScore = Double.NaN;
				
				HolisticHighlight hHighlight = new HolisticHighlight("Desciptive Statistics - Count of Values", columnName, "a values count", null, 
						"True", null, descriptiveHighlightsScore, null); 
						//countValue, null);
				holisticHighlights.add(hHighlight);
				descriptiveStatsHolisticHLs.add(hHighlight);
							

				hHighlight = new HolisticHighlight("Desciptive Statistics - Mean Value", columnName, "mean value calculation", null, 
						"True", null, descriptiveHighlightsScore, null); //meanValue, null);
				holisticHighlights.add(hHighlight);
				descriptiveStatsHolisticHLs.add(hHighlight);
				

				hHighlight = new HolisticHighlight("Desciptive Statistics - Standard Deviation", columnName, "standard deviation calculation", null, 
						"True", null, descriptiveHighlightsScore, null); // standDevValue, null);
				holisticHighlights.add(hHighlight);
				descriptiveStatsHolisticHLs.add(hHighlight);
				

				hHighlight = new HolisticHighlight("Desciptive Statistics - Median Value", columnName, "median value calculation", null, 
						"True", null, descriptiveHighlightsScore, null);  //medianValue, null);
				holisticHighlights.add(hHighlight);
				descriptiveStatsHolisticHLs.add(hHighlight);
				

				hHighlight = new HolisticHighlight("Desciptive Statistics - Minimum Value", columnName, "minimum value calculation", null, 
						"True", null, descriptiveHighlightsScore, null);  //minValue, null);
				holisticHighlights.add(hHighlight);
				descriptiveStatsHolisticHLs.add(hHighlight);
				

				hHighlight = new HolisticHighlight("Desciptive Statistics - Maximum Value", columnName, "maximum value calculation", null, 
						"True", null, descriptiveHighlightsScore, null);  //maxValue, null);
				holisticHighlights.add(hHighlight);
				descriptiveStatsHolisticHLs.add(hHighlight);
			}
		}
		
//		IHighlightsReporter  dStatsHLReporter = this.highlightReporterFactory.createHighlightReporter(HighlightReporterFactory.HighlightReporterType.DESCR_STATS); 
//		//DescriptiveStatsHighlightsReporter dStatsHLReporter = new DescriptiveStatsHighlightsReporter();
//		for(HolisticHighlight hHL: descriptiveStatsHolisticHLs) {
//			String descriptiveStatsHighlightsToString = dStatsHLReporter.getModelHighlightsString(hHL);
//			stringBuilder.append(String.format("%s\n", descriptiveStatsHighlightsToString));
//		}
		
		logger.info(String.format("Extracted the highlights for the descriptive stats of the dataset"));
		return descriptiveStatsHolisticHLs;
	}
		
	private List<HolisticHighlight> extractHistogramHighlights() {
		List<HolisticHighlight> histogramHolisticHLs = new ArrayList<HolisticHighlight>();
		for(Column c: columns) {
			if(c.getHistogram() != null) {
				String columnName = c.getName();
				List<ElementaryHighlight> eHighlights = new ArrayList<ElementaryHighlight>();
				
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
				
				double columnHistoScore = c.getHistogram().getHistoSkewness(); //Double.MIN_VALUE; 
				
				HolisticHighlight hHighlight = new HolisticHighlight("Histogram", columnName, "a histogram constructor", null, 
						"True", "Histogram Non-Uniformity", columnHistoScore, eHighlights);
				holisticHighlights.add(hHighlight);
				histogramHolisticHLs.add(hHighlight);
			}
		}
//		IHighlightsReporter  histogramHLReporter = this.highlightReporterFactory.createHighlightReporter(HighlightReporterFactory.HighlightReporterType.HISTO);
//		//HistogramsHighlightsReporter histogramHLReporter = new HistogramsHighlightsReporter();
//		for(HolisticHighlight hHL: histogramHolisticHLs) {
//			String histogramHighlightsToString = histogramHLReporter.getModelHighlightsString(hHL);
//			stringBuilder.append(String.format("%s\n", histogramHighlightsToString));
//		}
		
		logger.info(String.format("Extracted the highlights for the histograms of the dataset"));
		return histogramHolisticHLs;
	}
	
	private List<HolisticHighlight> extractCorrelationsHighlights() {
		List<HolisticHighlight> localCorrelationsHHs = new ArrayList<HolisticHighlight>();
		for(Column c: columns) {
			if(c.getCorrelationsProfile() != null) {
				String columnName = c.getName();
				for (Map.Entry<String, Double> entry: c.getCorrelationsProfile().getAllCorrelations().entrySet()) {
					String supportingRole = entry.getKey();
					Double scoreValue = entry.getValue();
					String resultingModel = "Significantly Low";
					if(Math.abs(scoreValue) >= 0.2 && Math.abs(scoreValue) < 0.4) {
						resultingModel = "Low";
					} else if(Math.abs(scoreValue) >= 0.4 && Math.abs(scoreValue) < 0.6) {
						resultingModel = "High";
					} else if (Math.abs(scoreValue) >= 0.6) {
						resultingModel = "Significantly High";
					}
					HolisticHighlight hHighlight = new HolisticHighlight("Correlation", columnName, "Pearson algorithm", supportingRole,
							resultingModel, "r", scoreValue, null );
					holisticHighlights.add(hHighlight);
					localCorrelationsHHs.add(hHighlight);
					
//					IHighlightsReporter correlationsHighlightsReporter = this.highlightReporterFactory.createHighlightReporter(HighlightReporterFactory.HighlightReporterType.CORREL);
//					//CorrelationsHighlightsReporter correlationsHighlightsReporter = new CorrelationsHighlightsReporter();
//					String correlationHighlightToString = correlationsHighlightsReporter.getModelHighlightsString(hHighlight);
//					stringBuilder.append(String.format("%s\n", correlationHighlightToString));
				}
			}
		}
		logger.info(String.format("Extracted the highlights for the correlations of the dataset"));
		return localCorrelationsHHs;
	}
	
	private List<HolisticHighlight> extractDecisionTreesHighlights() {
		List<HolisticHighlight> decisionTreesHHs = new ArrayList<HolisticHighlight>();
		for(Column c: columns) {
			if(c instanceof LabeledColumn) {
				String columnName = c.getName();
				List<DecisionTree> decisionTrees = ((LabeledColumn)c).getDecisionTrees();
				
				for(int i=0; i<decisionTrees.size(); i++) {
					String supportingRole = String.join(", ", decisionTrees.get(i).getFeatureColumnNames());
					
					HolisticHighlight hHighlight = new HolisticHighlight("Decision Tree", columnName, "decision tree construction algorithm",
							supportingRole, "True", "Average Impurity", decisionTrees.get(i).getAverageImpurity(), null);
					hHighlight.setSupportingText(" with feature columns: ");
					decisionTreesHHs.add(hHighlight);
					holisticHighlights.add(hHighlight);
					
//					IHighlightsReporter dTHLReporter = this.highlightReporterFactory.createHighlightReporter(HighlightReporterFactory.HighlightReporterType.DEC_TREE);
//					//DecisionTreesHighlightsReporter dTHLReporter = new DecisionTreesHighlightsReporter();
//					String dTHLToString = dTHLReporter.getModelHighlightsString(hHighlight);
//					stringBuilder.append(String.format("%s\n", dTHLToString));
				}
			}
		}
		logger.info(String.format("Extracted the highlights for the decision trees of the dataset"));
		return decisionTreesHHs;
	}
		
	private void extractDominanceHighlights() {
		//logger.info(String.format("Extracted the highlights for the dominance patterns of the dataset"));
	}
	
	private List<HolisticHighlight> extractOutlierHighlights() {
		List<HolisticHighlight> outlierHolisticHLs = new ArrayList<HolisticHighlight>();
		List<OutlierResult>  outliersResults = datasetProfile.getPatternsProfile().getOutlierResults();
		
		System.out.println(" &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& " + outliersResults.size());
		
		for(OutlierResult outlierRes: outliersResults) {
			String columnName = outlierRes.getColumnName();
			
			HolisticHighlight hHighlight = new HolisticHighlight("Outlier", columnName, "a "+ datasetProfile.getPatternsProfile().getOutlierType() +" calculation algorithm",
					null, "True", datasetProfile.getPatternsProfile().getOutlierType(), outlierRes.getScore(), null);
			hHighlight.setSupportingText(" with value " + outlierRes.getValue());
			holisticHighlights.add(hHighlight);
			outlierHolisticHLs.add(hHighlight);
		}
		
//		IHighlightsReporter outlierHLReporter = this.highlightReporterFactory.createHighlightReporter(HighlightReporterFactory.HighlightReporterType.OUTLIER);
////		OutlierHighlightsReporter outlierHLReporter = new OutlierHighlightsReporter();
//		for(HolisticHighlight hHL: outlierHolisticHLs) {
//			String outlierHighlightsToString = outlierHLReporter.getModelHighlightsString(hHL);
//			stringBuilder.append(String.format("%s\n", outlierHighlightsToString));
//		}

		logger.info(String.format("Extracted the highlights for the outliers of the dataset"));
		return outlierHolisticHLs;
	}


}//end class
