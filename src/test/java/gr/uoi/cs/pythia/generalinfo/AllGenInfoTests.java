package gr.uoi.cs.pythia.generalinfo;

import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    SparkBasicInfoCalculatorTestsHappy.class,
        SparkBasicInfoCalculatorTestsRainy.class,
})
public class AllGenInfoTests {

    @ClassRule
    public static GenInfoResource genInfoResource = new GenInfoResource();

}

