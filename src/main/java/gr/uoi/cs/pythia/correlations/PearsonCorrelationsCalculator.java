package gr.uoi.cs.pythia.correlations;

import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.CorrelationsProfile;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.util.DatasetProfilerUtils;
import gr.uoi.cs.pythia.util.Pair;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.DataTypes;

public class PearsonCorrelationsCalculator implements ICorrelationsCalculator {

  @Override
  public void calculateAllPairsCorrelations(Dataset<Row> dataset, DatasetProfile datasetProfile) {
    Set<String> supportedDataTypes =
        new HashSet<>(
            Arrays.asList(
                String.valueOf(DataTypes.IntegerType),
                String.valueOf(DataTypes.DoubleType),
                String.valueOf(DataTypes.FloatType),
                String.valueOf(DataTypes.LongType),
                String.valueOf(DataTypes.ShortType)));
    List<String> columnNames =
        DatasetProfilerUtils.filterOutDatasetColumnsByTypes(datasetProfile, supportedDataTypes);

    List<Pair<String>> allPairs = DatasetProfilerUtils.calculateAllPairsOfColumns(columnNames);

    List<Double> correlations =
        allPairs.stream()
            .map(pair -> dataset.stat().corr(pair.getColumnA(), pair.getColumnB()))
            .collect(Collectors.toList());

    Map<Pair<String>, Double> correlationsResults =
        DatasetProfilerUtils.zipListsToMap(allPairs, correlations);

    for (Column column : datasetProfile.getColumns()) {
      Map<String, Double> correlationProfile =
          DatasetProfilerUtils.filterCorrelationResultsByColumnName(
              correlationsResults, column.getName());
      column.setCorrelationsProfile(new CorrelationsProfile(correlationProfile));
    }
  }
}
