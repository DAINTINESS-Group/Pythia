package gr.uoi.cs.pythia.report.md.components;

import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.LabeledColumn;
import gr.uoi.cs.pythia.model.decisiontree.DecisionTree;
import gr.uoi.cs.pythia.report.md.structures.MdBasicStructures;

import java.io.File;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

public class MdDecisionTrees {

    private final DatasetProfile datasetProfile;
    private final List<LabeledColumn> columns;

    public MdDecisionTrees(DatasetProfile datasetProfile) {
        this.datasetProfile = datasetProfile;
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
        return MdBasicStructures.center(MdBasicStructures.heading3("Decision Trees"));
    }

    private String getAllDecisionTrees() {
        StringBuilder stringBuilder = new StringBuilder();
        String decisionTreesDirectory = datasetProfile.getOutputDirectory() + File.separator + "decisionTrees";

        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        for (LabeledColumn column : columns) {
            stringBuilder.append(MdBasicStructures.bold(String.format(
                    "~ Column: %s", column.getName()
            )));
            stringBuilder.append("\n\n");
            File[] decisionTreesImages = new File(
                    Paths.get(decisionTreesDirectory, column.getName()).toString())
                    .listFiles();
            List<DecisionTree> decisionTrees = column.getDecisionTrees();
            for (int i=0; i< decisionTrees.size(); i++) {
                stringBuilder.append(String.format("Decision tree #%d:\n",  i+1));
                stringBuilder.append(MdBasicStructures.center(
                                     MdBasicStructures.image(
                                             decisionTreesImages[i].getAbsolutePath(), "")));
                stringBuilder.append(MdBasicStructures.center(
                                     MdBasicStructures.bold(String.format("The average impurity is %s",
                                        decimalFormat.format(decisionTrees.get(i).getAverageImpurity())))
                ));
                stringBuilder.append("\n");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
