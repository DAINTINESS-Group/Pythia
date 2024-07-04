package gr.uoi.cs.pythia.outliers;

import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ZScoreOutlierAlgoTests.class,
        NormalizedScoreOutlierAlgoTests.class
})

public class AllOutlierTests {
    @ClassRule
    public static OutlierResource outlierResource = new OutlierResource();
}
