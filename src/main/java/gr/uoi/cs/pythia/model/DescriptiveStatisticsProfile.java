package gr.uoi.cs.pythia.model;

import java.util.List;

public class DescriptiveStatisticsProfile {

  private final String count;
  private final String mean;
  private final String standardDeviation;
  private final String median;
  private final String min;
  private final String max;
  private final String q1;
  private final String q3;
  private final List<String> modeList;

  public DescriptiveStatisticsProfile(String count, String mean, String standardDeviation, String q1, String median, String q3, String min, String max, List<String> listModes) {
    this.count = count;
    this.mean = mean;
    this.standardDeviation = standardDeviation;
    this.median = median;
    this.min = min;
    this.max = max;
    this.q1 = q1;
    this.q3 = q3;
    this.modeList = listModes;
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

  public String getQ1(){
    return q1;
  }
  public String getQ3(){
    return q3;
  }
  public List<String> getModeList() {
    return modeList;
  }
  public String modeValuetoString(){
    StringBuilder modeValuesAsString = new StringBuilder();
    if (modeList == null || modeList.isEmpty()) {
      modeValuesAsString = new StringBuilder("mode: N/A\n");
    }
    else{
      for(String mode: modeList){
        modeValuesAsString.append(mode).append(", ");
      }
      if (modeValuesAsString.length() > 0) {
        modeValuesAsString.setLength(modeValuesAsString.length() - 2);
      }
    }
    return modeValuesAsString.toString();
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
        + "q1: "
        + q1
        + '\n'
        + "median: "
        + median
        + '\n'
        + "q3: "
        + q3
        + "\n"
        + "min: "
        + min
        + '\n'
        + "max: "
        + max
        + '\n'
        + "mode: "+modeValuetoString()
        + "\n";
  }
}
