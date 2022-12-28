package gr.uoi.cs.pythia.labeling;

import java.util.List;
import java.util.stream.Collectors;

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

  public List<String> getTargetColumns() {
    return rules.stream()
            .map(Rule::getTargetColumnName)
            .collect(Collectors.toList());
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
