package gr.uoi.cs.pythia.model;

public class DescriptiveStatisticsProfile {

  private final String count;
  private final String mean;
  private final String standardDeviation;
  private final String median;
  private final String min;
  private final String max;

  public DescriptiveStatisticsProfile(String count, String mean, String standardDeviation, String median, String min, String max) {
    this.count = count;
    this.mean = mean;
    this.standardDeviation = standardDeviation;
    this.median = median;
    this.min = min;
    this.max = max;
  }

  public String getCount() {
    return count;
  }

  public String getMean() {
    return mean;
  }

  public String getStandardDeviation() {
    return standardDeviation;
  }

  public String getMedian() {
    return median;
  }

  public String getMin() {
    return min;
  }

  public String getMax() {
    return max;
  }

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
