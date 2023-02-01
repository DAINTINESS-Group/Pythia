package gr.uoi.cs.pythia.model;

import java.util.List;

public class DatasetProfile {

  private final String alias;
  private final String path;
  private final List<Column> columns;
  private String outputDirectory;

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

  public String getOutputDirectory() {
    return outputDirectory;
  }

  public void setOutputDirectory(String outputDirectory) {
    this.outputDirectory = outputDirectory;
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
        + "\n\n"
        + "Column Profiles:"
        + '\n'
        + stringBuilder
        + "\n";
  }
}
