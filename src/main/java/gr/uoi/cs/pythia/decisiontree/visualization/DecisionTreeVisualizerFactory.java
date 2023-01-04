package gr.uoi.cs.pythia.decisiontree.visualization;

import gr.uoi.cs.pythia.decisiontree.model.DecisionTree;

public class DecisionTreeVisualizerFactory {
    private final DecisionTree decisionTree;

    public DecisionTreeVisualizerFactory(DecisionTree decisionTree) {
        this.decisionTree = decisionTree;
    }

    public IDecisionTreeVisualizer getGraphvizVisualizer() {
        return new DecisionTreeGraphvizVisualizer(decisionTree);
    }
}
