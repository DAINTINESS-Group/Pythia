package gr.uoi.cs.pythia.decisiontree.model;

import gr.uoi.cs.pythia.decisiontree.model.node.DecisionTreeNode;
import gr.uoi.cs.pythia.decisiontree.model.path.DecisionTreePath;
import gr.uoi.cs.pythia.decisiontree.paths.DecisionTreePathsFinder;

import java.util.List;
import java.util.stream.Collectors;

public class DecisionTree {
    private final double accuracy;
    private final List<String> featureColumnNames;
    private final List<String> nonGeneratorAttributes;
    private final DecisionTreeNode rootNode;
    private final List<DecisionTreePath> paths;

    public DecisionTree(double accuracy,
                        List<String> featureColumnNames,
                        List<String> nonGeneratorAttributes,
                        DecisionTreeNode rootNode) {
        this.accuracy = accuracy;
        this.featureColumnNames = featureColumnNames;
        this.nonGeneratorAttributes = nonGeneratorAttributes;
        this.rootNode = rootNode;
        this.paths = new DecisionTreePathsFinder(rootNode).getPaths();
    }

    public double getAccuracy() {
        return accuracy;
    }

    public List<String> getFeatureColumnNames() {
        return featureColumnNames;
    }

    public List<String> getNonGeneratorAttributes() {
        return nonGeneratorAttributes;
    }

    public DecisionTreeNode getRootNode() {
        return rootNode;
    }

    public List<DecisionTreePath> getPaths() {
        return paths;
    }

    @Override
    public String toString() {
        String allPaths = paths.stream()
                .map(DecisionTreePath::toString)
                .collect(Collectors.joining("\n"));

        return "DecisionTree"
                + "\n"
                + "featureColumnNames="
                + String.join(", ", featureColumnNames)
                + "\n"
                + "accuracy="
                + accuracy
                + "\n"
                + "Non generator columns="
                + String.join(", ", nonGeneratorAttributes)
                + "\n"
                + "Paths="
                + "\n"
                + allPaths;
    }
}
