package gr.uoi.cs.pythia.decisiontree;

import gr.uoi.cs.pythia.decisiontree.dataprepatarion.DecisionTreeDataProcessor;
import gr.uoi.cs.pythia.decisiontree.dataprepatarion.DecisionTreeParams;
import gr.uoi.cs.pythia.decisiontree.dataprepatarion.FeaturesFinder;
import gr.uoi.cs.pythia.decisiontree.model.DecisionTree;
import gr.uoi.cs.pythia.decisiontree.model.node.DecisionTreeNode;
import gr.uoi.cs.pythia.decisiontree.model.node.DecisionTreeNodeParams;
import org.apache.spark.ml.classification.DecisionTreeClassificationModel;
import org.apache.spark.ml.classification.DecisionTreeClassifier;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class DecisionTreeGenerator {

    private final DecisionTree decisionTree;

    private final DecisionTreeParams decisionTreeParams;

    public DecisionTreeGenerator(DecisionTreeParams decisionTreeParams) {
        this.decisionTreeParams = decisionTreeParams;
        this.decisionTree = new DecisionTree();
        computeDecisionTree();
    }

    public DecisionTree getDecisionTree() {
        return decisionTree;
    }

    private void computeDecisionTree() {
        FeaturesFinder featuresFinder = new FeaturesFinder(decisionTreeParams);
        DecisionTreeDataProcessor decisionTreeDataProcessor =
                new DecisionTreeDataProcessor(decisionTreeParams, featuresFinder);

        // Train a DecisionTree model.
        DecisionTreeClassifier decisionTreeClassifier = decisionTreeParams
                .getDecisionTreeClassifier()
                .setLabelCol(decisionTreeParams.getLabeledColumnName() + "_indexed");

        DecisionTreeClassificationModel model = decisionTreeClassifier
                .fit(decisionTreeDataProcessor.getTrainingData());
        Dataset<Row> predictions = model
                .transform(decisionTreeDataProcessor.getTestData());

        MulticlassClassificationEvaluator evaluator =
                new MulticlassClassificationEvaluator()
                        .setLabelCol(decisionTreeParams.getLabeledColumnName() + "_indexed")
                        .setPredictionCol("prediction")
                        .setMetricName("accuracy");
        double accuracy = evaluator.evaluate(predictions);

        // Get node parameters
        DecisionTreeNodeParams nodeParams = new DecisionTreeNodeParams(
                decisionTreeDataProcessor.getIndexedToActualValuesForEachIndexedColumn(),
                decisionTreeParams.getLabeledColumnName(),
                featuresFinder.getAllFeatures(),
                model.toOld().topNode()
        );
        // Get decision tree data
        decisionTree.setAccuracy(accuracy)
                    .setFeatureColumnNames(featuresFinder.getAllFeatures())
                    .setRootNode(new DecisionTreeNode(nodeParams))
                    .setDecisionTreeVisualization(model.toDebugString());
    }
}
