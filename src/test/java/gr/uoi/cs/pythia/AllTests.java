package gr.uoi.cs.pythia;

import gr.uoi.cs.pythia.decisiontree.AllDecisionTreeTests;
import gr.uoi.cs.pythia.report.AllReportTests;
import gr.uoi.cs.pythia.writer.AllWriterTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        UtilTests.class,
        LabelingSystemTests.class,
        AllDecisionTreeTests.class,
        AllReportTests.class,
        AllWriterTests.class
})
public class AllTests {}
