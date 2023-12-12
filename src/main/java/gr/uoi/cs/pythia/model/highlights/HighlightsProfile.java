package gr.uoi.cs.pythia.model.highlights;

import java.util.ArrayList;
import java.util.List;

public class HighlightsProfile {
	private List<HolisticHighlight> descriptiveStatsHHs;
	private List<HolisticHighlight> histogramsHHs;
	private List<HolisticHighlight> correlationsHHs;
	private List<HolisticHighlight> decisionTreesHHs;
	private List<HolisticHighlight> outliersHHs;
	
	public HighlightsProfile(List<HolisticHighlight> descriptiveStatsHHs, List<HolisticHighlight> histogramsHHs,
			List<HolisticHighlight> correlationsHHs, List<HolisticHighlight> decisionTreesHHs,
			List<HolisticHighlight> outliersHHs) {
		
		if(null == descriptiveStatsHHs)
			this.descriptiveStatsHHs = new ArrayList<HolisticHighlight>();
		else
			this.descriptiveStatsHHs = descriptiveStatsHHs;
		if(null == histogramsHHs)
			this.histogramsHHs = new ArrayList<HolisticHighlight>();
		else
			this.histogramsHHs = histogramsHHs;
		if(null == correlationsHHs)
			this.correlationsHHs = new ArrayList<HolisticHighlight>();
		else
			this.correlationsHHs = correlationsHHs;
		if(null == decisionTreesHHs)
			this.decisionTreesHHs = new ArrayList<HolisticHighlight>();
		else
			this.decisionTreesHHs = decisionTreesHHs;
		if(null == outliersHHs)
			this.outliersHHs = new ArrayList<HolisticHighlight>();
		else
			this.outliersHHs = outliersHHs;
	}
	
	public List<HolisticHighlight> getDescriptiveStatsHHs() {
		return descriptiveStatsHHs;
	}
	public void setDescriptiveStatsHHs(List<HolisticHighlight> descriptiveStatsHHs) {
		this.descriptiveStatsHHs = descriptiveStatsHHs;
	}
	public List<HolisticHighlight> getHistogramsHHs() {
		return histogramsHHs;
	}
	public void setHistogramsHHs(List<HolisticHighlight> histogramsHHs) {
		this.histogramsHHs = histogramsHHs;
	}
	public List<HolisticHighlight> getCorrelationsHHs() {
		return correlationsHHs;
	}
	public void setCorrelationsHHs(List<HolisticHighlight> correlationsHHs) {
		this.correlationsHHs = correlationsHHs;
	}
	public List<HolisticHighlight> getDecisionTreesHHs() {
		return decisionTreesHHs;
	}
	public void setDecisionTreesHHs(List<HolisticHighlight> decisionTreesHHs) {
		this.decisionTreesHHs = decisionTreesHHs;
	}
	public List<HolisticHighlight> getOutliersHHs() {
		return outliersHHs;
	}
	public void setOutliersHHs(List<HolisticHighlight> outliersHHs) {
		this.outliersHHs = outliersHHs;
	}
	
	
}//end class
