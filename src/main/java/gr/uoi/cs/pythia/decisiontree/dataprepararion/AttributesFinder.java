package gr.uoi.cs.pythia.decisiontree.dataprepararion;

import gr.uoi.cs.pythia.decisiontree.input.DecisionTreeParams;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;

import java.util.*;
import java.util.stream.Collectors;

public class AttributesFinder {

    private final Dataset<Row> dataset;
    private final DecisionTreeParams decisionTreeParams;
    private List<String> numericalFeatures = new ArrayList<>();
    private List<String> categoricalFeatures = new ArrayList<>();
    private List<String> categoricalFeaturesIndexed = new ArrayList<>();
    private List<String> nonGeneratingAttributes = new ArrayList<>();

    public AttributesFinder(DecisionTreeParams decisionTreeParams, Dataset<Row> dataset) {
        this.decisionTreeParams = decisionTreeParams;
        this.dataset = dataset;
        discernFeatures();
    }

    public List<String> getNonGeneratingAttributes() {
        return nonGeneratingAttributes;
    }

    public List<String> getCategoricalFeatures() {
        return categoricalFeatures;
    }

    public List<String> getAllFeatures() {
        List<String> allFeatures = new ArrayList<>();
        allFeatures.addAll(numericalFeatures);
        allFeatures.addAll(categoricalFeatures);
        return allFeatures;
    }

    public List<String> getAllFeaturesIndexed() {
        List<String> allFeatures = new ArrayList<>();
        allFeatures.addAll(numericalFeatures);
        allFeatures.addAll(categoricalFeaturesIndexed);
        return allFeatures;
    }

    private void discernFeatures() {
        findNumericalFeatures();
        findCategoricalFeatures();
        findNonGeneratingAttributes();
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
        return !decisionTreeParams.getNonGeneratorAttributes().contains(feature) &&
                !feature.equals(decisionTreeParams.getLabeledColumnName());
    }

    private boolean featureIsSelected(String feature) {
        return  decisionTreeParams.getSelectedFeatures().isEmpty() ||
                decisionTreeParams.getSelectedFeatures().contains(feature);
    }

    private void findNonGeneratingAttributes() {
        nonGeneratingAttributes = Arrays.stream(dataset.schema().fields())
                .filter(field ->
                        !numericalFeatures.contains(field.name()) &&
                        !categoricalFeatures.contains(field.name())
//                        // uncomment if we want the labeled column to not be included
//                        && !field.name().equals(decisionTreeParams.getLabeledColumnName())
//                        // uncomment if we want the target column names to not be included
//                        && !decisionTreeParams.getNonGeneratorAttributes().contains(field.name()
                )
                .map(StructField::name)
                .collect(Collectors.toList());
    }
}
