import gr.uoi.cs.pythia.datatypeIdentifier.AllTypeStrategyTests;
import gr.uoi.cs.pythia.gui.analysisTasksGuiPanelTest.AllAnalysisTasksGuiPanelTests;
import gr.uoi.cs.pythia.gui.guiButtonPanelTest.AllButtonPanelTest;
import gr.uoi.cs.pythia.gui.guiScores.AllGuiScoreTests;
import gr.uoi.cs.pythia.gui.resultsGui.AllResultTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        AllTypeStrategyTests.class,
        AllAnalysisTasksGuiPanelTests.class,
        AllButtonPanelTest.class,
        AllGuiScoreTests.class,
        AllResultTests.class

})
public class AllGuiTests{
}
