package gr.uoi.cs.pythia.storytelling;

import java.util.List;


public class HolisticHighlight {
	
	//Definitions based on the tables
	
	//HighlightType -> Correlation/Dec. Tree/ HighlightPattern / Histograms / Descriptive Stats
	//String mainMeasure -> The role of a measure type of the holistic highlight, i.e., the column name with the measure name
	//HighlightExtractionAlgorithm -> px Pearson/Kendall/Spearman for correlation
	//Suporting text, e.g., "wrt measure"
	//Supporting role, e.g., another measure with which our MainMeasure has a correlation
	//ResultingModel -> one value of the HighlightModelType list, e.g., if i do correlation, then one of Significant, Insignificant, Negatively Significant etc
	//ScoreType -> a list with all possible scores for the highlight extraction algorithm
	//Score -> one score
	//List<ElementaryHighlight>
	
	private String highlightType;
	private String mainMeasure;
	private String highlightExtractionAlgorithm;
	private String supportingText;
	private String supportingRole;
	private String resultingModel;
	private String scoreType; 
	private String scoreValue;
	private List<ElementaryHighlight> elementaryHighlights;
	
	public HolisticHighlight(String highlightType, String mainMeasure, String highlightExtractionAlgorithm, String supportingRole,
			 String resultingModel, String scoreType, String scoreValue, List<ElementaryHighlight> elementaryHighlights) {
		this.highlightType = highlightType;
		this.mainMeasure = mainMeasure;
		this.highlightExtractionAlgorithm = highlightExtractionAlgorithm;
		this.supportingRole = supportingRole;
		this.resultingModel = resultingModel;
		this.scoreType = scoreType;
		this.scoreValue = scoreValue;
		this.elementaryHighlights = elementaryHighlights;
		
	}
	
	public String getHighlightType() {
		return this.highlightType;
	}
	
	public String getMainMeasure() {
		return this.mainMeasure;
	}
		
	public String getHighlightExtractionAlgorithm() {
		return this.highlightExtractionAlgorithm;
	}
	
	public String getSupportingText() {
		return this.supportingText;
	}
	
	public String getSupportingRole() {
		return this.supportingRole;
	}
	
	public String getResultingModel() {
		return this.resultingModel;
	}
	
	public String getScoreType() {
		return this.scoreType;
	}
	
	public String getScoreValue() {
		return this.scoreValue;
	}
	
	public List<ElementaryHighlight> getElementaryHighlights(){
		return this.elementaryHighlights;
	}
	
	public void setSupportingText(String supportingText) {
		this.supportingText = supportingText;
	}
}
