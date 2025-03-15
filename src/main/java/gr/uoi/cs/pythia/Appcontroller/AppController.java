package gr.uoi.cs.pythia.Appcontroller;

import gr.uoi.cs.pythia.clustering.ClusteringParameters;
import gr.uoi.cs.pythia.correlations.CorrelationsParameters;
import gr.uoi.cs.pythia.datatypeIdentifier.ScoreCalculatorManager;
import gr.uoi.cs.pythia.engine.DatasetProfiler;
import gr.uoi.cs.pythia.engine.DatasetProfilerParameters;
import gr.uoi.cs.pythia.engine.IDatasetProfiler;
import gr.uoi.cs.pythia.histogram.generator.HistogramParameters;
import gr.uoi.cs.pythia.labeling.RuleSet;
import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.outliers.OutlierParameters;
import gr.uoi.cs.pythia.patterns.dominance.DominanceParameters;
import gr.uoi.cs.pythia.regression.RegressionRequest;
import gr.uoi.cs.pythia.report.ReportParameters;
import gr.uoi.cs.pythia.writer.DatasetWriterParameters;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.types.*;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class AppController{

    private IDatasetProfiler profiler;
    private DatasetProfilerParameters parameters;
    private DatasetProfile datasetProfile;
    private static AppController appController;
    private Dataset<Row> dataset;
    private final HashMap<String, DataType> selectedDataTypes = new HashMap<>();
    String pathSchema;
    String auxiliaryPath = null;

    public AppController(){
        profiler = new DatasetProfiler();
    }

    public static synchronized AppController getInstance(){
        if(appController==null)
            appController = new AppController();
        return appController;
    }


    public void registerDataset(String alias, String path, String loadSchema) throws AnalysisException, IOException{
        Objects.requireNonNull(alias, "Dataset alias cannot be null");
        Objects.requireNonNull(path, "Dataset path cannot be null");
        Objects.requireNonNull(loadSchema, "Dataset schema path cannot be null");
        if(alias.isEmpty()){
            throw new IllegalArgumentException("Dataset alias cannot be empty");
        }
        if(path.isEmpty()){
            throw new IllegalArgumentException("Dataset path cannot be empty");
        }
        if(loadSchema.isEmpty()){
            throw new IllegalArgumentException("Dataset schema path cannot be empty");
        }
        pathSchema = loadSchema;
        StructType schema = readFile();
        profiler.registerDataset(alias, path, schema);
        datasetProfile = profiler.getDatasetProfile();
        dataset = profiler.getDataset();

    }

    public void registerDataset(String alias, String path) throws AnalysisException{
        Objects.requireNonNull(alias, "Dataset alias cannot be null");
        Objects.requireNonNull(path, "Dataset path cannot be null");
        if(alias.isEmpty()){
            throw new IllegalArgumentException("Dataset alias cannot be empty");
        }
        if(path.isEmpty()){
            throw new IllegalArgumentException("Dataset path cannot be empty");
        }
        profiler.registerDataset(alias, path);
        datasetProfile = profiler.getDatasetProfile();

    }

    public void updateSchema() throws IOException{
        StructType schema = readFile();
        dataset = profiler.getDataset();
        for(String columnName : dataset.columns()){
            StructField newField = Arrays.stream(schema.fields())
                    .filter(f->f.name().equals(columnName))
                    .findFirst()
                    .orElse(null);

            if(newField!=null){
                DataType fieldType = newField.dataType();
                System.out.println(fieldType.toString()+" "+columnName);

                try {
                    if(fieldType instanceof DecimalType){
                        DataType targetType = findMaxScale(columnName);
                        dataset = dataset.withColumn(columnName, dataset.col(columnName).cast(targetType));
                        updateColumnType(columnName, targetType);
                    } else if(fieldType instanceof BooleanType){
                        dataset = dataset.withColumn(columnName, functions.when(
                                        functions.lower(functions.col(columnName)).isin("true", "1", "yes", "on", "enabled", "ok"), true)
                                .when(functions.lower(functions.col(columnName)).isin("false", "0", "no", "off", "disabled"), false)
                                .otherwise(null));
                        updateColumnType(columnName, DataTypes.BooleanType);
                    } else {
                        dataset = dataset.withColumn(columnName, dataset.col(columnName).cast(fieldType));
                        updateColumnType(columnName, fieldType);
                    }
                } catch (Exception e) {
                    System.err.println("Error casting column "+columnName+" to type "+fieldType+": "+e.getMessage());
                    dataset = dataset.withColumn(columnName, dataset.col(columnName).cast(DataTypes.StringType));
                    updateColumnType(columnName, DataTypes.StringType);
                }
            } else {
                dataset = dataset.withColumn(columnName, dataset.col(columnName).cast(DataTypes.StringType));
                updateColumnType(columnName, DataTypes.StringType);
            }
        }
        dataset.cache().count();
        profiler.setDataset(dataset);
    }

    private void updateColumnType(String columnName, DataType targetType){
        if(profiler.getDatasetProfile()!=null){
            List<Column> columnList = profiler.getDatasetProfile().getColumns();
            for(Column column : columnList){
                if(column.getName().equals(columnName)){
                    column.setDatatype(targetType.toString());
                    break;
                }
            }
            profiler.getDatasetProfile().setColumns(columnList);
        }
    }

    private DataType findMaxScale(String columnName){
        Row result = dataset.select(
                        functions.max(functions.length(
                                functions.expr("split("+columnName+", '\\\\.')[1]"))
                        ).alias("max_scale"))
                .collectAsList().get(0);

        int maxScale = result.isNullAt(0)?0:result.getInt(0);
        maxScale = (maxScale==0)?0:Math.min(maxScale, 18);
        return DataTypes.createDecimalType(38, maxScale);
    }

    public StructType readFile() throws IOException{
        List<String> lines = Files.readAllLines(Paths.get(pathSchema), StandardCharsets.UTF_8);
        StringBuilder jsonStringBuilder = new StringBuilder();
        for(String line : lines){
            jsonStringBuilder.append(line);
        }
        String jsonString = jsonStringBuilder.toString();
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray fieldsArray = jsonObject.getJSONArray("fields"); // Assuming "fields" is the key
        StructField[] fields = new StructField[fieldsArray.length()];
        for(int i = 0; i < fieldsArray.length(); i++){
            JSONObject fieldObject = fieldsArray.getJSONObject(i);
            String name = fieldObject.getString("name");
            String type = fieldObject.getString("type");
            boolean nullable = fieldObject.optBoolean("nullable", true); // Default to true if not present

            DataType dataType = getDataType(type);
            fields[i] = new StructField(name, dataType, nullable, Metadata.empty());
        }
        return new StructType(fields);
    }

    private DataType getDataType(String type){
        switch (type) {
            case "StringType":
                return DataTypes.StringType;
            case "IntegerType":
                return DataTypes.IntegerType;
            case "LongType":
                return DataTypes.LongType;
            case "DoubleType":
                return DataTypes.DoubleType;
            case "BooleanType":
                return DataTypes.BooleanType;
            case "FloatType":
                return DataTypes.FloatType;
            case "ShortType":
                return DataTypes.ShortType;
            case "ByteType":
                return DataTypes.ByteType;
            case "DateType":
                return DataTypes.DateType;
            case "TimestampType":
                return DataTypes.TimestampType;
            case "DecimalType(10,0)":
                return DataTypes.createDecimalType();

            default:
                throw new IllegalArgumentException("Unsupported type: "+type);
        }
    }

    public void computeLabeledColumn(RuleSet ruleSet){
        // Check if the RuleSet is null
        Objects.requireNonNull(ruleSet, "RuleSet cannot be null");
        if(datasetProfile==null){
            showErrorMessage("Dataset profile is null. Cannot compute labeled column.");
            return;
        }
        // If all column names are valid, proceed with computing the labeled column
        profiler.computeLabeledColumn(ruleSet);
    }

    public void declareDominanceParameters(DominanceParameters dominanceParameters){
        // Check if DominanceParameters is null
        Objects.requireNonNull(dominanceParameters, "DominanceParameters cannot be null");
        // Check if column selection mode is null
        Objects.requireNonNull(dominanceParameters.getColumnSelectionMode(), "Column selection mode cannot be null");
        // Check if dataset profile is null
        if(datasetProfile==null){
            showErrorMessage("Dataset profile is null. Cannot declare dominance parameters.");
            return;
        }

        // If all checks pass, declare the dominance parameters
        profiler.declareDominanceParameters(
                dominanceParameters.getColumnSelectionMode(),
                dominanceParameters.getMeasurementColumns(),
                dominanceParameters.getCoordinateColumns());
    }

    public void createDatasetProfileParameters(DatasetProfilerParameters parameters){
        Objects.requireNonNull(parameters, "DatasetProfilerParameters cannot be null");
        this.parameters = parameters;
        this.auxiliaryPath = parameters.getAuxiliaryDataOutputDirectory();
    }

    public void computeProfileOfDataset() throws IOException{
        Objects.requireNonNull(parameters, "DatasetProfilerParameters cannot be null. Call createDatasetProfileParameters first.");

        profiler.computeProfileOfDataset(this.parameters);
        this.datasetProfile = profiler.getDatasetProfile();

        if(datasetProfile==null){
            showErrorMessage("Dataset profile computation failed. DatasetProfile is null.");
        }
    }

    public DatasetProfile getDatasetProfile(){
        if(datasetProfile==null){
            return profiler.getDatasetProfile(); ///
        }
        return this.datasetProfile;
    }

    public Dataset<Row> getDataset(){
        if(profiler.getDataset()==null){ //For Testing
            return dataset;
        }
        return profiler.getDataset();
    }

    private void showErrorMessage(String message){
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }


    public void declareClusteringParameters(ClusteringParameters clusteringParameters){
        // Check if ClusteringParameters is null
        Objects.requireNonNull(clusteringParameters, "ClusteringParameters cannot be null");

        // Check if dataset profile is null
        if(datasetProfile==null){
            showErrorMessage("Dataset profile is null. Cannot declare clustering parameters.");
            return;
        }
        // If all checks pass, declare the clustering parameters
        profiler.declareClusteringParameters(
                clusteringParameters.getType(),
                clusteringParameters.getNumOfClusters(),
                clusteringParameters.getSelectedFeatures()
        );
    }

    public void declareCorrelationsParameters(CorrelationsParameters correlationsParameters){
        profiler.declareCorrelationsParameters(correlationsParameters.method);
    }

    public void writeDataset(DatasetWriterParameters datasetWriterParameters){
        try {
            profiler.writeDataset(datasetWriterParameters.writerType, datasetWriterParameters.path);
        } catch (IOException e) {
            showErrorMessage("Error writing dataset: "+e.getMessage());
        }
    }

    public void declareHistogramParameters(HistogramParameters histogramParameters){
        profiler.declareHistogramParameters(histogramParameters);
    }

    public void declareOutlierParameters(OutlierParameters outlierParameters){
        profiler.declareOutlierParameters(outlierParameters.type, outlierParameters.threshold);
    }

    public void generateReport(ReportParameters reportParameters){
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>(){
            @Override
            protected Void doInBackground(){
                try {
                    profiler.generateReport(reportParameters.type, reportParameters.path);
                } catch (IOException e) {
                    throw new RuntimeException("Error generating report: "+e.getMessage(), e);
                }
                return null;
            }

            @Override
            protected void done(){
                try {
                    get();
                } catch (InterruptedException | ExecutionException ignored) {

                }
            }
        };
        worker.execute();
    }

    public void onDataTypeSelected(DataType type, String columnName){
        selectedDataTypes.put(columnName, type);
    }

    public void declareRegressionRequest(RegressionRequest request){
        profiler.declareRegressionRequest(request);
    }

    public DatasetProfilerParameters getParameters(){
        return parameters;

    }

    private LinkedHashMap<String, DataType> createMapColumnType() {
        LinkedHashMap<String, DataType> map = new LinkedHashMap<>();
        // By default, set all columns to StringType
        for (StructField column : profiler.getDataset().schema().fields()) {
            map.put(column.name(), DataTypes.StringType);
        }
        return map;
    }

    public void writeSchemaFile(String path) {
        pathSchema = path + "//schema.json"; // Define the schema file path

        try (FileWriter file = new FileWriter(pathSchema)) {
            JSONObject schema = new JSONObject();
            schema.put("type", "struct");

            JSONArray fields = new JSONArray();

            // Add all data types to the schema, modifying values if they exist in selectedDataTypes
            LinkedHashMap<String, DataType> allDataType = createMapColumnType();
            addFieldsToSchema(fields, allDataType, selectedDataTypes);

            schema.put("fields", fields);

            // Write the schema to the file with pretty printing
            file.write(schema.toString(2));
            file.flush(); // Ensure all data is written to the file

            selectedDataTypes.clear(); // Clear the selected data types after writing
        } catch (IOException e) {
            System.out.println("Error in write: " + e.getMessage());
        }
    }


    private void addFieldsToSchema(JSONArray fields, Map<String, DataType> dataTypeMap, Map<String, DataType> excludeMap){
        for(Map.Entry<String, DataType> entry : dataTypeMap.entrySet()){
            String key = entry.getKey();
            DataType value = entry.getValue();

            // If the key exists in the excludeMap, modify the value
            if(excludeMap.containsKey(key)){
                value = excludeMap.get(key); // Use the modified value from excludeMap
            }

            JSONObject field = new JSONObject();
            field.put("name", key);
            field.put("type", value);
            field.put("nullable", true); // You can change this if needed
            fields.put(field);
        }
    }

    public ScoreCalculatorManager getScoreCalculatorManager(){
        if(profiler.getScoreCalculatorManager()==null){
            ScoreCalculatorManager manager = new ScoreCalculatorManager(dataset);
            manager.calculateScores();
            return manager;
        }
        return profiler.getScoreCalculatorManager();
    }

    public String getAuxiliaryPath(){
        return auxiliaryPath;
    }


    public void setProfiler(IDatasetProfiler datasetProfiler){
        this.profiler = datasetProfiler;
    }
}