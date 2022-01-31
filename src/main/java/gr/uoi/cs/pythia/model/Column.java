package gr.uoi.cs.pythia.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Column {

  private int position;
  private String name;
  private String datatype;
  @Setter private CorrelationsProfile correlationsProfile;
  @Setter private DescriptiveStatisticsProfile descriptiveStatisticsProfile;

  public Column(int position, String name, String datatype) {
    this.position = position;
    this.name = name;
    this.datatype = datatype;
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
