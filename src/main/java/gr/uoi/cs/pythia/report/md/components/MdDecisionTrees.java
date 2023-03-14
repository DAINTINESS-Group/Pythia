package gr.uoi.cs.pythia.report.md.components;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.LabeledColumn;
import gr.uoi.cs.pythia.model.decisiontree.DecisionTree;
import gr.uoi.cs.pythia.report.md.structures.MdBasicStructures;

public class MdDecisionTrees {

    private final List<LabeledColumn> columns;
    private final DecimalFormat decimalFormat = new DecimalFormat("#.###",
                                                    new DecimalFormatSymbols(Locale.ENGLISH));

    public MdDecisionTrees(DatasetProfile datasetProfile) {
        this.columns = datasetProfile.getColumns().stream()
                .filter(column -> column instanceof LabeledColumn)
                .map(column -> (LabeledColumn) column)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        if (columns.isEmpty())
            return "";
        return getTitle() + "\n" +
                MdBasicStructures.horizontalLine() + "\n" +
                getAllDecisionTrees() + "\n";
    }

    private String getTitle() {
        return MdBasicStructures.center(MdBasicStructures.heading2("Decision Trees"));
    }

    private String getAllDecisionTrees() {
        StringBuilder stringBuilder = new StringBuilder();
        for (LabeledColumn column : columns) {
            stringBuilder.append(MdBasicStructures.bold(
                    String.format("~ Column: %s", column.getName())));
            stringBuilder.append("\n\n");
            addDecisionTreesForColumn(column, stringBuilder);
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    private void addDecisionTreesForColumn(LabeledColumn column, StringBuilder stringBuilder) {
        List<DecisionTree> decisionTrees = column.getDecisionTrees();
        for (int i=0; i < decisionTrees.size(); i++) {
            stringBuilder.append(String.format("Decision tree #%d:\n",  i+1));
            stringBuilder.append(MdBasicStructures.center(
                    MdBasicStructures.image(
                            decisionTrees.get(i).getOutputPath(), "")));
            stringBuilder.append(MdBasicStructures.center(
                    MdBasicStructures.bold(String.format("The average impurity is %s",
                            decimalFormat.format(decisionTrees.get(i).getAverageImpurity())))
            ));
            stringBuilder.append("\n");
        }
    }
}
