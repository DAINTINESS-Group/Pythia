package gr.uoi.cs.pythia.patterns.outlier;

public class OutlierAlgoFactory {

	  public IOutlierAlgo createOutlierAlgo(OutlierType type) {
		    switch (type) {
		      case Z_SCORE:
		        return new ZScoreOutlierAlgo();
		    }
		    throw new IllegalArgumentException(
		        String.format("Outlier %s is not a supported outlier type.", type));
		  }
}
