package gr.uoi.cs.pythia.correlations;

import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        PearsonCorrelationsTests.class
})

public class AllCorrelationsTests {

    @ClassRule
    public static CorrelationsResource correlationsResource = new CorrelationsResource();
}
