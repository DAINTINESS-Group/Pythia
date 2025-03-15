package gr.uoi.cs.pythia.model;

import java.util.Map;

public class CorrelationsProfile {

  private Map<String, Double> allCorrelations;

  public CorrelationsProfile(Map<String, Double> allCorrelations) {
    this.allCorrelations = allCorrelations;
  }

  public CorrelationsProfile(){
    //For Testing
  }

  public Map<String, Double> getAllCorrelations() {
    return allCorrelations;
  }

  public void setCorrelations(Map<String, Double> correlations){
    this.allCorrelations =correlations;
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
