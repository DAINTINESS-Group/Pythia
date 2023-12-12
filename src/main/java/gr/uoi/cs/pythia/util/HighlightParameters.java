package gr.uoi.cs.pythia.util;

public class HighlightParameters {
	public enum HighlightExtractionMode{
		NONE,
		ALL,
		TOP,
		ABOVE_THRESHOLD
		//ADAPTIVE
	}
	
	private final HighlightExtractionMode highlightExtractionMode;
	private final Double numericLimit;
	
	public HighlightParameters(HighlightExtractionMode highlightExtractionMode, Double numericLimit) throws IllegalArgumentException{
		this.highlightExtractionMode = highlightExtractionMode;	
		if(null == highlightExtractionMode)
			throw new IllegalArgumentException("highlightExtractionMode cannot be null");
		switch(this.highlightExtractionMode) {
			case NONE: 
				this.numericLimit = Double.MAX_VALUE;
				break;
			case ALL: 
				this.numericLimit = Double.MIN_VALUE;  //Not NaN: a NaN H/L score means "don't put me in, no matter what"
				break;
			default: 
				if(null == numericLimit)
					throw new IllegalArgumentException("the numericLimit for this highlight extraction mode cannot be null");
				else this.numericLimit = numericLimit;
		}
	}

	public HighlightExtractionMode getHighlightExtractionMode() {
		return highlightExtractionMode;
	}

	public Double getNumericLimit() {
		return numericLimit;
	}
	
	
}
