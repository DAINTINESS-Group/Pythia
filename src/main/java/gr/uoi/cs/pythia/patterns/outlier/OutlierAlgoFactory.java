package gr.uoi.cs.pythia.patterns.outlier;

import gr.uoi.cs.pythia.model.outlier.OutlierType;

public class OutlierAlgoFactory {

	  public IOutlierAlgo createOutlierAlgo(OutlierType type, double outlierThreshold) {
		    switch (type) {
		      case Z_SCORE:
		        return new ZScoreOutlierAlgo(outlierThreshold);
		      case NORMALIZED_SCORE:
		      	return new NormalizedScoreOutlierAlgo(outlierThreshold);
		    }
		    throw new IllegalArgumentException(
		        String.format("Outlier %s is not a supported outlier type.", type));
		  }
}
