package gr.uoi.cs.pythia.clustering;

import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.StructType;
import org.junit.rules.ExternalResource;

import gr.uoi.cs.pythia.engine.DatasetProfilerParameters;
import gr.uoi.cs.pythia.engine.IDatasetProfiler;
import gr.uoi.cs.pythia.engine.IDatasetProfilerFactory;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.testshelpers.TestsDatasetSchemas;
import gr.uoi.cs.pythia.testshelpers.TestsUtilities;
import gr.uoi.cs.pythia.util.HighlightParameters;
import gr.uoi.cs.pythia.util.HighlightParameters.HighlightExtractionMode;

public class ClusteringResource extends ExternalResource{

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
        TestsUtilities.setupResultsDir("clustering");
        initializeProfile();
    }
    
    private void initializeProfile() throws AnalysisException, IOException, IllegalAccessException {
    	StructType schema = TestsDatasetSchemas.getCarsCsvSchema();
        IDatasetProfiler datasetProfiler = new IDatasetProfilerFactory().createDatasetProfiler();
        String datasetPath = TestsUtilities.getDatasetPath("cars_10.csv");
        datasetProfiler.registerDataset("cars", datasetPath, schema);
        // Get dataset
        Field datasetField = FieldUtils.getField(datasetProfiler.getClass(), "dataset", true);
        dataset = (Dataset<Row>) datasetField.get(datasetProfiler);
		boolean shouldRunDescriptiveStats = true;
		boolean shouldRunHistograms = false;
		boolean shouldRunAllPairsCorrelations = true;
		boolean shouldRunDecisionTrees = false;
		boolean shouldRunDominancePatterns = false;
		boolean shouldRunOutlierDetection = false;
		boolean shouldRunRegression = false;
		boolean shouldRunClustering = false;
	    HighlightParameters highlightParameters = new HighlightParameters(HighlightExtractionMode.NONE, Double.MAX_VALUE);

		datasetProfile = datasetProfiler.computeProfileOfDataset(
				new DatasetProfilerParameters(
						TestsUtilities.getResultsDir("clustering"),
						shouldRunDescriptiveStats, shouldRunHistograms,
						shouldRunAllPairsCorrelations, shouldRunDecisionTrees,
						shouldRunDominancePatterns, shouldRunOutlierDetection,
						shouldRunRegression, shouldRunClustering, highlightParameters));
    }
}
