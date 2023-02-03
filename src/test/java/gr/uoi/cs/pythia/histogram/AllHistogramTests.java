package gr.uoi.cs.pythia.histogram;

import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        HistogramTests.class
})
public class AllHistogramTests {

    @ClassRule
    public static HistogramResource histogramResource = new HistogramResource();
}
