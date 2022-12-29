package gr.uoi.cs.pythia.patterns;

public class IPatternManagerFactory {
	public IPatternManager createPatternManager(
			ColumnSelectionMode columnSelectionMode, 
			String[] measurementColNames, 
			String[] coordinateColNames) {
		return new PatternManager(
				columnSelectionMode, 
				measurementColNames, 
				coordinateColNames);
	}
}
