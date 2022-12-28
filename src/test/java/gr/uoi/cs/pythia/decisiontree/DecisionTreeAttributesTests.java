package gr.uoi.cs.pythia.decisiontree;

import gr.uoi.cs.pythia.decisiontree.engine.DecisionTreeEngineFactory;
import gr.uoi.cs.pythia.decisiontree.input.DecisionTreeParams;
import gr.uoi.cs.pythia.decisiontree.model.DecisionTree;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class DecisionTreeAttributesTests {

    @Test
    public void testAccuracy() {
        DecisionTree decisionTree = AllDecisionTreeTests.dtResource.getDecisionTree(new ArrayList<>());
        assertTrue("Accuracy should be Nan, due to 0 testing data",
                Double.isNaN(decisionTree.getAccuracy()));
    }

    @Test
    public void testFeatureColumns() {
        String[] expectedFeatures = {"CompPrice", "Income", "Advertising",
                "Population", "Price", "Age", "Education",
                "ShelveLoc", "Urban", "US"};
        DecisionTree decisionTree = getDecisionTree(new ArrayList<>());
        assertArrayEquals(expectedFeatures, decisionTree.getFeatureColumnNames());
    }

    @Test
    public void testValidSelectedFeatureColumns() {
        String[] expectedFeatures = {"Income", "Price", "Age", "ShelveLoc"};
        DecisionTree decisionTree = getDecisionTree(Arrays.asList(expectedFeatures));
        assertArrayEquals(expectedFeatures, decisionTree.getFeatureColumnNames());
    }

    @Test
    public void testSomeNotValidSelectedFeatureColumns() {
        String[] someNotValidFeatures = {"Income", "Price", "Age", "ShelveLoc",
                "Sales_labeled", "Sales_labeled_indexed", "ShelveLoc_indexed"};
        DecisionTree decisionTree = getDecisionTree(Arrays.asList(someNotValidFeatures));
        String[] expectedFeatures = {"Income", "Price", "Age", "ShelveLoc"};
        assertArrayEquals(expectedFeatures, decisionTree.getFeatureColumnNames());
    }

    @Test
    public void testNullSelectedFeaturesList() {
        String[] expectedFeatures = {"CompPrice", "Income", "Advertising",
                "Population", "Price", "Age", "Education",
                "ShelveLoc", "Urban", "US"};
        DecisionTree decisionTree = getDecisionTree(null);
        assertArrayEquals(expectedFeatures, decisionTree.getFeatureColumnNames());
    }

    @Test
    public void testSomeNullElementsSelectedFeatureColumns() {
        String[] someNullSelectedFeatures = {"Income", null, "Age", null};
        DecisionTree decisionTree = getDecisionTree(Arrays.asList(someNullSelectedFeatures));
        String[] expectedFeatures = {"Income", "Age"};
        assertArrayEquals(expectedFeatures, decisionTree.getFeatureColumnNames());
    }

    @Test
    public void testNonGeneratorAttributesWithAllAsFeatures() {
        String[] expectedNonGeneratorAttributes = {"Sales"};
        DecisionTree decisionTree = getDecisionTree(new ArrayList<>());
        assertArrayEquals(expectedNonGeneratorAttributes, decisionTree.getNonGeneratorAttributes().toArray());
    }

    @Test
    public void testNonGeneratorAttributesWithSomeFeatures() {
        String[] expectedNonGeneratorAttributes = {"Sales", "Income", "Price", "ShelveLoc", "Age"};
        String[] selectedFeatures = {"CompPrice", "Advertising", "Population", "Education", "Urban", "US"};
        DecisionTree decisionTree = getDecisionTree(Arrays.asList(selectedFeatures));
        assertArrayEquals(expectedNonGeneratorAttributes, decisionTree.getNonGeneratorAttributes().toArray());
    }

    private DecisionTree getDecisionTree(List<String> selectedFeatures) {
        DecisionTreeParams decisionTreeParams = new DecisionTreeParams
                .Builder(AllDecisionTreeTests.dtResource.getRuleSet().getNewColumnName(),
                AllDecisionTreeTests.dtResource.getRuleSet().getTargetColumns())
                .selectedFeatures(selectedFeatures)
                .trainingToTestDataSplitRatio(new double[]{1, 0})
                .build();
        return new DecisionTreeEngineFactory(decisionTreeParams, AllDecisionTreeTests.dtResource.getDataset())
                .getDefaultEngine()
                .computeDecisionTree();
    }
}
