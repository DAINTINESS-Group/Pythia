package gr.uoi.cs.pythia.model;

public class Column {

  private final int position;
  private final String name;
  private final String datatype;
  private CorrelationsProfile correlationsProfile;
  private DescriptiveStatisticsProfile descriptiveStatisticsProfile;

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

  public void setCorrelationsProfile(CorrelationsProfile correlationsProfile) {
    this.correlationsProfile = correlationsProfile;
  }

  public void setDescriptiveStatisticsProfile(DescriptiveStatisticsProfile descriptiveStatisticsProfile) {
    this.descriptiveStatisticsProfile = descriptiveStatisticsProfile;
  }

  @Override
  public String toString() {
    String descriptiveStatisticsProfileString =
        descriptiveStatisticsProfile != null ? descriptiveStatisticsProfile.toString() : "";
    String correlationsProfileString =
        correlationsProfile != null ? correlationsProfile.toString() : "";
    return "=============================================================================\n"
        + "Column"
        + "\n"
        + "position: "
        + position
        + "\n"
        + "name: "
        + name
        + "\n"
        + "datatype: "
        + datatype
        + "\n"
        + "descriptiveStatisticsProfile:\n"
        + descriptiveStatisticsProfileString
        + "\n"
        + "correlationsProfile:\n"
        + correlationsProfileString
        + "\n";
  }
}
