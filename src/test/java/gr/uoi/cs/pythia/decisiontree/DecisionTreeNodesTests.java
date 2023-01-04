package gr.uoi.cs.pythia.decisiontree;

import gr.uoi.cs.pythia.decisiontree.generator.DecisionTreeGeneratorFactory;
import gr.uoi.cs.pythia.decisiontree.input.DecisionTreeParams;
import gr.uoi.cs.pythia.decisiontree.model.DecisionTree;
import gr.uoi.cs.pythia.decisiontree.model.node.DecisionTreeNode;
import gr.uoi.cs.pythia.decisiontree.model.node.FeatureType;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class DecisionTreeNodesTests {

    private static DecisionTree decisionTree;

    @BeforeClass
    public static void initializeDecisionTree() {
        String[] selectedFeatures = {"Income", "Price", "Age", "ShelveLoc"};
        DecisionTreeParams decisionTreeParams = new DecisionTreeParams
                .Builder(AllDecisionTreeTests.dtResource.getRuleSet().getNewColumnName(),
                AllDecisionTreeTests.dtResource.getRuleSet().getTargetColumns())
                .selectedFeatures(Arrays.asList(selectedFeatures))
                .trainingToTestDataSplitRatio(new double[]{1, 0})
                .maxDepth(2)
                .build();
        decisionTree = new DecisionTreeGeneratorFactory(decisionTreeParams,
                AllDecisionTreeTests.dtResource.getDataset())
                .getDefaultGenerator()
                .computeDecisionTree();
    }

    @Test
    public void testDecisionTreeRootNode() {
        DecisionTreeNode rootNode = decisionTree.getRootNode();
        String[] expectedCategoriesRootNode = {"Medium", "Bad"};

        assertFalse(rootNode.isLeaf());
        assertEquals(rootNode.getSplit().getFeature(), "ShelveLoc");
        assertEquals(rootNode.getSplit().getFeatureType(), FeatureType.CATEGORICAL);
        assertEquals(rootNode.getSplit().getCategories(), Arrays.asList(expectedCategoriesRootNode));
        assertEquals(rootNode.getStats().getImpurity(), 0.46276249999999997, 0.01);
        assertEquals(rootNode.getPredict().getPrediction(), "mid");
    }

    @Test
    public void testDecisionTreeLeftNode() {
        DecisionTreeNode leftNode = decisionTree.getRootNode().getLeftNode();

        assertFalse(leftNode.isLeaf());
        assertEquals(leftNode.getSplit().getFeature(), "Price");
        assertEquals(leftNode.getSplit().getFeatureType(), FeatureType.CONTINUOUS);
        assertEquals(leftNode.getSplit().getThreshold(), 89.5, 0);
        assertEquals(leftNode.getStats().getImpurity(), 0.37149911816578485, 0.01);
        assertEquals(leftNode.getPredict().getPrediction(), "mid");
    }

    @Test
    public void testDecisionTreeRightNode() {
        DecisionTreeNode rightNode = decisionTree.getRootNode().getRightNode();

        assertFalse(rightNode.isLeaf());
        assertEquals(rightNode.getSplit().getFeature(), "Price");
        assertEquals(rightNode.getSplit().getFeatureType(), FeatureType.CONTINUOUS);
        assertEquals(rightNode.getSplit().getThreshold(), 143.5, 0);
        assertEquals(rightNode.getStats().getImpurity(), 0.43349480968858134, 0.01);
        assertEquals(rightNode.getPredict().getPrediction(), "high");
    }

    @Test
    public void testDecisionTreeLeftMostLeafNode() {
        DecisionTreeNode leftMostLeaf = decisionTree.getRootNode()
                .getLeftNode()
                .getLeftNode();

        assertTrue(leftMostLeaf.isLeaf());
        assertEquals(leftMostLeaf.getPredict().getPrediction(), "high");
    }

    @Test
    public void testDecisionTreeRightMostLeafNode() {
        DecisionTreeNode rightMostLeaf = decisionTree.getRootNode()
                .getRightNode()
                .getRightNode();

        assertTrue(rightMostLeaf.isLeaf());
        assertEquals(rightMostLeaf.getPredict().getPrediction(), "mid");
    }
}
