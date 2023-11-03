package gr.uoi.cs.pythia.patterns;

import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	DominanceColumnSelectorTests.class, 
	DominanceAlgoTests.class,
	ZScoreOutlierAlgoTests.class,
	NormalizedScoreOutlierAlgoTests.class
})
public class AllPatternTests {

    @ClassRule
    public static PatternsResource patternsResource = new PatternsResource();
    
}
  