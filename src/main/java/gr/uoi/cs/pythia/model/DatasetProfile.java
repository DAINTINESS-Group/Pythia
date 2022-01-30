package gr.uoi.cs.pythia.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DatasetProfile {

  private String alias;
  private String path;
  private List<Column> columns;

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    for (Column column : columns) {
      stringBuilder.append(column.toString());
    }
    return "DatasetProfile\n"
        + "alias: "
        + alias
        + '\n'
        + "path: "
        + path
        + '\n'
        + "Column Profiles:"
        + '\n'
        + stringBuilder
        + "\n";
  }
}
