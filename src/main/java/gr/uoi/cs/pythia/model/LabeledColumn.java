package gr.uoi.cs.pythia.model;

import gr.uoi.cs.pythia.engine.ml.DecisionTreeBuilderForLabeledColumn;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LabeledColumn extends Column {

  private DecisionTreeBuilderForLabeledColumn decisionTree;

  public LabeledColumn(
      int position,
      String datatype,
      String newColumnName,
      DecisionTreeBuilderForLabeledColumn decisionTree) {
    super(position, newColumnName, datatype);
    this.decisionTree = decisionTree;
  }

  @Override
  public String toString() {
    return super.toString() + decisionTree + "\n";
  }
}
