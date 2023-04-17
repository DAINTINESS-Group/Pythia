package gr.uoi.cs.pythia.model;

import java.util.ArrayList;
import java.util.List;

import gr.uoi.cs.pythia.patterns.dominance.DominanceResult;
import gr.uoi.cs.pythia.patterns.outlier.OutlierResult;

public class PatternsProfile {

	private List<DominanceResult> highDominanceResults;
	private List<DominanceResult> lowDominanceResults;
	private List<OutlierResult> outlierResults;
	private String outlierType;
	
	public PatternsProfile() {
		this.highDominanceResults = new ArrayList<DominanceResult>();;
		this.lowDominanceResults = new ArrayList<DominanceResult>();;
		this.outlierResults = new ArrayList<OutlierResult>();
	}

	public List<DominanceResult> getHighDominanceResults() {
		return highDominanceResults;
	}

	public List<DominanceResult> getLowDominanceResults() {
		return lowDominanceResults;
	}

	public List<OutlierResult> getOutlierResults() {
		return outlierResults;
	}

	public String getOutlierType() {
		return outlierType;
	}

	public void setHighDominanceResults(List<DominanceResult> highDominanceResults) {
		this.highDominanceResults = highDominanceResults;
	}

	public void setLowDominanceResults(List<DominanceResult> lowDominanceResults) {
		this.lowDominanceResults = lowDominanceResults;
	}

	public void setOutlierResults(List<OutlierResult> outlierResults) {
		this.outlierResults = outlierResults;
	}

	public void setOutlierType(String outlierType) {
		this.outlierType = outlierType;
	}

	public String outlierResultsToString() {
		String str = String.format("## " + outlierType + " Outlier Pattern Results\n\n" +
				"Total outliers found: %s\n", outlierResults.size());
		for (OutlierResult result : outlierResults) {
			str += result.toString();
		}
		return str;
	}
	
	public String highDominanceResultsToString() {
		String str = String.format("## High Dominance Pattern Results\n");
		for (DominanceResult result : highDominanceResults) {
			str += result.toString();
		}
		return str;
	}
	
	public String lowDominanceResultsToString() {
		String str = String.format("## Low Dominance Pattern Results\n");
		for (DominanceResult result : lowDominanceResults) {
			str += result.toString();
		}
		return str;
	}
	
}
