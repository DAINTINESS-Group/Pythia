package gr.uoi.cs.pythia.highlights;

import gr.uoi.cs.pythia.model.DatasetProfile;

public class HighlightsManagerFactory {

	public enum HighlightManagerVersion{
		V0a,V01 
	}
	
	public HighlightsManagerInterface generateHighlightsManager(HighlightManagerVersion version, DatasetProfile datasetProfile) {
		switch(version) {
			case V0a: return new HighlightsManagerV0a(datasetProfile);
			default:
				return new HighlightsManagerV01(datasetProfile);
		}
	}
}
