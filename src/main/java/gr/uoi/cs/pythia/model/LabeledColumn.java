package gr.uoi.cs.pythia.model;

import gr.uoi.cs.pythia.ml.DecisionTreeBuilder;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LabeledColumn extends Column {

  private DecisionTreeBuilder decisionTree;

  public LabeledColumn(
      int position, String datatype, String newColumnName, DecisionTreeBuilder decisionTree) {
    super(position, newColumnName, datatype);
    this.decisionTree = decisionTree;
  }

  @Override
  public String toString() {
    return super.toString() + decisionTree + "\n";
  }
}
