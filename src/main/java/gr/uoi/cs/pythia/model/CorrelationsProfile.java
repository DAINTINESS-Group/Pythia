package gr.uoi.cs.pythia.model;

import lombok.AllArgsConstructor;
import java.util.Map;

@AllArgsConstructor
public class CorrelationsProfile {

  private final Map<String, Double> allCorrelations;

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
