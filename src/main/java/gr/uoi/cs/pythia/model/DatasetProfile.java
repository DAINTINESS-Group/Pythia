package gr.uoi.cs.pythia.model;

import java.util.List;
import gr.uoi.cs.pythia.model.highlights.HighlightsProfile;
public class DatasetProfile {

  private final String alias;
  private final String path;
  private final List<Column> columns;
  private final PatternsProfile patternsProfile;
  private final RegressionProfile regressionProfile;
  private String auxiliaryDataOutputDirectory;
  private HighlightsProfile highlightsProfile;
  
  public DatasetProfile(String alias, String path, List<Column> columns) {
    this.alias = alias;
    this.path = path;
    this.columns = columns;
    this.patternsProfile = new PatternsProfile();
    this.regressionProfile = new RegressionProfile();
    this.highlightsProfile = null;
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

  public PatternsProfile getPatternsProfile() {
    return patternsProfile;
  }
  
  public RegressionProfile getRegressionProfile() {
	  return regressionProfile;
  }

  public String getAuxiliaryDataOutputDirectory() {
    return auxiliaryDataOutputDirectory;
  }

  public void setAuxiliaryDataOutputDirectory(String outputDirectory) {
    this.auxiliaryDataOutputDirectory = outputDirectory;
  }

  public void setHighlightsProfile(HighlightsProfile highlightsProfile) {
	  this.highlightsProfile = highlightsProfile;
  }
  public HighlightsProfile getHighlightsProfile() {
	  return  this.highlightsProfile;
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
