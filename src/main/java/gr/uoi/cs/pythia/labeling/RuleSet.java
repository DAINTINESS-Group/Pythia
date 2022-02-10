package gr.uoi.cs.pythia.labeling;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RuleSet {
  private String newColumnName;
  private List<Rule> rules;

  public String generateSparkSqlExpression() {
    StringBuilder stringBuilder = new StringBuilder("CASE ");
    for (Rule rule : rules) {
      stringBuilder.append(rule.toString());
    }
    stringBuilder.append("END");

    return stringBuilder.toString();
  }
}
