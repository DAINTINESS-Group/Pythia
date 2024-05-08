package gr.uoi.cs.pythia.cardinalities;


import gr.uoi.cs.pythia.generalinfo.GenInfoResource;

import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        SparkCardinalitiesCalculatorHappyTests.class,
        SparkCardinalitiesCalculatorRainyTests.class,
})

public class AllCardinalitiesTests {
}
