package gr.uoi.cs.pythia.model;

import gr.uoi.cs.pythia.decisiontree.input.DecisionTreeParams;

import java.util.ArrayList;
import java.util.List;

public class UserDefinedSettings {

    private final List<DecisionTreeParams> decisionTreesParams;

    public UserDefinedSettings() {
        this.decisionTreesParams = new ArrayList<>();
    }

    public List<DecisionTreeParams> getDecisionTreeParams() {
        return decisionTreesParams;
    }

    public void AddDecisionTreeParameters(DecisionTreeParams decisionTreeParams) {
        decisionTreesParams.add(decisionTreeParams);
    }
}
