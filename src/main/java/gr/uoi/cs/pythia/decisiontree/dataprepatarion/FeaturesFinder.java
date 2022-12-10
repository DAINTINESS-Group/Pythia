package gr.uoi.cs.pythia.decisiontree.dataprepatarion;

import org.apache.commons.collections.ListUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;

import java.util.*;
import java.util.stream.Collectors;

public class FeaturesFinder {

    private final Dataset<Row> dataset;
    private final DecisionTreeParams decisionTreeParams;
    private List<String> numericalFeatures = new ArrayList<>();
    private List<String> categoricalFeatures = new ArrayList<>();
    private List<String> categoricalFeaturesIndexed = new ArrayList<>();

    public FeaturesFinder(DecisionTreeParams decisionTreeParams, Dataset<Row> dataset) {
        this.decisionTreeParams = decisionTreeParams;
        this.dataset = dataset;
        discernFeatures();
    }

    public List<String> getCategoricalFeatures() {
        return categoricalFeatures;
    }

    public List<String> getAllFeatures() {
        return ListUtils.union(numericalFeatures, categoricalFeatures);
    }

    public List<String> getAllFeaturesIndexed() {
        return ListUtils.union(numericalFeatures, categoricalFeaturesIndexed);
    }

    public void discernFeatures() {

        findNumericalFeatures();
        findCategoricalFeatures();
    }

    private void findNumericalFeatures() {
        Set<DataType> numericDatatypes = new HashSet<>(
                Arrays.asList(
                        DataTypes.ByteType,
                        DataTypes.ShortType,
                        DataTypes.IntegerType,
                        DataTypes.LongType,
                        DataTypes.FloatType,
                        DataTypes.DoubleType,
                        DataTypes.createDecimalType()));

        numericalFeatures = Arrays.stream(dataset.schema().fields())
                .filter(field ->
                        numericDatatypes.contains(field.dataType())
                        && featureIsValid(field.name())
                        && featureIsSelected(field.name()))
                .map(StructField::name).collect(Collectors.toList());
    }

    private void findCategoricalFeatures() {
        categoricalFeatures = Arrays.stream(dataset.schema().fields())
                .filter(field ->
                        field.dataType() == DataTypes.StringType
                        && featureIsValid(field.name())
                        && featureIsSelected(field.name()))
                .map(StructField::name).collect(Collectors.toList());
        categoricalFeaturesIndexed = categoricalFeatures.stream()
                .map(s -> s + "_indexed")
                .collect(Collectors.toList());
    }

    private boolean featureIsValid(String feature) {
        return !decisionTreeParams.getNotValidFeatures().contains(feature) &&
                !feature.equals(decisionTreeParams.getLabeledColumnName());
    }

    private boolean featureIsSelected(String feature) {
        return  decisionTreeParams.getSelectedFeatures().isEmpty() ||
                decisionTreeParams.getSelectedFeatures().contains(feature);
    }
}
