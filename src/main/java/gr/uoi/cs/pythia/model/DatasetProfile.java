package gr.uoi.cs.pythia.model;

import java.util.ArrayList;
import java.util.List;
import gr.uoi.cs.pythia.model.highlights.HighlightsProfile;
public class DatasetProfile {

  private final String alias;
  private final String path;
  private final List<Column> columns;
  private final PatternsProfile patternsProfile;
  private List<RegressionProfile> regressionProfiles;
  private ClusteringProfile clusteringProfile;
  private String auxiliaryDataOutputDirectory;
  private HighlightsProfile highlightsProfile;
  
  public DatasetProfile(String alias, String path, List<Column> columns) {
    this.alias = alias;
    this.path = path;
    this.columns = columns;
    this.patternsProfile = new PatternsProfile();
    this.regressionProfiles = new ArrayList<RegressionProfile>();
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
  
  public Column getColumn(String name) {
      for (Column column : columns) {
          if (column.getName().equals(name)) {
              return column;
          }
      }return null;
  }

  public PatternsProfile getPatternsProfile() {
    return patternsProfile;
  }
  
  public List<RegressionProfile> getRegressionProfiles() {
	  return regressionProfiles;
  }
  
  public void addRegressionProfile(RegressionProfile regressionProfile) {
	  regressionProfiles.add(regressionProfile);
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
  
  public ClusteringProfile getClusteringProfile() {
	return clusteringProfile;
	}
	
	public void setRegressionProfiles(List<RegressionProfile> regressionProfiles) {
		this.regressionProfiles = regressionProfiles;
	}
	
	public void setClusteringProfile(ClusteringProfile clusteringProfile) {
		this.clusteringProfile = clusteringProfile;
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
