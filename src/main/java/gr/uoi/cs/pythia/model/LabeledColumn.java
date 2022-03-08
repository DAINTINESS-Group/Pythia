package gr.uoi.cs.pythia.model;

import java.util.Arrays;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LabeledColumn extends Column {

  private double decisionTreeAccuracy;
  private String[] featureColumnNames;
  private String decisionTreeVisualization;

  public LabeledColumn(
      int position,
      String datatype,
      String newColumnName,
      double decisionTreeAccuracy,
      String[] featureColumnNames,
      String decisionTreeVisualization) {
    super(position, newColumnName, datatype);
    this.decisionTreeAccuracy = decisionTreeAccuracy;
    this.featureColumnNames = featureColumnNames;
    this.decisionTreeVisualization = decisionTreeVisualization;
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
        + "\n";
  }
}
