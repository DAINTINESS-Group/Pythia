package gr.uoi.cs.pythia;

import gr.uoi.cs.pythia.config.SparkConfig;
import gr.uoi.cs.pythia.decisiontree.dataprepatarion.DecisionTreeParams;
import gr.uoi.cs.pythia.decisiontree.engine.DecisionTreeEngineFactory;
import gr.uoi.cs.pythia.decisiontree.model.DecisionTree;
import gr.uoi.cs.pythia.decisiontree.model.node.DecisionTreeNode;
import gr.uoi.cs.pythia.decisiontree.model.node.FeatureType;
import gr.uoi.cs.pythia.engine.IDatasetProfiler;
import gr.uoi.cs.pythia.engine.IDatasetProfilerFactory;
import gr.uoi.cs.pythia.labeling.LabelingSystemConstants;
import gr.uoi.cs.pythia.labeling.Rule;
import gr.uoi.cs.pythia.labeling.RuleSet;
import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.apache.spark.sql.functions.expr;
import static org.junit.Assert.*;


public class DecisionTreeTests {

    private static SparkSession sparkSession;
    private RuleSet ruleSet;
    private Dataset<Row> dataset;

    @Before
    public void init() throws AnalysisException, IllegalAccessException {
        SparkConfig sparkConfig = new SparkConfig();
        sparkSession =
                SparkSession.builder()
                        .appName(sparkConfig.getAppName())
                        .master(sparkConfig.getMaster())
                        .config("spark.sql.warehouse.dir", sparkConfig.getSparkWarehouse())
                        .getOrCreate();

        initializeProfile();
    }

    private void initializeProfile() throws AnalysisException, IllegalAccessException {
        StructType schema =
                new StructType(
                        new StructField[] {
                                new StructField("Sales", DataTypes.DoubleType, false, Metadata.empty()),
                                new StructField("CompPrice", DataTypes.IntegerType, false, Metadata.empty()),
                                new StructField("Income", DataTypes.IntegerType, false, Metadata.empty()),
                                new StructField("Advertising", DataTypes.IntegerType, false, Metadata.empty()),
                                new StructField("Population", DataTypes.IntegerType, false, Metadata.empty()),
                                new StructField("Price", DataTypes.IntegerType, false, Metadata.empty()),
                                new StructField("ShelveLoc", DataTypes.StringType, false, Metadata.empty()),
                                new StructField("Age", DataTypes.IntegerType, false, Metadata.empty()),
                                new StructField("Education", DataTypes.IntegerType, false, Metadata.empty()),
                                new StructField("Urban", DataTypes.StringType, false, Metadata.empty()),
                                new StructField("US", DataTypes.StringType, false, Metadata.empty())
                        });
        IDatasetProfiler datasetProfiler = new IDatasetProfilerFactory().createDatasetProfiler();
        datasetProfiler.registerDataset("carseats",
                new File(
                        Objects.requireNonNull(getClass().getClassLoader().getResource("carseats.csv"))
                                .getFile()).getAbsolutePath(),
                schema);
        // Get dataset
        Field datasetField = FieldUtils.getField(datasetProfiler.getClass(), "dataset", true);
        dataset = (Dataset<Row>) datasetField.get(datasetProfiler);
        // Get rules
        List<Rule> rules = new ArrayList<>();
        rules.add(new Rule("Sales", LabelingSystemConstants.LEQ, 3, "low"));
        rules.add(new Rule("Sales", LabelingSystemConstants.LEQ, 9, "mid"));
        rules.add(new Rule("Sales", LabelingSystemConstants.GT, 9, "high"));
        ruleSet = new RuleSet("Sales_labeled", rules);
        // Add new labeledColumn
        String labelingRulesAsExpression = ruleSet.generateSparkSqlExpression();
        dataset = dataset.withColumn(ruleSet.getNewColumnName(), expr(labelingRulesAsExpression));
    }

    @Test
    public void testAccuracy() {
        DecisionTree decisionTree = getDecisionTree(new ArrayList<>());
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
    public void testDecisionTreeRootNode() {
        DecisionTreeNode rootNode = getDecisionTreeForNodeTesting().getRootNode();
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
        DecisionTreeNode leftNode = getDecisionTreeForNodeTesting().getRootNode().getLeftNode();

        assertFalse(leftNode.isLeaf());
        assertEquals(leftNode.getSplit().getFeature(), "Price");
        assertEquals(leftNode.getSplit().getFeatureType(), FeatureType.CONTINUOUS);
        assertEquals(leftNode.getSplit().getThreshold(), 89.5, 0);
        assertEquals(leftNode.getStats().getImpurity(), 0.37149911816578485, 0.01);
        assertEquals(leftNode.getPredict().getPrediction(), "mid");
    }

    @Test
    public void testDecisionTreeRightNode() {
        DecisionTreeNode rightNode = getDecisionTreeForNodeTesting().getRootNode().getRightNode();

        assertFalse(rightNode.isLeaf());
        assertEquals(rightNode.getSplit().getFeature(), "Price");
        assertEquals(rightNode.getSplit().getFeatureType(), FeatureType.CONTINUOUS);
        assertEquals(rightNode.getSplit().getThreshold(), 143.5, 0);
        assertEquals(rightNode.getStats().getImpurity(), 0.43349480968858134, 0.01);
        assertEquals(rightNode.getPredict().getPrediction(), "high");
    }

    @Test
    public void testDecisionTreeLeftMostLeafNode() {
        DecisionTreeNode leftMostLeaf = getDecisionTreeForNodeTesting().getRootNode()
                .getLeftNode()
                .getLeftNode();

        assertTrue(leftMostLeaf.isLeaf());
        assertEquals(leftMostLeaf.getPredict().getPrediction(), "high");
    }

    @Test
    public void testDecisionTreeRightMostLeafNode() {
        DecisionTreeNode rightMostLeaf = getDecisionTreeForNodeTesting().getRootNode()
                .getRightNode()
                .getRightNode();

        assertTrue(rightMostLeaf.isLeaf());
        assertEquals(rightMostLeaf.getPredict().getPrediction(), "mid");
    }

    private DecisionTree getDecisionTree(List<String> selectedFeatures) {
        DecisionTreeParams decisionTreeParams = new DecisionTreeParams.Builder(ruleSet)
                .selectedFeatures(selectedFeatures)
                .trainingToTestDataSplitRatio(new double[]{1, 0})
                .build();
        return new DecisionTreeEngineFactory(decisionTreeParams, dataset)
                .getDefaultEngine()
                .getDecisionTree();
    }

    private DecisionTree getDecisionTreeForNodeTesting() {
        String[] selectedFeatures = {"Income", "Price", "Age", "ShelveLoc"};
        DecisionTreeParams decisionTreeParams = new DecisionTreeParams.Builder(ruleSet)
                .selectedFeatures(Arrays.asList(selectedFeatures))
                .trainingToTestDataSplitRatio(new double[]{1, 0})
                .maxDepth(2)
                .build();
        return new DecisionTreeEngineFactory(decisionTreeParams, dataset)
                .getDefaultEngine()
                .getDecisionTree();
    }

    @AfterClass
    public static void closeSparkSession() {
        sparkSession.stop();
    }
}