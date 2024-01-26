package gr.uoi.cs.pythia.regression;

import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({ 
	LinearRegressionPerformerTests.class,
	MultipleLinearRegressionPerformerTests.class,
	AutomatedRegressionPerformerTests.class,
	PolynomialRegressionPerformerTests.class
})
public class AllRegressionTests {
	
	@ClassRule
    public static RegressionResource regressionResource = new RegressionResource();
	
}
