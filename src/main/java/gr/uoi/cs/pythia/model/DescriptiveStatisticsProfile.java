package gr.uoi.cs.pythia.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DescriptiveStatisticsProfile {

  private String count;
  private String mean;
  private String standardDeviation;
  private String median;
  private String min;
  private String max;

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
