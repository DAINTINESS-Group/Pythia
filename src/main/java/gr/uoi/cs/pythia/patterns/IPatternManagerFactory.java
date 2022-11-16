package gr.uoi.cs.pythia.patterns;

public class IPatternManagerFactory {
	public IPatternManager createPatternManager() {
		return new PatternManager();
	}
}
