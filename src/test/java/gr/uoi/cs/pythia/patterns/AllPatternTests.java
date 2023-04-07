package gr.uoi.cs.pythia.patterns;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	DominanceColumnSelectorTests.class, 
	DominanceAlgoTests.class,
	ZScoreOutlierAlgoTests.class 
})
public class AllPatternTests {

}
