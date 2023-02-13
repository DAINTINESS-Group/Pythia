package gr.uoi.cs.pythia.model;

import gr.uoi.cs.pythia.model.histogram.Histogram;

public class Column {

  private final int position;
  private final String name;
  private final String datatype;
  private CorrelationsProfile correlationsProfile;
  private DescriptiveStatisticsProfile descriptiveStatisticsProfile;
  private Histogram histogram;

  public Column(int position, String name, String datatype) {
    this.position = position;
    this.name = name;
    this.datatype = datatype;
  }

  public String getName() {
    return name;
  }

  public String getDatatype() {
    return datatype;
  }

  public CorrelationsProfile getCorrelationsProfile() {
    return correlationsProfile;
  }

  public DescriptiveStatisticsProfile getDescriptiveStatisticsProfile() {
    return descriptiveStatisticsProfile;
  }

  public Histogram getHistogram() {
    return histogram;
  }

  public void setCorrelationsProfile(CorrelationsProfile correlationsProfile) {
    this.correlationsProfile = correlationsProfile;
  }

  public void setDescriptiveStatisticsProfile(DescriptiveStatisticsProfile descriptiveStatisticsProfile) {
    this.descriptiveStatisticsProfile = descriptiveStatisticsProfile;
  }

  public void setHistogram(Histogram histogram) {
    this.histogram = histogram;
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("=============================================================================\n");
    stringBuilder.append("Column\n");
    stringBuilder.append(String.format("position: %d\n", position));
    stringBuilder.append(String.format("name: %s\n", name));
    stringBuilder.append(String.format("datatype: %s\n", datatype));
    stringBuilder.append("\n");

    if (descriptiveStatisticsProfile != null) {
      stringBuilder.append("DescriptiveStatisticsProfile:\n");
      stringBuilder.append(descriptiveStatisticsProfile);
      stringBuilder.append("\n");
    }

    if (correlationsProfile != null) {
      stringBuilder.append("CorrelationsProfile:\n");
      stringBuilder.append(correlationsProfile);
      stringBuilder.append("\n");
    }

    if (histogram != null) {
      stringBuilder.append("Histogram:\n");
      stringBuilder.append(histogram);
      stringBuilder.append("\n");
    }
    return stringBuilder.toString();
  }
}
