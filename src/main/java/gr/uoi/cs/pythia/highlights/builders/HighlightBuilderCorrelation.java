package gr.uoi.cs.pythia.highlights.builders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.highlights.HolisticHighlight;


public class HighlightBuilderCorrelation {
	private DatasetProfile datasetProfile;
	private List<Column> columns;

	public HighlightBuilderCorrelation(DatasetProfile datasetProfile) {
		this.datasetProfile = datasetProfile;
		this.columns = this.datasetProfile.getColumns();
	}

	public List<HolisticHighlight> extractHolisticHighlights() {
		
		List<HolisticHighlight> fullListHLs = new ArrayList<HolisticHighlight>();
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
					fullListHLs.add(hHighlight);	
				}
			}
		}
		
		
		return fullListHLs;
	}

}
