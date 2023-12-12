package gr.uoi.cs.pythia.highlights.builders;

import java.util.ArrayList;
import java.util.List;

import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.LabeledColumn;
import gr.uoi.cs.pythia.model.decisiontree.DecisionTree;
import gr.uoi.cs.pythia.model.highlights.HolisticHighlight;


public class HighlightBuilderDecisionTree {
	private DatasetProfile datasetProfile;
	private List<Column> columns;

	public HighlightBuilderDecisionTree(DatasetProfile datasetProfile) {
		this.datasetProfile = datasetProfile;
		this.columns = this.datasetProfile.getColumns();
	}

	public List<HolisticHighlight> extractHolisticHighlights() {
		
		List<HolisticHighlight> fullListHLs = new ArrayList<HolisticHighlight>();
		for(Column c: columns) {
			if(c instanceof LabeledColumn) {
				String columnName = c.getName();
				List<DecisionTree> decisionTrees = ((LabeledColumn)c).getDecisionTrees();
				
				for(int i=0; i<decisionTrees.size(); i++) {
					String supportingRole = String.join(", ", decisionTrees.get(i).getFeatureColumnNames());
					
					HolisticHighlight hHighlight = new HolisticHighlight("Decision Tree", columnName, "decision tree construction algorithm",
							supportingRole, "True", "Average Impurity", decisionTrees.get(i).getAverageImpurity(), null);
					hHighlight.setSupportingText(" with feature columns: ");
					fullListHLs.add(hHighlight);
				}
			}
		}
		
		
		return fullListHLs;
	}

}
