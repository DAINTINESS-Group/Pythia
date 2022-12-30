package gr.uoi.cs.pythia.model;

import gr.uoi.cs.pythia.decisiontree.model.DecisionTree;

public class LabeledColumn extends Column {

  private final DecisionTree decisionTree;

  public LabeledColumn(
          int position,
          String newColumnName,
          String datatype,
          DecisionTree decisionTree) {
    super(position, newColumnName, datatype);
    this.decisionTree = decisionTree;
  }

  @Override
  public String toString() {
    return super.toString()
            + "\n"
            + decisionTree.toString();
  }
}
