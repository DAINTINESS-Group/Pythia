package gr.uoi.cs.pythia.engine.ml;

import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.util.DatasetProfilerUtils;
import gr.uoi.cs.pythia.util.Pair;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.ml.classification.DecisionTreeClassificationModel;
import org.apache.spark.ml.classification.DecisionTreeClassifier;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.feature.IndexToString;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

@Getter
@Setter
public class DecisionTreeBuilderForLabeledColumn {

  private String[] featureColumnNames;
  private Map<String, String> indexedLabelsToActualValues;
  private double accuracy;
  private String decisionTreeVisualization;

  public DecisionTreeBuilderForLabeledColumn(
      Dataset<Row> dataset, DatasetProfile datasetProfile, String labeledColumnName) {
    // Index labeled column
    Dataset<Row> dataset2 =
        new StringIndexer()
            .setHandleInvalid("skip")
            .setInputCol(labeledColumnName)
            .setOutputCol(labeledColumnName + "_indexed")
            .fit(dataset)
            .transform(dataset);

    // Index mapping for labels
    Dataset<Row> indexToString =
        new IndexToString()
            .setInputCol(labeledColumnName + "_indexed")
            .setOutputCol("value")
            .transform(dataset2.select(labeledColumnName + "_indexed").distinct());

    // Determine input columns for the features
    featureColumnNames =
        datasetProfile.getColumns().stream()
            .filter(
                columnProperty ->
                    DatasetProfilerUtils.columnIsNumeric(columnProperty.getDatatype()))
            .map(Column::getName)
            .toArray(String[]::new);

    // Assemble the features
    VectorAssembler vectorAssembler =
        new VectorAssembler()
            .setHandleInvalid("skip")
            .setInputCols(featureColumnNames)
            .setOutputCol("features");

    Dataset<Row> inputData =
        vectorAssembler.transform(dataset2).select(labeledColumnName + "_indexed", "features");

    // Split the data into training and test sets (30% held out for testing).
    Dataset<Row>[] splits = inputData.randomSplit(new double[] {0.7, 0.3});
    Dataset<Row> trainingData = splits[0];
    Dataset<Row> testData = splits[1];

    // Train a DecisionTree model.
    DecisionTreeClassifier decisionTreeClassifier =
        new DecisionTreeClassifier()
            .setLabelCol(labeledColumnName + "_indexed")
            .setFeaturesCol("features");

    DecisionTreeClassificationModel model = decisionTreeClassifier.fit(trainingData);
    Dataset<Row> predictions = model.transform(testData);

    // Select (prediction, true label) and compute test error.
    MulticlassClassificationEvaluator evaluator =
        new MulticlassClassificationEvaluator()
            .setLabelCol(labeledColumnName + "_indexed")
            .setPredictionCol("prediction")
            .setMetricName("accuracy");
    accuracy = evaluator.evaluate(predictions);

    indexedLabelsToActualValues =
        indexToString
            .toJavaRDD()
            .map(
                (Function<Row, Pair<String>>)
                    row -> new Pair<>(row.get(0).toString(), row.get(1).toString()))
            .collect()
            .stream()
            .collect(Collectors.toMap(Pair::getColumnA, Pair::getColumnB));

    // Create readable Visualization
    decisionTreeVisualization = model.toDebugString();
    for (int i = 0; i < featureColumnNames.length; ++i) {
      decisionTreeVisualization =
          decisionTreeVisualization.replace("feature " + i, featureColumnNames[i]);
    }
    for (Map.Entry<String, String> entry : indexedLabelsToActualValues.entrySet()) {
      decisionTreeVisualization =
          decisionTreeVisualization.replace(
              "Predict: " + entry.getKey(), "Predict: " + entry.getValue());
    }
  }

  @Override
  public String toString() {
    return "\nDecisionTree\n"
        + "featureColumnNames="
        + Arrays.toString(featureColumnNames)
        + "\n"
        + "indexedLabelsToActualValues="
        + indexedLabelsToActualValues
        + "\n"
        + "accuracy="
        + accuracy
        + "\n"
        + "decisionTreeVisualization='"
        + decisionTreeVisualization
        + "\n";
  }
}
