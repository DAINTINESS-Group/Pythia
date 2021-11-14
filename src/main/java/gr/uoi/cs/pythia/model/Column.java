package gr.uoi.cs.pythia.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Column {

  private int position;
  private String name;
  private String datatype;
  private CorrelationsProfile correlationsProfile;
  private DescriptiveStatisticsProfile descriptiveStatisticsProfile;

  public Column(int position, String name, String datatype) {
    this.position = position;
    this.name = name;
    this.datatype = datatype;
  }

  @Override
  public String toString() {
    String descriptiveStatisticsProfileString =
        descriptiveStatisticsProfile != null
            ? descriptiveStatisticsProfile.toString()
            : "";
    String correlationsProfileString =
        correlationsProfile != null
            ? correlationsProfile.toString()
            : "";
    return "Column"
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
