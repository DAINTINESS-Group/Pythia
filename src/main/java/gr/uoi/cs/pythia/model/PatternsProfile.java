package gr.uoi.cs.pythia.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import gr.uoi.cs.pythia.model.dominance.DominanceResult;
import gr.uoi.cs.pythia.model.outlier.OutlierResult;

public class PatternsProfile {

  private List<DominanceResult> highDominanceResults;
  private List<DominanceResult> lowDominanceResults;
  private List<OutlierResult> outlierResults;
  private String outlierType;

  public PatternsProfile() {
    this.highDominanceResults = new ArrayList<>();
    this.lowDominanceResults = new ArrayList<>();
    this.outlierResults = new ArrayList<>();
  }

  public List<DominanceResult> getHighDominanceResults() {
    return highDominanceResults;
  }

  public List<DominanceResult> getLowDominanceResults() {
    return lowDominanceResults;
  }

  public List<OutlierResult> getOutlierResults() {
    return outlierResults;
  }

  public String getOutlierType() {
    return outlierType;
  }

  public void setHighDominanceResults(List<DominanceResult> highDominanceResults) {
    this.highDominanceResults = highDominanceResults;
  }

  public void setLowDominanceResults(List<DominanceResult> lowDominanceResults) {
    this.lowDominanceResults = lowDominanceResults;
  }

  public void setOutlierResults(List<OutlierResult> outlierResults) {
    this.outlierResults = outlierResults;
  }

  public void setOutlierType(String outlierType) {
    this.outlierType = outlierType;
  }

  public int countOutliersInColumn(String columnName) {
    int count = 0;
    for (OutlierResult result : outlierResults) {
      if (Objects.equals(result.getColumnName(), columnName)) count++;
    }
    return count;
  }

}
