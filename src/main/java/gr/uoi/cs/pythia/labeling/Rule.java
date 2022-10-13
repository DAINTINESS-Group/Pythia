package gr.uoi.cs.pythia.labeling;

public class Rule {
  private String targetColumnName;
  private String sparkOperator;
  private Number limit;
  private String label;

  public Rule(String targetColumnName, String sparkOperator, Number limit, String label) {
    this.targetColumnName = targetColumnName;
    this.sparkOperator = sparkOperator;
    this.limit = limit;
    this.label = label;
  }

  @Override
  public String toString() {
    return String.format(
        "WHEN %s %s '%s' THEN '%s' ", targetColumnName, sparkOperator, limit, label);
  }
}
