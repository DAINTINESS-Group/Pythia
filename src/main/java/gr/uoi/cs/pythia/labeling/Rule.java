package gr.uoi.cs.pythia.labeling;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Rule {
  private String targetColumnName;
  private String sparkOperator;
  private Number limit;
  private String label;

  @Override
  public String toString() {
    return String.format(
        "WHEN %s %s '%s' THEN '%s' ", targetColumnName, sparkOperator, limit, label);
  }
}
