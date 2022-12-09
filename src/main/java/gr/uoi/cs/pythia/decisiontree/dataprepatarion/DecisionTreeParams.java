package gr.uoi.cs.pythia.decisiontree.dataprepatarion;

import gr.uoi.cs.pythia.labeling.Rule;
import gr.uoi.cs.pythia.labeling.RuleSet;
import org.apache.spark.ml.classification.DecisionTreeClassifier;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DecisionTreeParams {
    public enum Impurity {ENTROPY, GINI}
    private Dataset<Row> dataset;
    private final String labeledColumnName;
    private final List<String> notValidFeatures;
    private List<String> selectedFeatures = new ArrayList<>();
    private final DecisionTreeClassifier decisionTreeClassifier;
    private double[] trainingAndTestDataSplitRatio;

    public DecisionTreeParams(RuleSet ruleSet) {
        this.labeledColumnName = ruleSet.getNewColumnName();
        this.notValidFeatures = findNotValidFeatures(ruleSet);
        this.decisionTreeClassifier = new DecisionTreeClassifier();
        this.trainingAndTestDataSplitRatio = new double[]{0.3, 0.7};
    }

    public DecisionTreeParams(RuleSet ruleSet, List<String> selectedFeatures) {
        this(ruleSet);
        this.selectedFeatures = validateSelectedFeatures(selectedFeatures);
    }

    private List<String> validateSelectedFeatures(List<String> selectedFeatures) {
        if (selectedFeatures == null)
            return new ArrayList<>();
        // TODO: Removes only not null elements (?) maybe throw error?
        if (selectedFeatures.contains(null))
            return selectedFeatures.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        return selectedFeatures;
    }

    // TODO: Maybe inside FeaturesFinder (?)
    private List<String> findNotValidFeatures(RuleSet ruleSet) {
        return ruleSet.getRules().stream()
                .map(Rule::getTargetColumnName)
                .collect(Collectors.toList());
    }

    public Dataset<Row> getDataset() {
        return dataset;
    }

    public String getLabeledColumnName() {
        return labeledColumnName;
    }

    public List<String> getNotValidFeatures() {
        return notValidFeatures;
    }

    public List<String> getSelectedFeatures() {
        return selectedFeatures;
    }

    public DecisionTreeClassifier getDecisionTreeClassifier() {
        return decisionTreeClassifier;
    }

    public double[] getTrainingAndTestDataSplitRatio() {
        return trainingAndTestDataSplitRatio;
    }

    public DecisionTreeParams setDataset(Dataset<Row> dataset) {
        this.dataset = dataset;
        return this;
    }

    public DecisionTreeParams setImpurity(DecisionTreeParams.Impurity impurityType) {
        decisionTreeClassifier.setImpurity(impurityType.toString().toLowerCase());
        return this;
    }

    public DecisionTreeParams setMaxDepth(int value) {
        decisionTreeClassifier.setMaxDepth(value);
        return this;
    }

    public DecisionTreeParams setMaxBins(int value) {
        decisionTreeClassifier.setMaxBins(value);
        return this;
    }

    public DecisionTreeParams setMinInfoGain(int value) {
        decisionTreeClassifier.setMinInfoGain(value);
        return this;
    }

    public DecisionTreeParams setTrainingAndTestDataSplitRatio(double[] ratio) {
        trainingAndTestDataSplitRatio = ratio;
        return this;
    }
}