package gr.uoi.cs.pythia.decisiontree;

import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        DecisionTreeOptimizerTests.class,
        DecisionTreeNodesTests.class,
        DecisionTreeAttributesTests.class,
        DecisionTreeVisualizationTests.class,
        DecisionTreePathsTests.class
})
public class AllDecisionTreeTests {

    @ClassRule
    public static DecisionTreeResource dtResource = new DecisionTreeResource();
}
