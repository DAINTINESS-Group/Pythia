package gr.uoi.cs.pythia.decisiontree.model;

import gr.uoi.cs.pythia.decisiontree.model.node.DecisionTreeNode;

import java.util.List;

public class DecisionTree {
    private final double accuracy;
    private final List<String> featureColumnNames;
    private final List<String> nonGeneratorAttributes;
    private final DecisionTreeNode rootNode;

    private final String decisionTreeVisualization;

    public DecisionTree(double accuracy,
                        List<String> featureColumnNames,
                        List<String> nonGeneratorAttributes,
                        DecisionTreeNode rootNode,
                        String decisionTreeVisualization) {
        this.accuracy = accuracy;
        this.featureColumnNames = featureColumnNames;
        this.nonGeneratorAttributes = nonGeneratorAttributes;
        this.rootNode = rootNode;
        this.decisionTreeVisualization = decisionTreeVisualization;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public String[] getFeatureColumnNames() {
        return featureColumnNames.toArray(new String[0]);
    }

    public List<String> getNonGeneratorAttributes() {
        return nonGeneratorAttributes;
    }

    public DecisionTreeNode getRootNode() {
        return rootNode;
    }

    public String getDecisionTreeVisualization() {
        return decisionTreeVisualization;
    }
}
