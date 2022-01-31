package gr.uoi.cs.pythia.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class DatasetProfile {

  private String alias;
  @Setter private String path;
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
