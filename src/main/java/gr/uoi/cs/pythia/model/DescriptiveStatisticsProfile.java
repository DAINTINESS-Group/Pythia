package gr.uoi.cs.pythia.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DescriptiveStatisticsProfile {

  private final String count;
  private final String mean;
  private final String standardDeviation;
  private final String median;
  private final String min;
  private final String max;

  @Override
  public String toString() {
    return "count: "
        + count
        + '\n'
        + "mean: "
        + mean
        + '\n'
        + "standardDeviation: "
        + standardDeviation
        + '\n'
        + "median: "
        + median
        + '\n'
        + "min: "
        + min
        + '\n'
        + "max: "
        + max
        + '\n';
  }
}
