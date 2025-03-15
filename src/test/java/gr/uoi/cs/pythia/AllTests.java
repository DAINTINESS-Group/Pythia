package gr.uoi.cs.pythia;

import gr.uoi.cs.pythia.cardinalities.AllCardinalitiesTests;
import gr.uoi.cs.pythia.clustering.AllClusteringTests;
import gr.uoi.cs.pythia.correlations.AllCorrelationsTests;
import gr.uoi.cs.pythia.datatypeIdentifier.AllTypeStrategyTests;
import gr.uoi.cs.pythia.decisiontree.AllDecisionTreeTests;
import gr.uoi.cs.pythia.descriptiveStatistics.DescriptiveStatisticsTest;
import gr.uoi.cs.pythia.generalinfo.AllGenInfoTests;
import gr.uoi.cs.pythia.gui.analysisTasksGuiPanelTest.AllAnalysisTasksGuiPanelTests;
import gr.uoi.cs.pythia.gui.guiButtonPanelTest.AllButtonPanelTest;
import gr.uoi.cs.pythia.gui.guiScores.AllGuiScoreTests;
import gr.uoi.cs.pythia.gui.resultsGui.AllResultTests;
import gr.uoi.cs.pythia.highlights.AllHighlightsTests;
import gr.uoi.cs.pythia.histogram.AllHistogramTests;
import gr.uoi.cs.pythia.labeling.LabelingSystemTests;
import gr.uoi.cs.pythia.outliers.AllOutlierTests;
import gr.uoi.cs.pythia.patterns.AllPatternTests;
import gr.uoi.cs.pythia.regression.AllRegressionTests;
import gr.uoi.cs.pythia.report.AllReportTests;
import gr.uoi.cs.pythia.writer.AllWriterTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        AllCardinalitiesTests.class,
        AllClusteringTests.class,
        AllCorrelationsTests.class,
        AllDecisionTreeTests.class,
        AllGenInfoTests.class,
        AllHighlightsTests.class,
        AllHistogramTests.class,
        LabelingSystemTests.class,
        AllOutlierTests.class,
        AllPatternTests.class,
        AllRegressionTests.class,
        AllReportTests.class,
        AllWriterTests.class,
        DescriptiveStatisticsTest.class,
        /*AllTypeStrategyTests.class,
        AllAnalysisTasksGuiPanelTests.class,
        AllButtonPanelTest.class,
        AllGuiScoreTests.class,
        AllResultTests.class*/
})
public class AllTests{
}
