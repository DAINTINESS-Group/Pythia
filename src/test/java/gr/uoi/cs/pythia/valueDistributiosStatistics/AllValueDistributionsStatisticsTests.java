package gr.uoi.cs.pythia.valueDistributiosStatistics;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
        SparkValueDistributionsCalculatorHappyScenarios.class,
        SparkValueDistributionsCalculatorRainyScenarios.class,
        SortingHappyTests.class,
        SortingRainyTests.class,
        HistogramTest.class
})
public class AllValueDistributionsStatisticsTests{

}
