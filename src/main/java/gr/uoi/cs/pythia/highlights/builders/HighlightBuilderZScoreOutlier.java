package gr.uoi.cs.pythia.highlights.builders;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.distribution.NormalDistribution;

import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.highlights.HolisticHighlight;
import gr.uoi.cs.pythia.model.outlier.OutlierResult;

public class HighlightBuilderZScoreOutlier {
	private DatasetProfile datasetProfile;


	public HighlightBuilderZScoreOutlier(DatasetProfile datasetProfile) {
		this.datasetProfile = datasetProfile;
	}

	/**
	 * Produces a list of HolisticHighlights for Z-score outliers
	 * 
	 * The key to normalize the z-scores in [0..1) is to use their survival score
	 * via the formula 
	 *     survScore = 1 - cumulativeProbability(abs(zscore))
	 * @see https://stackoverflow.com/questions/51772174/zscore-and-p-value-in-java-survival-function
	 *     
	 * @return a List of outlier highlights
	 */
	public List<HolisticHighlight> extractHolisticHighlights() {
		NormalDistribution nd = new NormalDistribution();

		List<HolisticHighlight> fullListHLs = new ArrayList<HolisticHighlight>();
		List<OutlierResult>  outliersResults = datasetProfile.getPatternsProfile().getOutlierResults();

		for(OutlierResult outlierRes: outliersResults) {
			String columnName = outlierRes.getColumnName();
			double zscore = outlierRes.getScore();
			double survivalScore = 1.0 - nd.cumulativeProbability(Math.abs(zscore)); 
			HolisticHighlight hHighlight = new HolisticHighlight("Outlier", columnName, "a "+ datasetProfile.getPatternsProfile().getOutlierType() +" calculation algorithm",
					null, "True", datasetProfile.getPatternsProfile().getOutlierType(),
					survivalScore, null);
			//					survivalFunction(outlierRes.getScore()), null);
			hHighlight.setSupportingText(" with value " + outlierRes.getValue());

			fullListHLs.add(hHighlight);
		}


		return fullListHLs;
	}

	// THe significance of an outlier should be 1 - pValue(abs(zScore))
	// to calculate the p-value:
	//    https://stackoverflow.com/questions/51772174/zscore-and-p-value-in-java-survival-function
	// where you use the survival function for p-value
	//
	//   Commented out for simple performance reasons, to avoid re-new-ing a normal distribution per value
	//
	//	private double survivalFunction(Double zscore) {
	//		NormalDistribution nd = new NormalDistribution();
	//		double sf = 1.0 - nd.cumulativeProbability(Math.abs(zscore));
	//		
	//		return sf;
	//	}

}//end class
