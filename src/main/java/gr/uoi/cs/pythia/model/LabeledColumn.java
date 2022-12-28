package gr.uoi.cs.pythia.model;

import java.util.Arrays;
import java.util.List;

public class LabeledColumn extends Column {

  private double decisionTreeAccuracy;
  private String[] featureColumnNames;
  private String decisionTreeVisualization;
  private List<String> nonGeneratorColumns;

  public LabeledColumn(
          int position,
          String datatype,
          String newColumnName,
          double decisionTreeAccuracy,
          String[] featureColumnNames,
          String decisionTreeVisualization,
          List<String> nonGeneratorColumns) {
    super(position, newColumnName, datatype);
    this.decisionTreeAccuracy = decisionTreeAccuracy;
    this.featureColumnNames = featureColumnNames;
    this.decisionTreeVisualization = decisionTreeVisualization;
    this.nonGeneratorColumns = nonGeneratorColumns;
  }

  @Override
  public String toString() {
    return super.toString()
        + "\nDecisionTree\n"
        + "featureColumnNames="
        + Arrays.toString(featureColumnNames)
        + "\n"
        + "accuracy="
        + decisionTreeAccuracy
        + "\n"
        + "decisionTreeVisualization='"
        + decisionTreeVisualization
        + "\n"
        + "Non generator columns="
        + String.join(", ", nonGeneratorColumns)
        + "\n";
  }
}
