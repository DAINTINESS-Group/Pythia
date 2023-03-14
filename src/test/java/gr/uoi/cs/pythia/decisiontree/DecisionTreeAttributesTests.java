package gr.uoi.cs.pythia.decisiontree;

import gr.uoi.cs.pythia.decisiontree.generator.DecisionTreeGeneratorFactory;
import gr.uoi.cs.pythia.decisiontree.input.DecisionTreeParams;
import gr.uoi.cs.pythia.model.decisiontree.DecisionTree;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
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
        List<String> expectedFeatures = Arrays.asList("CompPrice", "Income", "Advertising",
                "Population", "Price", "Age", "Education",
                "ShelveLoc", "Urban", "US");
        DecisionTree decisionTree = getDecisionTree(new ArrayList<>());
        assertEquals(expectedFeatures, decisionTree.getFeatureColumnNames());
    }

    @Test
    public void testValidSelectedFeatureColumns() {
        List<String> expectedFeatures = Arrays.asList("Income", "Price", "Age", "ShelveLoc");
        DecisionTree decisionTree = getDecisionTree(expectedFeatures);
        assertEquals(expectedFeatures, decisionTree.getFeatureColumnNames());
    }

    @Test
    public void testSomeNotValidSelectedFeatureColumns() {
        List<String> someNotValidFeatures = Arrays.asList("Income", "Price", "Age", "ShelveLoc",
                "Sales_labeled", "Sales_labeled_indexed", "ShelveLoc_indexed");
        DecisionTree decisionTree = getDecisionTree(someNotValidFeatures);
        List<String> expectedFeatures = Arrays.asList("Income", "Price", "Age", "ShelveLoc");
        assertEquals(expectedFeatures, decisionTree.getFeatureColumnNames());
    }

    @Test
    public void testNonExistingFeatures() {
        List<String> someNotValidFeatures = Arrays.asList("Test", "Price", "Test2", "ShelveLoc");
        DecisionTree decisionTree = getDecisionTree(someNotValidFeatures);
        List<String> expectedFeatures = Arrays.asList("Price", "ShelveLoc");
        assertEquals(expectedFeatures, decisionTree.getFeatureColumnNames());
    }

    @Test
    public void testNullSelectedFeaturesList() {
        List<String> expectedFeatures = Arrays.asList("CompPrice", "Income", "Advertising",
                "Population", "Price", "Age", "Education",
                "ShelveLoc", "Urban", "US");
        DecisionTree decisionTree = getDecisionTree(null);
        assertEquals(expectedFeatures, decisionTree.getFeatureColumnNames());
    }

    @Test
    public void testSomeNullElementsSelectedFeatureColumns() {
        List<String> someNullSelectedFeatures = Arrays.asList("Income", null, "Age", null);
        DecisionTree decisionTree = getDecisionTree(someNullSelectedFeatures);
        List<String> expectedFeatures = Arrays.asList("Income", "Age");
        assertEquals(expectedFeatures, decisionTree.getFeatureColumnNames());
    }

    @Test
    public void testNonGeneratorAttributesWithAllAsFeatures() {
        List<String> expectedNonGeneratorAttributes = Arrays.asList("Sales", "Sales_labeled");
        DecisionTree decisionTree = getDecisionTree(new ArrayList<>());
        assertEquals(expectedNonGeneratorAttributes, decisionTree.getNonGeneratorAttributes());
    }

    @Test
    public void testNonGeneratorAttributesWithSomeFeatures() {
        List<String> expectedNonGeneratorAttributes = Arrays.asList("Sales", "Income", "Price",
                "ShelveLoc", "Age", "Sales_labeled");
        List<String> selectedFeatures = Arrays.asList("CompPrice", "Advertising", "Population",
                "Education", "Urban", "US");
        DecisionTree decisionTree = getDecisionTree(selectedFeatures);
        assertEquals(expectedNonGeneratorAttributes, decisionTree.getNonGeneratorAttributes());
    }

    private DecisionTree getDecisionTree(List<String> selectedFeatures) {
        DecisionTreeParams decisionTreeParams = new DecisionTreeParams
                .Builder(AllDecisionTreeTests.dtResource.getRuleSet().getNewColumnName(),
                AllDecisionTreeTests.dtResource.getTargetColumns())
                .selectedFeatures(selectedFeatures)
                .trainingToTestDataSplitRatio(new double[]{1, 0})
                .build();
        return new DecisionTreeGeneratorFactory(decisionTreeParams, AllDecisionTreeTests.dtResource.getDataset())
                .getDefaultGenerator()
                .computeDecisionTree();
    }
}
