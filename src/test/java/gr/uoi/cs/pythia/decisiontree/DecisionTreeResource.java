package gr.uoi.cs.pythia.decisiontree;

import gr.uoi.cs.pythia.config.SparkConfig;
import gr.uoi.cs.pythia.decisiontree.engine.DecisionTreeEngineFactory;
import gr.uoi.cs.pythia.decisiontree.input.DecisionTreeParams;
import gr.uoi.cs.pythia.decisiontree.model.DecisionTree;
import gr.uoi.cs.pythia.engine.IDatasetProfiler;
import gr.uoi.cs.pythia.engine.IDatasetProfilerFactory;
import gr.uoi.cs.pythia.labeling.LabelingSystemConstants;
import gr.uoi.cs.pythia.labeling.Rule;
import gr.uoi.cs.pythia.labeling.RuleSet;
import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.junit.rules.ExternalResource;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DecisionTreeResource extends ExternalResource {
    private static SparkSession sparkSession;
    private RuleSet ruleSet;

    private Dataset<Row> dataset;

    public RuleSet getRuleSet() {
        return ruleSet;
    }

    public Dataset<Row> getDataset() {
        return dataset;
    }

    @Override
    protected void before() throws Throwable {
        super.before();
        initializeSpark();
        initializeProfile();
    }

    private void initializeSpark() {
        SparkConfig sparkConfig = new SparkConfig();
        sparkSession =
                SparkSession.builder()
                        .appName(sparkConfig.getAppName())
                        .master(sparkConfig.getMaster())
                        .config("spark.sql.warehouse.dir", sparkConfig.getSparkWarehouse())
                        .getOrCreate();
    }

    private void initializeProfile() throws AnalysisException, IllegalAccessException {
        StructType schema =
                new StructType(
                        new StructField[] {
                                new StructField("Sales", DataTypes.DoubleType, false, Metadata.empty()),
                                new StructField("CompPrice", DataTypes.IntegerType, false, Metadata.empty()),
                                new StructField("Income", DataTypes.IntegerType, false, Metadata.empty()),
                                new StructField("Advertising", DataTypes.IntegerType, false, Metadata.empty()),
                                new StructField("Population", DataTypes.IntegerType, false, Metadata.empty()),
                                new StructField("Price", DataTypes.IntegerType, false, Metadata.empty()),
                                new StructField("ShelveLoc", DataTypes.StringType, false, Metadata.empty()),
                                new StructField("Age", DataTypes.IntegerType, false, Metadata.empty()),
                                new StructField("Education", DataTypes.IntegerType, false, Metadata.empty()),
                                new StructField("Urban", DataTypes.StringType, false, Metadata.empty()),
                                new StructField("US", DataTypes.StringType, false, Metadata.empty())
                        });
        IDatasetProfiler datasetProfiler = new IDatasetProfilerFactory().createDatasetProfiler();
        datasetProfiler.registerDataset("carseats",
                new File(
                        Objects.requireNonNull(getClass().getClassLoader().getResource("carseats.csv"))
                                .getFile()).getAbsolutePath(),
                schema);
        // Get rules
        List<Rule> rules = new ArrayList<>();
        rules.add(new Rule("Sales", LabelingSystemConstants.LEQ, 3, "low"));
        rules.add(new Rule("Sales", LabelingSystemConstants.LEQ, 9, "mid"));
        rules.add(new Rule("Sales", LabelingSystemConstants.GT, 9, "high"));
        ruleSet = new RuleSet("Sales_labeled", rules);
        datasetProfiler.computeLabeledColumn(ruleSet);
        // Get dataset
        Field datasetField = FieldUtils.getField(datasetProfiler.getClass(), "dataset", true);
        dataset = (Dataset<Row>) datasetField.get(datasetProfiler);
    }

    public DecisionTree getDecisionTree(List<String> selectedFeatures) {
        DecisionTreeParams decisionTreeParams = new DecisionTreeParams
                .Builder(ruleSet.getNewColumnName(), ruleSet.getTargetColumns())
                .selectedFeatures(selectedFeatures)
                .trainingToTestDataSplitRatio(new double[]{1, 0})
                .build();
        return new DecisionTreeEngineFactory(decisionTreeParams, dataset)
                .getDefaultEngine()
                .computeDecisionTree();
    }

    @Override
    protected void after() {
        super.after();
        sparkSession.stop();
    }
}
