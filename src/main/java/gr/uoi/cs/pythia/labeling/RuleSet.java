package gr.uoi.cs.pythia.labeling;

import java.util.List;

public class RuleSet {
  private String newColumnName;
  private List<Rule> rules;

  public RuleSet(String newColumnName, List<Rule> rules) {
    this.newColumnName = newColumnName;
    this.rules = rules;
  }

  public String getNewColumnName() {
    return newColumnName;
  }

  public String generateSparkSqlExpression() {
    StringBuilder stringBuilder = new StringBuilder("CASE ");
    for (Rule rule : rules) {
      stringBuilder.append(rule.toString());
    }
    stringBuilder.append("END");

    return stringBuilder.toString();
  }
}
