package gr.uoi.cs.pythia.highlights;

import gr.uoi.cs.pythia.model.DatasetProfile;

public class HighlightsManagerFactory {

	public enum HighlightManagerVersion{
		V00,V01 
	}
	
	public HighlightsManagerInterface generateHighlightsManager(HighlightManagerVersion version, DatasetProfile datasetProfile) {
		switch(version) {
			case V00: return new HighlightsManagerV00(datasetProfile);
			default:
				return new HighlightsManagerV01(datasetProfile);
		}
	}
}
