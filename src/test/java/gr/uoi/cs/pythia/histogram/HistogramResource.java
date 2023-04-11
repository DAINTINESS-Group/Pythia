package gr.uoi.cs.pythia.histogram;

import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.testshelpers.TestsUtilities;
import gr.uoi.cs.pythia.engine.DatasetProfilerExecParameters;
import gr.uoi.cs.pythia.engine.IDatasetProfiler;
import gr.uoi.cs.pythia.engine.IDatasetProfilerFactory;
import gr.uoi.cs.pythia.testshelpers.TestsDatasetSchemas;
import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.StructType;
import org.junit.rules.ExternalResource;

import java.io.IOException;
import java.lang.reflect.Field;

public class HistogramResource extends ExternalResource {

    private Dataset<Row> dataset;
    private DatasetProfile datasetProfile;

    public Dataset<Row> getDataset() {
        return dataset;
    }

    public DatasetProfile getDatasetProfile() {
        return datasetProfile;
    }

    @Override
    protected void before() throws Throwable {
        super.before();
        TestsUtilities.setupResultsDir("histogram");
        initializeProfile();
    }

    private void initializeProfile() throws AnalysisException, IllegalAccessException, IOException {
        StructType schema = TestsDatasetSchemas.getBreastCsvSchema();
        IDatasetProfiler datasetProfiler = new IDatasetProfilerFactory().createDatasetProfiler();
        datasetProfiler.registerDataset("breast-w", TestsUtilities.getDatasetPath("breast-w.csv"), schema);
        // Get dataset
        Field datasetField = FieldUtils.getField(datasetProfiler.getClass(), "dataset", true);
        dataset = (Dataset<Row>) datasetField.get(datasetProfiler);
        //datasetProfile = datasetProfiler.computeProfileOfDataset(TestsUtilities.getResultsDir("histogram"));
        boolean shouldRunDescriptiveStats = true; boolean shouldRunHistograms = true;
    	boolean shouldRunAllPairsCorrelations= true; boolean shouldRunDecisionTrees= true; boolean shouldRunHighlightPatterns = false;

    	datasetProfile = datasetProfiler.computeProfileOfDataset(new DatasetProfilerExecParameters(TestsUtilities.getResultsDir("histogram"), shouldRunDescriptiveStats,  
        		 shouldRunHistograms, shouldRunAllPairsCorrelations,  shouldRunDecisionTrees,  shouldRunHighlightPatterns));

    }
}
