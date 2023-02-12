package gr.uoi.cs.pythia.decisiontree;

import gr.uoi.cs.pythia.decisiontree.generator.DecisionTreeGeneratorFactory;
import gr.uoi.cs.pythia.decisiontree.input.DecisionTreeParams;
import gr.uoi.cs.pythia.model.decisiontree.DecisionTree;
import gr.uoi.cs.pythia.model.decisiontree.DecisionTreePath;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DecisionTreePathsTests {
    private static DecisionTree decisionTree;

    @BeforeClass
    public static void initializeDecisionTree() {
        DecisionTreeParams decisionTreeParams = new DecisionTreeParams
                .Builder(AllDecisionTreeTests.dtResource.getRuleSet().getNewColumnName(),
                AllDecisionTreeTests.dtResource.getTargetColumns())
                .trainingToTestDataSplitRatio(new double[]{1, 0})
                .maxDepth(2)
                .build();
        decisionTree = new DecisionTreeGeneratorFactory(decisionTreeParams,
                AllDecisionTreeTests.dtResource.getDataset())
                .getDefaultGenerator()
                .computeDecisionTree();
    }

    @Test
    public void testDecisionTreePaths() {
        List<DecisionTreePath> paths = decisionTree.getPaths();

        List<String> expectedPath0 = Arrays.asList("ShelveLoc in (Medium, Bad)", "Price <= 89.5", "class: high");
        List<String> expectedPath1 = Arrays.asList("ShelveLoc in (Medium, Bad)", "Price > 89.5", "class: mid");
        List<String> expectedPath2 = Arrays.asList("ShelveLoc not in (Medium, Bad)", "Price <= 143.5", "class: high");
        List<String> expectedPath3 = Arrays.asList("ShelveLoc not in (Medium, Bad)", "Price > 143.5", "class: mid");

        assertEquals(expectedPath0, paths.get(0).getNodesAsString());
        assertEquals(expectedPath1, paths.get(1).getNodesAsString());
        assertEquals(expectedPath2, paths.get(2).getNodesAsString());
        assertEquals(expectedPath3, paths.get(3).getNodesAsString());
    }
}
