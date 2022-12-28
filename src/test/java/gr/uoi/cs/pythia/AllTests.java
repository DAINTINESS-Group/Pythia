package gr.uoi.cs.pythia;

import gr.uoi.cs.pythia.decisiontree.AllDecisionTreeTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        UtilTests.class,
        LabelingSystemTests.class,
        AllDecisionTreeTests.class,
        ReportSystemTests.class
})
public class AllTests {}
