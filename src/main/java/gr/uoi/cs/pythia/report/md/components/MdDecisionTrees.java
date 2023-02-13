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
            stringBuilder.append(
                    String.format("Decision Trees for '%s':\n", column.getName())
            );
            File[] decisionTreesImages = new File(
                    Paths.get(decisionTreesDirectory, column.getName()).toString())
                    .listFiles();
            List<DecisionTree> decisionTrees = column.getDecisionTrees();
            for (int i=0; i< decisionTrees.size(); i++) {
                stringBuilder.append(MdBasicStructures.center(
                                     MdBasicStructures.image(
                                             decisionTreesImages[i].getAbsolutePath(), "")));
                stringBuilder.append(MdBasicStructures.center(
                                     MdBasicStructures.heading4(
                                decimalFormat.format(decisionTrees.get(i).getAverageImpurity()))));
            }
        }
        return stringBuilder.toString();
    }
}
