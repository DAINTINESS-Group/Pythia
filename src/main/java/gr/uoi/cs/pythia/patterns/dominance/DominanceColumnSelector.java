package gr.uoi.cs.pythia.patterns.dominance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.DataTypes;

import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;

// TODO maybe add interface-factory for the different selection modes.
public class DominanceColumnSelector {

  private final Logger logger = Logger.getLogger(DominanceColumnSelector.class);
  private static final double CORRELATIONS_THRESHOLD = 0.6;
  private static final int DISTINCT_VALUES_THRESHOLD = 20;

  private final DominanceColumnSelectionMode dominanceColumnSelectionMode;
  private final List<String> measurementColumns;
  private final List<String> coordinateColumns;
  private final List<String> userSpecifiedMeasurementColumns;

  // Valid data types for measurement columns
  private final String[] measurementDataTypes = {
          DataTypes.DoubleType.toString(),
          DataTypes.IntegerType.toString()
  };

  // Valid data types for coordinate columns
  private final String[] coordinateDataTypes = {
          DataTypes.StringType.toString(),
          DataTypes.DateType.toString()
  };

  public DominanceColumnSelector(DominanceParameters dominanceParameters) {
    this.measurementColumns = new ArrayList<>();
    this.coordinateColumns = new ArrayList<>();
    this.userSpecifiedMeasurementColumns = new ArrayList<>();

    if (dominanceParameters.getColumnSelectionMode() != null) {
      this.dominanceColumnSelectionMode = dominanceParameters.getColumnSelectionMode();
    } else {
      // Default column selection mode is SMART.
      this.dominanceColumnSelectionMode = DominanceColumnSelectionMode.SMART;
    }
    if (dominanceParameters.getMeasurementColumns() != null) {
    	this.userSpecifiedMeasurementColumns.addAll(Arrays.asList(
                dominanceParameters.getMeasurementColumns()));
      this.measurementColumns.addAll(userSpecifiedMeasurementColumns);
    }
    if (dominanceParameters.getCoordinateColumns() != null) {
      this.coordinateColumns.addAll(Arrays.asList(
              dominanceParameters.getCoordinateColumns()));
    }
  }

  public List<String> selectMeasurementColumns(DatasetProfile datasetProfile) {
    if (dominanceColumnSelectionMode.equals(DominanceColumnSelectionMode.EXHAUSTIVE)) {
      selectAllCandidateMeasurementColumns(datasetProfile);
    } else if (dominanceColumnSelectionMode.equals(DominanceColumnSelectionMode.SMART)) {
      selectInterestingMeasurementColumns(datasetProfile);
    }
    validateMeasurementColumns(datasetProfile);
    logger.info(String.format("Selected measurement columns: %s", measurementColumns));
    return measurementColumns;
  }

  public List<String> selectCoordinateColumns(DatasetProfile datasetProfile, Dataset<Row> dataset) {
    if (dominanceColumnSelectionMode.equals(DominanceColumnSelectionMode.EXHAUSTIVE)) {
      selectAllCandidateCoordinateColumns(datasetProfile);
    } else if (dominanceColumnSelectionMode.equals(DominanceColumnSelectionMode.SMART)) {
      selectInterestingCoordinateColumns(datasetProfile, dataset);
    }
    validateCoordinateColumns(datasetProfile);
    logger.info(String.format("Selected coordinate columns: %s", coordinateColumns));
    return coordinateColumns;
  }

  private void selectAllCandidateMeasurementColumns(DatasetProfile datasetProfile) {
    for (Column column : datasetProfile.getColumns()) {
      if (measurementColumns.contains(column.getName())) continue;
      if (isValidDataType(column.getDatatype(), measurementDataTypes)) {
        measurementColumns.add(column.getName());
      }
    }
  }

  private void selectAllCandidateCoordinateColumns(DatasetProfile datasetProfile) {
    for (Column column : datasetProfile.getColumns()) {
      if (coordinateColumns.contains(column.getName())) continue;
      if (isValidDataType(column.getDatatype(), coordinateDataTypes)) {
        coordinateColumns.add(column.getName());
      }
    }
  }

  // TODO 1: can we improve measurement columns selection algorithm?
  //  The algorithm currently prunes the columns that are highly correlated
  //  unless they are explicitly declared for dominance identification by the user
  //  in the input DominanceParameters.
  // TODO 2: report on pruned columns - currently no information is kept about columns
  //  that are skipped due to the fact that they are highly correlated.
  private void selectInterestingMeasurementColumns(DatasetProfile datasetProfile) {
    for (Column column : datasetProfile.getColumns()) {
      if (measurementColumns.contains(column.getName())) continue;
      if (!isValidDataType(column.getDatatype(), measurementDataTypes)) continue;
      Map<String, Double> allCorrelations = column.getCorrelationsProfile().getAllCorrelations();
      for (Map.Entry<String, Double> entry : allCorrelations.entrySet()) {
        double correlation = entry.getValue();
        String correlatedColumn = entry.getKey();
        if (Math.abs(correlation) >= CORRELATIONS_THRESHOLD) {
          if (!userSpecifiedMeasurementColumns.contains(correlatedColumn)) {
            measurementColumns.remove(correlatedColumn);
          }
        }
      }
      measurementColumns.add(column.getName());
    }
  }

  // TODO can we improve coordinate columns selection algorithm?
  //  The algorithm currently prunes the columns with more distinct values
  //  than the defined threshold allows, and the columns with a single distinct value,
  //  unless they are explicitly declared for dominance identification by the user
  //  in the input DominanceParameters.
  private void selectInterestingCoordinateColumns(DatasetProfile datasetProfile,
      Dataset<Row> dataset) {
    for (Column column : datasetProfile.getColumns()) {
      if (coordinateColumns.contains(column.getName())) continue;
      if (!isValidDataType(column.getDatatype(), coordinateDataTypes)) continue;
      List<String> distinctValues = runGetDistinctValuesQuery(dataset, column.getName());
      if (distinctValues.size() > DISTINCT_VALUES_THRESHOLD) continue;
      if (distinctValues.size() == 1) continue;
      coordinateColumns.add(column.getName());
    }
  }

  private List<String> runGetDistinctValuesQuery(Dataset<Row> dataset, String colName) {
    return dataset
            .select(colName)
            .distinct()
            .collectAsList()
            .stream()
            .map(s -> parseStringValue(s.get(0)))
            .collect(Collectors.toList());
  }

  private String parseStringValue(Object object) {
    if (object == null) return "";
    return object.toString();
  }

  private void validateMeasurementColumns(DatasetProfile datasetProfile) {
    for (String colName : measurementColumns) {
      Column column = findColumnByName(datasetProfile, colName);
      validateDataType(column, measurementDataTypes);
    }
  }

  private void validateCoordinateColumns(DatasetProfile datasetProfile) {
    for (String colName : coordinateColumns) {
      Column column = findColumnByName(datasetProfile, colName);
      validateDataType(column, coordinateDataTypes);
    }
  }

  private Column findColumnByName(DatasetProfile datasetProfile, String colName) {
    for (Column column : datasetProfile.getColumns()) {
      if (column.getName().equals(colName)) return column;
    }
    throw new IllegalArgumentException(String.format(
            "Column \"%s\" is not a column of the registered dataset file" +
                    "with alias \"%s\" at \"%s\".",
            colName, datasetProfile.getAlias(), datasetProfile.getPath()));
  }

  private void validateDataType(Column column, String[] validDataTypes) {
    if (isValidDataType(column.getDatatype(), validDataTypes)) return;
    throw new IllegalArgumentException(String.format(
            "Column \"%s\" has an invalid data type of \"%s\".\n" +
                    "Valid data types are: %s",
            column.getName(),
            column.getDatatype(),
            Arrays.asList(validDataTypes)));
  }

  private boolean isValidDataType(String dataType, String[] validDataTypes) {
    for (String validDataType : validDataTypes) {
      if (validDataType.equals(dataType)) return true;
    }
    return false;
  }

}
