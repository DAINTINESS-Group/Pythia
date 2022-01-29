package gr.uoi.cs.pythia;

import static org.junit.Assert.*;

import gr.uoi.cs.pythia.labeling.LabelingSystemConstants;
import gr.uoi.cs.pythia.labeling.Rule;
import gr.uoi.cs.pythia.labeling.RuleSet;
import gr.uoi.cs.pythia.labeling.SparkSqlExpressionGenerator;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class LabelingSystemTests {

  @Test
  public void testRuleSetCreation() {
    List<Rule> rules = new ArrayList<>();
    rules.add(new Rule("age", LabelingSystemConstants.LT, 18, "kid"));
    rules.add(new Rule("age", LabelingSystemConstants.LEQ, 30, "adult"));
    rules.add(new Rule("age", LabelingSystemConstants.GT, 60, "old"));
    rules.add(new Rule("age", LabelingSystemConstants.GEQ, 90, "outlier"));
    rules.add(new Rule("age", LabelingSystemConstants.EQ, 123, "world record"));

    RuleSet ruleSet = new RuleSet("age_labeled", rules);
    SparkSqlExpressionGenerator sparkSqlExpressionGenerator =
        new SparkSqlExpressionGenerator(ruleSet);
    String expected =
        "CASE WHEN age < '18' THEN 'kid' "
            + "WHEN age <= '30' THEN 'adult' "
            + "WHEN age > '60' THEN 'old' "
            + "WHEN age >= '90' THEN 'outlier' "
            + "WHEN age = '123' THEN 'world record' "
            + "END";

    assertEquals(expected, sparkSqlExpressionGenerator.generateExpression());
  }
}
