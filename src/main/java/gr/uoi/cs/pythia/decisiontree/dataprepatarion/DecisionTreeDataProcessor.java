package gr.uoi.cs.pythia.decisiontree.dataprepatarion;

import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DecisionTreeDataProcessor {

    private final FeaturesFinder featuresFinder;
    private final String labeledColumnName;
    private Dataset<Row> dataset;
    private final Dataset<Row> trainingData;
    private final Dataset<Row> testData;

    public DecisionTreeDataProcessor(DecisionTreeParams decisionTreeParams, FeaturesFinder featuresFinder) {
        this.featuresFinder = featuresFinder;
        this.labeledColumnName = decisionTreeParams.getLabeledColumnName();
        this.dataset = decisionTreeParams.getDataset();
        // compute data
        Dataset<Row>[] splits = getInputData()
                .randomSplit(decisionTreeParams.getTrainingAndTestDataSplitRatio());
        this.trainingData = splits[0];
        this.testData = splits[1];
    }

    public Dataset<Row> getTrainingData() {
        return trainingData;
    }

    public Dataset<Row> getTestData() {
        return testData;
    }

    private Dataset<Row> getInputData() {
        indexCategoricalFeaturesInTheDataset();

        return new VectorAssembler()
                .setHandleInvalid("skip")
                .setInputCols(featuresFinder.getAllFeaturesIndexed()
                        .toArray(new String[0]))
                .setOutputCol("features")
                .transform(dataset)
                .select("features", labeledColumnName + "_indexed");
    }

    private void indexCategoricalFeaturesInTheDataset() {
        // index all categorical features
        for (String categoricalColumn : featuresFinder.getCategoricalFeatures()) {
            dataset = indexColumn(categoricalColumn);
        }
        // index labeled field
        dataset = indexColumn(labeledColumnName);
    }

    private Dataset<Row> indexColumn(String categoricalColumn) {
        return new StringIndexer().setHandleInvalid("skip")
                .setInputCol(categoricalColumn)
                .setOutputCol(categoricalColumn + "_indexed")
                .fit(dataset)
                .transform(dataset);
    }

    public HashMap<String, HashMap<Double, String>> getIndexedToActualValuesForEachIndexedColumn() {
        HashMap<String, HashMap<Double, String>> indexedColumnsToIndexedAndActualValues = new HashMap<>();

        List<String> indexedColumns = new ArrayList<>();
        indexedColumns.addAll(featuresFinder.getCategoricalFeatures());
        indexedColumns.add(labeledColumnName);
        for (String indexedColumn : indexedColumns) {
            HashMap<Double, String> indexedToActualValues = new HashMap<>();
            Iterator<Row> rows = dataset
                    .select(indexedColumn, indexedColumn + "_indexed")
                    .distinct()
                    .toLocalIterator();
            while (rows.hasNext()) {
                Row row = rows.next();
                indexedToActualValues.put(row.getDouble(1), row.getString(0));
            }
            indexedColumnsToIndexedAndActualValues.put(indexedColumn, indexedToActualValues);
        }
        return indexedColumnsToIndexedAndActualValues;
    }
}