package gr.uoi.cs.pythia.decisiontree;

import gr.uoi.cs.pythia.decisiontree.generator.DecisionTreeGeneratorFactory;
import gr.uoi.cs.pythia.decisiontree.input.DecisionTreeParams;
import gr.uoi.cs.pythia.decisiontree.model.DecisionTree;
import gr.uoi.cs.pythia.labeling.Rule;
import gr.uoi.cs.pythia.labeling.RuleSet;
import gr.uoi.cs.pythia.model.LabeledColumn;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.spark.sql.types.DataTypes.StringType;

// TODO: Make with user given DecisionTreeParams (Now only does default)
public class DecisionTreeManager {

    private final List<RuleSet> ruleSets;
    private final Dataset<Row> dataset;
    private int numOfCurrentColumns;

    public DecisionTreeManager(List<RuleSet> ruleSets, Dataset<Row> dataset, int numOfCurrentColumns) {
        this.ruleSets = ruleSets;
        this.dataset = dataset;
        this.numOfCurrentColumns = numOfCurrentColumns;
    }

    public List<LabeledColumn> extractAllDecisionTrees() {
        List<LabeledColumn> labeledColumns = new ArrayList<>();
        for (RuleSet ruleSet : ruleSets) {
            DecisionTree dt = extractDecisionTree(ruleSet);
            labeledColumns.add(createLabeledColumn(dt, ruleSet));
        }
        return labeledColumns;
    }

    private DecisionTree extractDecisionTree(RuleSet ruleSet) {
        return new DecisionTreeGeneratorFactory(getDefaultDtParams(ruleSet), dataset)
                .getDefaultGenerator()
                .computeDecisionTree();
    }

    private DecisionTreeParams getDefaultDtParams(RuleSet ruleSet) {
        List<String> targetColumns = ruleSet.getRules().stream()
                .map(Rule::getTargetColumnName)
                .collect(Collectors.toList());
        return new DecisionTreeParams
                .Builder(ruleSet.getNewColumnName(), targetColumns)
                .build();
    }

    private LabeledColumn createLabeledColumn(DecisionTree decisionTree, RuleSet ruleSet) {
        return new LabeledColumn(
                        numOfCurrentColumns++,
                        ruleSet.getNewColumnName(),
                        StringType.toString(),
                        decisionTree);
    }
}
