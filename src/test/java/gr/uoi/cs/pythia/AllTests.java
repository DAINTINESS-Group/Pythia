package gr.uoi.cs.pythia;

import gr.uoi.cs.pythia.GenInfo.AllGenInfoTests;
import gr.uoi.cs.pythia.clustering.AllClusteringTests;
import gr.uoi.cs.pythia.correlations.AllCorrelationsTests;
import gr.uoi.cs.pythia.decisiontree.AllDecisionTreeTests;
import gr.uoi.cs.pythia.highlights.AllHighlightsTests;
import gr.uoi.cs.pythia.histogram.AllHistogramTests;
import gr.uoi.cs.pythia.labeling.LabelingSystemTests;
import gr.uoi.cs.pythia.patterns.AllPatternTests;
import gr.uoi.cs.pythia.regression.AllRegressionTests;
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
        AllPatternTests.class,
        AllRegressionTests.class,
        AllReportTests.class,
        AllWriterTests.class,
        AllHighlightsTests.class,
        AllClusteringTests.class,
        AllGenInfoTests.class
})
public class AllTests {}
