package gr.uoi.cs.pythia.decisiontree.model;

import gr.uoi.cs.pythia.decisiontree.model.node.DecisionTreeNode;

import java.util.List;

public class DecisionTree {
    private double accuracy;
    private List<String> featureColumnNames;
    private DecisionTreeNode rootNode;

    private String decisionTreeVisualization;

    public double getAccuracy() {
        return accuracy;
    }

    public String[] getFeatureColumnNames() {
        return featureColumnNames.toArray(new String[0]);
    }

    public DecisionTreeNode getRootNode() {
        return rootNode;
    }

    public String getDecisionTreeVisualization() {
        return decisionTreeVisualization;
    }

    public DecisionTree setAccuracy(double accuracy) {
        this.accuracy = accuracy;
        return this;
    }

    public DecisionTree setFeatureColumnNames(List<String> featureColumnNames) {
        this.featureColumnNames = featureColumnNames;
        return this;
    }

    public DecisionTree setRootNode(DecisionTreeNode rootNode) {
        this.rootNode = rootNode;
        return this;
    }

    public DecisionTree setDecisionTreeVisualization(String value) {
        this.decisionTreeVisualization = value;
        return this;
    }
}
