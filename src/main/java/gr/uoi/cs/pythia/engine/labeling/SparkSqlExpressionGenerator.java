package gr.uoi.cs.pythia.engine.labeling;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SparkSqlExpressionGenerator {

  private RuleSet ruleSet;

  public String generateExpression() {
    StringBuilder stringBuilder = new StringBuilder("CASE ");
    for (Rule rule : ruleSet.getRules()) {
      stringBuilder.append(rule.toString());
    }
    stringBuilder.append("END");

    return stringBuilder.toString();
  }
}
