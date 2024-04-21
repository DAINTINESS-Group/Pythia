package gr.uoi.cs.pythia.GenInfo;

import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    HappyGenInfoCalculationsTests.class,
        RainyGenInfoCalculationsTests.class,
        HappyInfoManagerTests.class,
        RainyManagerTests.class,
})
public class AllGenInfoTests {

    @ClassRule
    public static GenInfoResource genInfoResource = new GenInfoResource();

}

