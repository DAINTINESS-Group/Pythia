package gr.uoi.cs.pythia.model;

import gr.uoi.cs.pythia.model.highlights.HighlightsProfile;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class DatasetProfile {

  private final String alias;
  private  final String path;
  private final List<Column> columns;

  /**
   * ADD ONS
   * This section includes additional information about the dataset:
   * - numberOfLines: Represents the total number of lines in the dataset.
   * - fileSize: Represents the size of the file in bytes.
   * - timestamp: Represents the timestamp indicating when the profile was created.
   */
  private final long numberOfLines;
  private final Double fileSize;
  private Timestamp timestamp;
  private ZoneId zoneId;

  private final PatternsProfile patternsProfile;
  private final List<RegressionProfile> regressionProfiles;
  private ClusteringProfile clusteringProfile;
  private String auxiliaryDataOutputDirectory;
  private HighlightsProfile highlightsProfile;


  public DatasetProfile(String alias, String path, List<Column> columns, Timestamp timeStamp, long numberOfLines,Double fileSize) {
    this.alias = alias;
    this.path = path;
    this.columns = columns;
    this.timestamp = timeStamp;
    try {
      LocalDateTime localDateTime = timestamp.toLocalDateTime();
      this.zoneId = localDateTime.atZone(ZoneId.systemDefault()).getZone();
    } catch (Exception e) {
      this.zoneId = null;
    }
    this.numberOfLines = numberOfLines;
    this.fileSize = fileSize;
    this.patternsProfile = new PatternsProfile();
    this.regressionProfiles = new ArrayList<>();
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
    public void setClusteringProfile(ClusteringProfile clusteringProfile) {
		this.clusteringProfile = clusteringProfile;
	}

  public long getNumberOfLines() {
    return numberOfLines;
  }

  public Double getFileSize() {
    return fileSize;
  }

  public Timestamp getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Timestamp timestamp) {
    this.timestamp = timestamp;
  }


  public ZoneId getZoneId() {
    return zoneId;
  }


  public void setZoneId(ZoneId zoneId) {
    this.zoneId = zoneId;
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    for (Column column : columns) {
      stringBuilder.append(column.toString());
    }
    return "DatasetProfile\n" +
            "Alias: " + alias + "\n" +
            "Path: " + path + "\n" +
            "Number of Lines: " + numberOfLines + "\n" +
            "File Size: " + fileSize +" MB" +"\n" +
            "Timestamp: " + timestamp +" "+zoneId +"\n\n" +
            "Column Profiles:\n" + stringBuilder;
  }



}
