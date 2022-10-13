package gr.uoi.cs.pythia.model;

import java.util.List;

public class DatasetProfile {

  private String alias;
  private String path;
  private List<Column> columns;

  public DatasetProfile(String alias, String path, List<Column> columns) {
    this.alias = alias;
    this.path = path;
    this.columns = columns;
  }

  public String getAlias() {
    return alias;
  }

  public String getPath() {
    return path;
  }

  public List<Column> getColumns() {
    return columns;
  }

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
