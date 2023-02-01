package gr.uoi.cs.pythia.decisiontree.generator;

import gr.uoi.cs.pythia.decisiontree.dataprepararion.DecisionTreeDataProcessor;
import gr.uoi.cs.pythia.decisiontree.input.DecisionTreeParams;
import gr.uoi.cs.pythia.decisiontree.dataprepararion.AttributesFinder;
import gr.uoi.cs.pythia.decisiontree.model.DecisionTree;
import gr.uoi.cs.pythia.decisiontree.model.node.DecisionTreeNode;
import gr.uoi.cs.pythia.decisiontree.model.node.DecisionTreeNodeParams;
import gr.uoi.cs.pythia.decisiontree.model.path.DecisionTreePathsFinder;
import org.apache.spark.ml.classification.DecisionTreeClassificationModel;
import org.apache.spark.ml.classification.DecisionTreeClassifier;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.ArrayList;
import java.util.List;

public class DecisionTreeGenerator implements IDecisionTreeGenerator {

    private final Dataset<Row> dataset;
    private final DecisionTreeParams decisionTreeParams;
    private AttributesFinder attributesFinder;
    private DecisionTreeDataProcessor decisionTreeDataProcessor;
    private DecisionTreeClassificationModel model;
    private double accuracy;

    public DecisionTreeGenerator(DecisionTreeParams decisionTreeParams, Dataset<Row> dataset) {
        this.decisionTreeParams = decisionTreeParams;
        this.dataset = dataset;
    }

    public DecisionTree computeDecisionTree() {
        attributesFinder = new AttributesFinder(decisionTreeParams, dataset);
        decisionTreeDataProcessor = new DecisionTreeDataProcessor(decisionTreeParams, attributesFinder, dataset);

        // Train a DecisionTree model.
        DecisionTreeClassifier decisionTreeClassifier = new DecisionTreeClassifier()
                .setLabelCol(decisionTreeParams.getLabeledColumnName() + "_indexed")
                .setImpurity(decisionTreeParams.getImpurity())
                .setMaxDepth(decisionTreeParams.getMaxDepth())
                .setMinInfoGain(decisionTreeParams.getMinInfoGain());

        model = decisionTreeClassifier.fit(decisionTreeDataProcessor.getTrainingData());
        Dataset<Row> predictions = model.transform(decisionTreeDataProcessor.getTestData());

        // Calculate accuracy
        MulticlassClassificationEvaluator evaluator =
                new MulticlassClassificationEvaluator()
                        .setLabelCol(decisionTreeParams.getLabeledColumnName() + "_indexed")
                        .setPredictionCol("prediction")
                        .setMetricName("accuracy");
        accuracy = evaluator.evaluate(predictions);

        return createDecisionTree();
    }

    private DecisionTree createDecisionTree() {
        DecisionTreeNode rootNode = getDecisionTreeRootNode();
        return new DecisionTree(
                accuracy,
                attributesFinder.getAllFeatures(),
                attributesFinder.getNonGeneratingAttributes(),
                getDecisionTreeRootNode(),
                calculateAverageImpurity(rootNode),
                new DecisionTreePathsFinder(rootNode).getPaths());
    }

    private DecisionTreeNode getDecisionTreeRootNode() {
        DecisionTreeNodeParams nodeParams = new DecisionTreeNodeParams(
                decisionTreeDataProcessor.getIndexedToActualValuesForEachIndexedColumn(),
                decisionTreeParams.getLabeledColumnName(),
                attributesFinder.getAllFeatures(),
                model.toOld().topNode());
        return new DecisionTreeNode(nodeParams);
    }

    private double calculateAverageImpurity(DecisionTreeNode rootNode) {
        List<Double> impurities = traverseNodesDFS(new ArrayList<>(), rootNode);
        double impuritySum = impurities.stream().mapToDouble(x -> x).sum();
        return impuritySum / impurities.size();
    }

    private List<Double> traverseNodesDFS(List<Double> impurities, DecisionTreeNode dtNode) {
        if (dtNode.isLeaf()) {
            impurities.add(dtNode.getImpurity());
            return impurities;
        }
        traverseNodesDFS(impurities, dtNode.getLeftNode());
        traverseNodesDFS(impurities, dtNode.getRightNode());
        return impurities;
    }
}
