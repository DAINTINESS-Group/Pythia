package gr.uoi.cs.pythia.decisiontree;

import gr.uoi.cs.pythia.decisiontree.generator.DecisionTreeGeneratorFactory;
import gr.uoi.cs.pythia.decisiontree.input.DecisionTreeParams;
import gr.uoi.cs.pythia.decisiontree.model.DecisionTree;
import gr.uoi.cs.pythia.labeling.Rule;
import gr.uoi.cs.pythia.labeling.RuleSet;
import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.LabeledColumn;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DecisionTreeManager {

    private final List<RuleSet> ruleSets;
    private final Dataset<Row> dataset;
    private final List<Column> columns;

    public DecisionTreeManager(List<RuleSet> ruleSets, Dataset<Row> dataset, List<Column> columns) {
        this.ruleSets = ruleSets;
        this.dataset = dataset;
        this.columns = columns;
    }

    public List<String> extractAllDecisionTrees(List<DecisionTreeParams> userDecisionTreeParams) {
        Map<String, DecisionTreeParams> labeledColumnNameToParams = userDecisionTreeParams
                .stream()
                .collect(Collectors.toMap(
                        DecisionTreeParams::getLabeledColumnName,
                        DecisionTreeParams -> DecisionTreeParams)
                );

        List<String> labeledColumnNames = new ArrayList<>();
        for (RuleSet ruleSet : ruleSets) {
            DecisionTreeParams decisionTreeParams = getDecisionTreeParams(labeledColumnNameToParams, ruleSet);
            DecisionTree dt = extractDecisionTree(decisionTreeParams);
            String columnName = ruleSet.getNewColumnName();
            getColumn(columnName).setDecisionTree(dt);
            labeledColumnNames.add(columnName);
        }
        return labeledColumnNames;
    }

    private DecisionTreeParams getDecisionTreeParams(Map<String, DecisionTreeParams> labeledColumnNamesToParams,
                                                     RuleSet ruleSet) {
        String labeledColumnName = ruleSet.getNewColumnName();
        if (labeledColumnNamesToParams.containsKey(labeledColumnName)) {
            return labeledColumnNamesToParams.get(labeledColumnName);
        }
        else {
            return getDefaultDtParams(ruleSet);
        }
    }

    public List<String> extractAllDecisionTrees() {
        List<String> labeledColumnNames = new ArrayList<>();
        for (RuleSet ruleSet : ruleSets) {
            DecisionTreeParams decisionTreeParams = getDefaultDtParams(ruleSet);
            DecisionTree dt = extractDecisionTree(decisionTreeParams);
            String columnName = ruleSet.getNewColumnName();
            getColumn(columnName).setDecisionTree(dt);
            labeledColumnNames.add(columnName);
        }
        return labeledColumnNames;
    }

    private DecisionTreeParams getDefaultDtParams(RuleSet ruleSet) {
        List<String> targetColumns = ruleSet.getRules().stream()
                .map(Rule::getTargetColumnName)
                .collect(Collectors.toList());
        return new DecisionTreeParams
                .Builder(ruleSet.getNewColumnName(), targetColumns)
                .build();
    }

    private DecisionTree extractDecisionTree(DecisionTreeParams decisionTreeParams) {
        return new DecisionTreeGeneratorFactory(decisionTreeParams, dataset)
                .getDefaultGenerator()
                .computeDecisionTree();
    }

    private LabeledColumn getColumn(String columnName) {
        return (LabeledColumn) columns.stream()
                .filter(column -> column.getName().equals(columnName))
                .findFirst().orElse(null);
    }
}
