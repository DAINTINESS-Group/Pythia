package gr.uoi.cs.pythia.highlights;

import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
// TODO add classes for highlights testing
})
public class AllPatternTests {

    @ClassRule
    public static HighlightsResource highlightsResource = new HighlightsResource();
    
}
  