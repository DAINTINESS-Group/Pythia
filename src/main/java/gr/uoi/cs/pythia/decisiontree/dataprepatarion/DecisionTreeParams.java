package gr.uoi.cs.pythia.decisiontree.dataprepatarion;

import gr.uoi.cs.pythia.labeling.Rule;
import gr.uoi.cs.pythia.labeling.RuleSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DecisionTreeParams {
    public enum Impurity {ENTROPY, GINI}
    private final String impurity;
    private final String labeledColumnName;
    private final List<String> notValidFeatures;
    private final List<String> selectedFeatures;
    private final double[] trainingToTestDataSplitRatio;
    private final int maxDepth;
    private final double minInfoGain;

    private DecisionTreeParams(Builder builder) {
        this.impurity = builder.impurity;
        this.labeledColumnName = builder.labeledColumnName;
        this.notValidFeatures = builder.notValidFeatures;
        this.selectedFeatures = builder.selectedFeatures;
        this.trainingToTestDataSplitRatio = builder.trainingToTestDataSplitRatio;
        this.maxDepth = builder.maxDepth;
        this.minInfoGain = builder.minInfoGain;
    }

    public String getImpurity() {
        return impurity;
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

    public double[] getTrainingToTestDataSplitRatio() {
        return trainingToTestDataSplitRatio;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public double getMinInfoGain() {
        return minInfoGain;
    }

    public static class Builder {
        private final String labeledColumnName;
        private final List<String> notValidFeatures;
        private String impurity;
        private List<String> selectedFeatures;
        private double[] trainingToTestDataSplitRatio;
        private int maxDepth;
        private double minInfoGain;

        public Builder(RuleSet ruleSet) {
            // does not check each rule etc., should it?
            if (ruleSet == null)
                throw new NullPointerException("Null RuleSet on DecisionTreeParams.");
            this.labeledColumnName = ruleSet.getNewColumnName();
            this.notValidFeatures = findNotValidFeatures(ruleSet);
            // default values
            this.impurity = Impurity.GINI.toString().toLowerCase();
            this.selectedFeatures = new ArrayList<>();
            this.trainingToTestDataSplitRatio = new double[]{0.3, 0.7};
            this.maxDepth = 5;
            this.minInfoGain = 0.0;
        }

        public Builder impurity(Impurity impurity) {
            this.impurity = impurity.toString().toLowerCase();
            return this;
        }

        public Builder selectedFeatures(List<String> selectedFeatures) {
            this.selectedFeatures = validateSelectedFeatures(selectedFeatures);
            return this;
        }

        public Builder trainingToTestDataSplitRatio(double[] ratio) {
            if (ratio != null)
                this.trainingToTestDataSplitRatio = ratio;
            return this;
        }

        public Builder maxDepth(int value) {
            if (value >= 0)
                this.maxDepth = value;
            return this;
        }

        public Builder minInfoGain(double value) {
            if (value >= 0)
                this.minInfoGain = value;
            return this;
        }

        public DecisionTreeParams build() {
            return new DecisionTreeParams(this);
        }

        // TODO: Maybe inside FeaturesFinder (?)
        private List<String> findNotValidFeatures(RuleSet ruleSet) {
            return ruleSet.getRules().stream()
                    .map(Rule::getTargetColumnName)
                    .collect(Collectors.toList());
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
    }
}
