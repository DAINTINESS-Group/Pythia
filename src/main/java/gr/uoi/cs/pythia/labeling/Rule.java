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

  public String getTargetColumnName() { return targetColumnName; }

  public String getSparkOperator(){
    return this.sparkOperator;
  }

  public Object getLimit(){
    return this.limit;
  }

  public String getLabel(){
    return this.label;
  }
  @Override
  public String toString() {
    return String.format(
        "WHEN %s %s '%s' THEN '%s' ", targetColumnName, sparkOperator, limit, label);
  }
}
