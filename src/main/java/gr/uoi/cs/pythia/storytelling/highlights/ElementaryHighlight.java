package gr.uoi.cs.pythia.storytelling.highlights;

import java.util.List;


public class ElementaryHighlight {
	
	//private final Logger logger = Logger.getLogger(ElementaryHighlight.class);
	// A fact produced as a combination of characters and measure values that play an important role in a holistic highlight
	
	//Definitions based on the tables
	
	//CharactersSet -> the values of the characters, i.e., dimension levels, e.g., April for the level month
	//CharacterType -> one or more dimension levels, i.e., the coordinates of a fact that is a highlight. E.g., month
	//MeasureType -> the column with the measure name
	//MeasureValue -> the measure value
	//HighlightType -> Correlation/Dec. Tree/ HighlightPattern / Histograms / Descriptive Stats
	//ScoreType -> a description of the type of the score, e.g., Peak Rank
	//ScoreValue -> the actual score value
	//ExtraComments
	
	private List<String> characterSet;
	private List<String> characterTypes;
	private String measureType;
	private String measureValue;
	private String highlightType;
	private String scoreType;
	private String scoreValue;
	private String comments;
	
	
	public ElementaryHighlight(List<String> characterSet, List<String> characterTypes, String measureType, String measureValue,
								String highlightType, String scoreType, String scoreValue) {
		this.characterSet = characterSet;
		this.characterTypes = characterTypes;
		this.measureType = measureType;
		this.measureValue = measureValue;
		this.highlightType = highlightType;
		this.scoreType = scoreType;
		this.scoreValue = scoreValue;	
	}
	
	public List<String> getCharacterSet(){
		return this.characterSet;
	}
	
	public List<String> getCharacterTypes(){
		return this.characterTypes;
	}
	
	public String getMeasureType() {
		return this.measureType;
	}
	
	public String getMeasureValue() {
		return this.measureValue;
	}
	
	public String getHighlightType() {
		return this.highlightType;
	}
	
	public String getScoreType() {
		return this.scoreType;
	}
	
	public String getScoreValue() {
		return this.scoreValue;
	}
	
	public String getComments() {
		return this.comments;
	}
	
	public void setComments(String comments) {
		this.comments = comments;
	}
}
