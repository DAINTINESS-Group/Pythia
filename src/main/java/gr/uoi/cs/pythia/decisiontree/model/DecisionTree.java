package gr.uoi.cs.pythia.decisiontree.model;

import gr.uoi.cs.pythia.decisiontree.model.node.DecisionTreeNode;
import gr.uoi.cs.pythia.decisiontree.model.path.DecisionTreePath;
import gr.uoi.cs.pythia.decisiontree.model.path.DecisionTreePathsFinder;

import java.util.ArrayList;
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
                        DecisionTreeNode rootNode) {
        this.accuracy = accuracy;
        this.featureColumnNames = featureColumnNames;
        this.nonGeneratorAttributes = nonGeneratorAttributes;
        this.rootNode = rootNode;
        this.averageImpurity = calculateAverageImpurity();
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

    public double getAverageImpurity() {
        return averageImpurity;
    }

    public List<DecisionTreePath> getPaths() {
        return paths;
    }

    private double calculateAverageImpurity() {
        List<Double> impurities = traverseNodes(new ArrayList<>(), rootNode);
        double impuritySum = impurities.stream().mapToDouble(x -> x).sum();
        return impuritySum / impurities.size();
    }

    private List<Double> traverseNodes(List<Double> impurities, DecisionTreeNode dtNode) {
        if (dtNode.isLeaf()) {
            impurities.add(dtNode.getImpurity());
            return impurities;
        }
        traverseNodes(impurities, dtNode.getLeftNode());
        traverseNodes(impurities, dtNode.getRightNode());
        return impurities;
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
