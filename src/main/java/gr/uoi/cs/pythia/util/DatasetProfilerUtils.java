package gr.uoi.cs.pythia.util;

import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.spark.sql.types.DataTypes;

public class DatasetProfilerUtils {

  public static List<String> filterOutDatasetColumnsByTypes(
      DatasetProfile datasetProfile, Set<String> dataTypes) {
    return datasetProfile.getColumns().stream()
        .filter(columnProperty -> dataTypes.contains(columnProperty.getDatatype()))
        .map(Column::getName)
        .collect(Collectors.toList());
  }

  public static List<Pair<String>> calculateAllPairsOfColumns(List<String> columnNames) {
    List<Pair<String>> allPairs = new ArrayList<>();
    for (int i = 0; i < columnNames.size(); ++i) {
      for (int j = i; j < columnNames.size(); ++j) {
        String columnA = columnNames.get(i);
        String columnB = columnNames.get(j);
        if (columnA.equals(columnB)) continue;
        allPairs.add(new Pair<>(columnA, columnB));
      }
    }
    return allPairs;
  }

  public static Map<Pair<String>, Double> zipListsToMap(
      List<Pair<String>> allPairs, List<Double> correlations) {
    assert allPairs.size() == correlations.size();
    return IntStream.range(0, allPairs.size())
        .boxed()
        .collect(Collectors.toMap(allPairs::get, correlations::get));
  }

  public static Map<String, Double> filterCorrelationResultsByColumnName(
      Map<Pair<String>, Double> correlationsResults, String columnName) {

    Map<Pair<String>, Double> correlations =
        correlationsResults.entrySet().stream()
            .filter(
                map ->
                    map.getKey().getColumnA().equals(columnName)
                        || map.getKey().getColumnB().equals(columnName))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    Map<String, Double> columnACorrelations =
        correlations.entrySet().stream()
            .filter(map -> map.getKey().getColumnA().equals(columnName))
            .collect(Collectors.toMap(e -> e.getKey().getColumnB(), Map.Entry::getValue));

    Map<String, Double> columnBCorrelations =
        correlations.entrySet().stream()
            .filter(map -> map.getKey().getColumnB().equals(columnName))
            .collect(Collectors.toMap(e -> e.getKey().getColumnA(), Map.Entry::getValue));

    return Stream.of(columnACorrelations, columnBCorrelations)
        .flatMap(map -> map.entrySet().stream())
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  public static boolean columnIsNumeric(String datatype) {
    Set<String> numericDatatypes =
        new HashSet<>(
            Arrays.asList(
                String.valueOf(DataTypes.ShortType),
                String.valueOf(DataTypes.IntegerType),
                String.valueOf(DataTypes.FloatType),
                String.valueOf(DataTypes.DoubleType)));
    return numericDatatypes.contains(datatype);
  }
}
