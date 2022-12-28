package gr.uoi.cs.pythia.decisiontree.engine;

import gr.uoi.cs.pythia.decisiontree.model.DecisionTree;

public interface IDecisionTreeEngine {
    /**
     * Computes and returns a DecisionTree object,
     * based on the given DecisionTreeParams and dataset,
     * that contains information about the features, its non-generator attributes,
     * its accuracy and its nodes (with the corresponding data at each node).
     * @return the decision tree data class
     */
    DecisionTree computeDecisionTree();
}
