package gr.uoi.cs.pythia.decisiontree;

import gr.uoi.cs.pythia.testshelpers.TestsUtilities;
import gr.uoi.cs.pythia.testshelpers.TestsDatasetSchemas;
import gr.uoi.cs.pythia.decisiontree.generator.DecisionTreeGeneratorFactory;
import gr.uoi.cs.pythia.decisiontree.input.DecisionTreeParams;
import gr.uoi.cs.pythia.engine.IDatasetProfiler;
import gr.uoi.cs.pythia.engine.IDatasetProfilerFactory;
import gr.uoi.cs.pythia.labeling.LabelingSystemConstants;
import gr.uoi.cs.pythia.labeling.Rule;
import gr.uoi.cs.pythia.labeling.RuleSet;
import gr.uoi.cs.pythia.model.decisiontree.DecisionTree;
import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.StructType;
import org.junit.rules.ExternalResource;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DecisionTreeResource extends ExternalResource {

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
        TestsUtilities.setupResultsDir("decisiontree");
        initializeProfile();
    }

    private void initializeProfile() throws AnalysisException, IllegalAccessException {
        StructType schema = TestsDatasetSchemas.getCarseatsCsvSchema();
        IDatasetProfiler datasetProfiler = new IDatasetProfilerFactory().createDatasetProfiler();
        datasetProfiler.registerDataset("carseats", TestsUtilities.getDatasetPath("carseats.csv"), schema);
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
                .Builder(ruleSet.getNewColumnName(), getTargetColumns())
                .selectedFeatures(selectedFeatures)
                .trainingToTestDataSplitRatio(new double[]{1, 0})
                .build();
        return new DecisionTreeGeneratorFactory(decisionTreeParams, dataset)
                .getDefaultGenerator()
                .computeDecisionTree();
    }

    public List<String> getTargetColumns() {
        return ruleSet.getRules().stream()
                .map(Rule::getTargetColumnName)
                .collect(Collectors.toList());
    }
}
