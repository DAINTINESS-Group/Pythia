package gr.uoi.cs.pythia.highlights.builders;

import java.util.ArrayList;
import java.util.List;

import gr.uoi.cs.pythia.highlights.dom.HolisticHighlight;
import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;

public class HighlightBuilderDescriptiveStats {
	private DatasetProfile datasetProfile;
	private List<Column> columns;

	public HighlightBuilderDescriptiveStats(DatasetProfile datasetProfile) {
		this.datasetProfile = datasetProfile;
		this.columns = this.datasetProfile.getColumns();
	}

	public List<HolisticHighlight> extractDescriptiveStatsHighlights() {
		
		List<HolisticHighlight> descriptiveStatsHolisticHLs = new ArrayList<HolisticHighlight>();
		for(Column c: columns) {
			if(c.getDescriptiveStatisticsProfile() != null) {
				String mainMeasure = c.getName();
				String supportingRole = null;
				String resultingModel = "True";
				String scoreTypePrefix = "ScoreOfDescrStats";

				/*
				Double countValue = Double.NaN;
				try{ 
					countValue = Double.parseDouble(c.getDescriptiveStatisticsProfile().getCount());
				}catch(NumberFormatException nfe) {
					;
				}
				catch(NullPointerException npe) {
					;
				}
				Double meanValue = Double.NaN;
				try{ 
					countValue = Double.parseDouble(c.getDescriptiveStatisticsProfile().getMean());
				}catch(NumberFormatException nfe) {
					;
				}
				catch(NullPointerException npe) {
					;
				}
				Double standDevValue  = Double.NaN;
				try{
					standDevValue  = Double.parseDouble(c.getDescriptiveStatisticsProfile().getStandardDeviation());
				}catch(NumberFormatException nfe) {
					;
				}
				catch(NullPointerException npe) {
					;
				}
				Double medianValue  = Double.NaN;
				try{
					medianValue = Double.parseDouble(c.getDescriptiveStatisticsProfile().getMedian());
				}catch(NumberFormatException nfe) {
					;
				}
				catch(NullPointerException npe) {
					;
				}
				Double minValue= Double.NaN;
				try{ 
					minValue = Double.parseDouble(c.getDescriptiveStatisticsProfile().getMin());
				}catch(NumberFormatException nfe) {
					;
				}
				catch(NullPointerException npe) {
					;
				}
				Double maxValue = Double.NaN;
				try{ 
					maxValue = Double.parseDouble(c.getDescriptiveStatisticsProfile().getMax());
				}catch(NumberFormatException nfe) {
					;
				}
				catch(NullPointerException npe) {
					;
				}	
			*/
				
			/*
			 	String highlightType, String mainMeasure, String highlightExtractionAlgorithm, String supportingRole,
		 		String resultingModel, String scoreType, 
		 		String scoreValue, List<ElementaryHighlight> elementaryHighlights)
			 */
				
				HolisticHighlight hHighlight = new HolisticHighlight("Desciptive Statistics - Count of Values", mainMeasure, "Algo count()", supportingRole, 
						resultingModel, scoreTypePrefix+"Count", //countValue, null);
						Double.NaN, null);
//				holisticHighlights.add(hHighlight);
				descriptiveStatsHolisticHLs.add(hHighlight);
	
				hHighlight = new HolisticHighlight("Desciptive Statistics - Mean Value", mainMeasure, "Algo mean()", supportingRole, 
						resultingModel, scoreTypePrefix+"Mean", //meanValue, null);
						Double.NaN, null);
//				holisticHighlights.add(hHighlight);
				descriptiveStatsHolisticHLs.add(hHighlight);
				
				hHighlight = new HolisticHighlight("Desciptive Statistics - Standard Deviation", mainMeasure, "Algo stddev()", supportingRole, 
						resultingModel, scoreTypePrefix+"Stddev", //standDevValue, null);
						Double.NaN, null);
//				holisticHighlights.add(hHighlight);
				descriptiveStatsHolisticHLs.add(hHighlight);
				
				hHighlight = new HolisticHighlight("Desciptive Statistics - Median Value", mainMeasure, "Algo median()", supportingRole, 
						resultingModel, scoreTypePrefix+"Median", //medianValue, null);
						Double.NaN, null);
//				holisticHighlights.add(hHighlight);
				descriptiveStatsHolisticHLs.add(hHighlight);
				
				hHighlight = new HolisticHighlight("Desciptive Statistics - Minimum Value", mainMeasure, "Algo min()", supportingRole, 
						resultingModel, scoreTypePrefix+"Min", //minValue, null);
						Double.NaN, null);
//				holisticHighlights.add(hHighlight);
				descriptiveStatsHolisticHLs.add(hHighlight);
				
				hHighlight = new HolisticHighlight("Desciptive Statistics - Maximum Value", mainMeasure, "Algo max()", supportingRole, 
						resultingModel, scoreTypePrefix+"Max", //maxValue, null);
						Double.NaN, null);
//				holisticHighlights.add(hHighlight);
				descriptiveStatsHolisticHLs.add(hHighlight);

			}
		}
		
		
		return descriptiveStatsHolisticHLs;
	}

}
