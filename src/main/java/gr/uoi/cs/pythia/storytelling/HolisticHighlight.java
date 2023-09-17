package gr.uoi.cs.pythia.storytelling;

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
	
		
	//////////////////////////////////////////
	//Definitions based on the text and graphs
	
	//String mainMeasure -> The role of a measure type of the holistic highlight
	//Explanator -> one of the possible features of the dataset
	//List<ElementaryHighlight>
	
	//common start here
	
	
	//HighlightType -> Correlation/Dec. Tree/ HighlightPattern / Histograms / Descriptive Stats
	//HighlightModelType -> if i do correlation, then HighlightModelType is a list that includes Significant, Insignificant, Negatively Significant etc
	//Model -> one value of the HighlightModelType list
	//HighlightExtractionAlgorithm -> px Pearson/Kendall/Spearman for correlation
	//ScoreType -> a list with all possible scores for the highlight extraction algorithm
	//Score -> one score

}
