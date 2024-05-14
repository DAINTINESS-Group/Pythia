package gr.uoi.cs.pythia.correlations;

import java.io.IOException;

import org.apache.spark.sql.AnalysisException;
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

public class CorrelationsResource extends ExternalResource {

    private DatasetProfile datasetProfile;

    public DatasetProfile getDatasetProfile() {
        return datasetProfile;
    }

    @Override
    protected void before() throws Throwable {
        super.before();
        TestsUtilities.setupResultsDir("correlations");
        initializeProfile();
    }

    private void initializeProfile() throws AnalysisException, IOException {
        StructType schema = TestsDatasetSchemas.getPeopleJsonSchema();
        IDatasetProfiler datasetProfiler = new IDatasetProfilerFactory().createDatasetProfiler();
        String datasetPath = TestsUtilities.getAbsoluteDatasetPath("people.json");
        datasetProfiler.registerDataset("people", datasetPath, schema);
        
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
						TestsUtilities.getResultsDir("correlations"),
						shouldRunDescriptiveStats, shouldRunHistograms,
						shouldRunAllPairsCorrelations, shouldRunDecisionTrees,
						shouldRunDominancePatterns,
						shouldRunOutlierDetection,
						shouldRunRegression,
						shouldRunClustering,
						highlightParameters));
    }
    
}
