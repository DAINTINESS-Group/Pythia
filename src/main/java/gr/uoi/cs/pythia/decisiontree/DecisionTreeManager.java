package gr.uoi.cs.pythia.decisiontree;

import gr.uoi.cs.pythia.decisiontree.generator.DecisionTreeGeneratorFactory;
import gr.uoi.cs.pythia.decisiontree.input.DecisionTreeParams;
import gr.uoi.cs.pythia.decisiontree.model.DecisionTree;
import gr.uoi.cs.pythia.labeling.RuleSet;
import gr.uoi.cs.pythia.model.LabeledColumn;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.ArrayList;
import java.util.List;

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
        DecisionTreeParams decisionTreeParams = new DecisionTreeParams
                .Builder(ruleSet.getNewColumnName(), ruleSet.getTargetColumns())
                .build();
        return new DecisionTreeGeneratorFactory(decisionTreeParams, dataset)
                .getDefaultGenerator()
                .computeDecisionTree();
    }

    private LabeledColumn createLabeledColumn(DecisionTree decisionTree, RuleSet ruleSet) {
        return new LabeledColumn(
                        numOfCurrentColumns++,
                        ruleSet.getNewColumnName(),
                        StringType.toString(),
                        decisionTree);
    }
}
