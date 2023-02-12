package gr.uoi.cs.pythia.model.decisiontree;

import gr.uoi.cs.pythia.model.decisiontree.node.DecisionTreeNode;

import java.util.List;
import java.util.stream.Collectors;

public class DecisionTree {
    private final double accuracy;
    private final List<String> featureColumnNames;
    private final List<String> nonGeneratorAttributes;
    private final DecisionTreeNode rootNode;
    private final double averageImpurity;
    private final List<DecisionTreePath> paths;

    public DecisionTree(double accuracy,
                        List<String> featureColumnNames,
                        List<String> nonGeneratorAttributes,
                        DecisionTreeNode rootNode,
                        double averageImpurity,
                        List<DecisionTreePath> paths
                        ) {
        this.accuracy = accuracy;
        this.featureColumnNames = featureColumnNames;
        this.nonGeneratorAttributes = nonGeneratorAttributes;
        this.rootNode = rootNode;
        this.averageImpurity = averageImpurity;
        this.paths = paths;
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

    public double getAverageImpurity() {
        return averageImpurity;
    }

    public List<DecisionTreePath> getPaths() {
        return paths;
    }

    @Override
    public String toString() {
        String allPaths = paths.stream()
                .map(DecisionTreePath::toString)
                .collect(Collectors.joining("\n"));

        return "DecisionTree info: "
                + "\n"
                + "featureColumnNames: "
                + String.join(", ", featureColumnNames)
                + "\n"
                + "accuracy: "
                + accuracy
                + "\n"
                + "Non generator columns: "
                + String.join(", ", nonGeneratorAttributes)
                + "\n"
                + "Average impurity: "
                + averageImpurity
                + "\n"
                + "Paths: "
                + "\n"
                + allPaths
                + "\n";
    }
}
