package gr.uoi.cs.pythia;

import static org.junit.Assert.*;

import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.util.DatasetProfilerUtils;
import gr.uoi.cs.pythia.util.Pair;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.spark.sql.types.DataTypes;
import org.junit.Before;
import org.junit.Test;

public class UtilTests {

  private List<Pair<String>> expectedPairs;

  @Before
  public void init() {
    expectedPairs =
        Arrays.asList(
            new Pair<>("a", "b"),
            new Pair<>("a", "c"),
            new Pair<>("a", "d"),
            new Pair<>("a", "e"),
            new Pair<>("b", "c"),
            new Pair<>("b", "d"),
            new Pair<>("b", "e"),
            new Pair<>("c", "d"),
            new Pair<>("c", "e"),
            new Pair<>("d", "e"));
  }

  @Test
  public void testFilterOutDatasetColumnsByType() {
    List<Column> columnProperties = new ArrayList<>();
    columnProperties.add(new Column(0, "Test1", String.valueOf(DataTypes.TimestampType)));
    columnProperties.add(new Column(1, "Test2", String.valueOf(DataTypes.IntegerType)));
    columnProperties.add(new Column(2, "Test3", String.valueOf(DataTypes.StringType)));
    columnProperties.add(new Column(3, "Test4", String.valueOf(DataTypes.StringType)));

    DatasetProfile test = new DatasetProfile("test", "test.json", columnProperties);

    List<String> actualFiltered =
        DatasetProfilerUtils.filterOutDatasetColumnsByTypes(
            test,
            new HashSet<>(
                Arrays.asList(
                    String.valueOf(DataTypes.StringType),
                    String.valueOf(DataTypes.TimestampType))));
    assertEquals(actualFiltered, Collections.singletonList("Test2"));
  }

  @Test
  public void testCalculateAllPairsOfColumns() {
    List<String> columns = Arrays.asList("a", "b", "c", "d", "e");
    List<Pair<String>> actual = DatasetProfilerUtils.calculateAllPairsOfColumns(columns);
    assertEquals(actual, expectedPairs);
  }

  @Test
  public void testZipListsToMap() {

    List<Double> dummyCorrelations =
        Arrays.asList(0.1, 0.2, 0.4, 0.5, 0.567, 0.001, 0.9, 1.0, 0.86543, 0.1235);

    Map<Pair<String>, Double> actual =
        DatasetProfilerUtils.zipListsToMap(expectedPairs, dummyCorrelations);

    Map<Pair<String>, Double> expected =
        new HashMap<Pair<String>, Double>() {
          {
            put(expectedPairs.get(0), dummyCorrelations.get(0));
            put(expectedPairs.get(1), dummyCorrelations.get(1));
            put(expectedPairs.get(2), dummyCorrelations.get(2));
            put(expectedPairs.get(3), dummyCorrelations.get(3));
            put(expectedPairs.get(4), dummyCorrelations.get(4));
            put(expectedPairs.get(5), dummyCorrelations.get(5));
            put(expectedPairs.get(6), dummyCorrelations.get(6));
            put(expectedPairs.get(7), dummyCorrelations.get(7));
            put(expectedPairs.get(8), dummyCorrelations.get(8));
            put(expectedPairs.get(9), dummyCorrelations.get(9));
          }
        };
    assertEquals(actual, expected);
  }

  @Test
  public void testFilterCorrelationResultsByColumnName() {
    List<Double> dummyCorrelations =
        Arrays.asList(0.1, 0.2, 0.4, 0.5, 0.567, 0.001, 0.9, 1.0, 0.86543, 0.1235);

    Map<Pair<String>, Double> correlationsResults =
        new HashMap<Pair<String>, Double>() {
          {
            put(expectedPairs.get(0), dummyCorrelations.get(0));
            put(expectedPairs.get(1), dummyCorrelations.get(1));
            put(expectedPairs.get(2), dummyCorrelations.get(2));
            put(expectedPairs.get(3), dummyCorrelations.get(3));
            put(expectedPairs.get(4), dummyCorrelations.get(4));
            put(expectedPairs.get(5), dummyCorrelations.get(5));
            put(expectedPairs.get(6), dummyCorrelations.get(6));
            put(expectedPairs.get(7), dummyCorrelations.get(7));
            put(expectedPairs.get(8), dummyCorrelations.get(8));
            put(expectedPairs.get(9), dummyCorrelations.get(9));
          }
        };

    Map<String, Double> actual =
        DatasetProfilerUtils.filterCorrelationResultsByColumnName(correlationsResults, "c");
    Map<String, Double> expected =
        new HashMap<String, Double>() {
          {
            put("a", dummyCorrelations.get(1));
            put("b", dummyCorrelations.get(4));
            put("d", dummyCorrelations.get(7));
            put("e", dummyCorrelations.get(8));
          }
        };
    assertEquals(actual, expected);

    actual = DatasetProfilerUtils.filterCorrelationResultsByColumnName(correlationsResults, "d");
    expected =
        new HashMap<String, Double>() {
          {
            put("a", dummyCorrelations.get(2));
            put("b", dummyCorrelations.get(5));
            put("c", dummyCorrelations.get(7));
            put("e", dummyCorrelations.get(9));
          }
        };
    assertEquals(actual, expected);

    actual = DatasetProfilerUtils.filterCorrelationResultsByColumnName(correlationsResults, "e");
    expected =
        new HashMap<String, Double>() {
          {
            put("a", dummyCorrelations.get(3));
            put("b", dummyCorrelations.get(6));
            put("c", dummyCorrelations.get(8));
            put("d", dummyCorrelations.get(9));
          }
        };
    assertEquals(actual, expected);
  }
}
