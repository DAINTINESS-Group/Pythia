package gr.uoi.cs.pythia.model;

import java.util.Map;

public class CorrelationsProfile {

  private final Map<String, Double> allCorrelations;

  public CorrelationsProfile(Map<String, Double> allCorrelations) {
    this.allCorrelations = allCorrelations;
  }

  public Map<String, Double> getAllCorrelations() {
    return allCorrelations;
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    for (Map.Entry<String, Double> entry : allCorrelations.entrySet()) {
      stringBuilder.append(
          String.format("Column correlation with %s is %s\n", entry.getKey(), entry.getValue()));
    }
    return stringBuilder.toString();
  }
}
