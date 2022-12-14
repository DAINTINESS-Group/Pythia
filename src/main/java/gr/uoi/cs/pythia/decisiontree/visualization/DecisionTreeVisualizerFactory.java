package gr.uoi.cs.pythia.decisiontree.visualization;

import gr.uoi.cs.pythia.decisiontree.model.DecisionTree;

public class DecisionTreeVisualizerFactory {
    private DecisionTree decisionTree;

    public DecisionTreeVisualizerFactory(DecisionTree decisionTree) {
        this.decisionTree = decisionTree;
    }

    public DecisionTreeVisualizer getGraphvizVisualizer() {
        return new DecisionTreeGraphvizVisualizer(decisionTree);
    }
}
