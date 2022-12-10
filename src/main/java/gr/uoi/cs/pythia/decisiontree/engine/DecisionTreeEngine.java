package gr.uoi.cs.pythia.decisiontree.engine;

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

public class DecisionTreeEngine implements IDecisionTreeEngine {

    private final Dataset<Row> dataset;
    private final DecisionTreeParams decisionTreeParams;
    private final DecisionTree decisionTree;

    public DecisionTreeEngine(DecisionTreeParams decisionTreeParams, Dataset<Row> dataset) {
        this.decisionTreeParams = decisionTreeParams;
        this.dataset = dataset;
        this.decisionTree = computeDecisionTree();
    }

    public DecisionTree getDecisionTree() {
        return decisionTree;
    }

    private DecisionTree computeDecisionTree() {
        FeaturesFinder featuresFinder = new FeaturesFinder(decisionTreeParams, dataset);
        DecisionTreeDataProcessor decisionTreeDataProcessor =
                new DecisionTreeDataProcessor(decisionTreeParams, featuresFinder, dataset);

        // Train a DecisionTree model.
        DecisionTreeClassifier decisionTreeClassifier = new DecisionTreeClassifier()
                .setLabelCol(decisionTreeParams.getLabeledColumnName() + "_indexed")
                .setImpurity(decisionTreeParams.getImpurity())
                .setMaxDepth(decisionTreeParams.getMaxDepth())
                .setMinInfoGain(decisionTreeParams.getMinInfoGain());

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

        return new DecisionTree(accuracy,
                                featuresFinder.getAllFeatures(),
                                new DecisionTreeNode(nodeParams),
                                model.toDebugString());
    }
}
