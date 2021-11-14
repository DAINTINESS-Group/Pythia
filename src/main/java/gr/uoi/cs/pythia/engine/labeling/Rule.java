package gr.uoi.cs.pythia.engine.labeling;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
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
