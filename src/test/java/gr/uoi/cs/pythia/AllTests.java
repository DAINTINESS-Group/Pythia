package gr.uoi.cs.pythia;

import gr.uoi.cs.pythia.correlations.AllCorrelationsTests;
import gr.uoi.cs.pythia.decisiontree.AllDecisionTreeTests;
import gr.uoi.cs.pythia.histogram.AllHistogramTests;
import gr.uoi.cs.pythia.report.AllReportTests;
import gr.uoi.cs.pythia.writer.AllWriterTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        LabelingSystemTests.class,
        AllHistogramTests.class,
        AllCorrelationsTests.class,
        AllDecisionTreeTests.class,
        AllReportTests.class,
        AllWriterTests.class
})
public class AllTests {}
