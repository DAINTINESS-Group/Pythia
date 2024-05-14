package gr.uoi.cs.pythia.report;

import gr.uoi.cs.pythia.engine.DatasetProfilerParameters;
import gr.uoi.cs.pythia.engine.IDatasetProfiler;
import gr.uoi.cs.pythia.engine.IDatasetProfilerFactory;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.clustering.ClusteringType;
import gr.uoi.cs.pythia.model.outlier.OutlierType;
import gr.uoi.cs.pythia.model.regression.RegressionType;
import gr.uoi.cs.pythia.patterns.dominance.DominanceColumnSelectionMode;
import gr.uoi.cs.pythia.regression.RegressionParameters;
import gr.uoi.cs.pythia.regression.RegressionRequest;
import gr.uoi.cs.pythia.testshelpers.TestsDatasetSchemas;
import gr.uoi.cs.pythia.testshelpers.TestsUtilities;
import gr.uoi.cs.pythia.util.HighlightParameters;
import gr.uoi.cs.pythia.util.HighlightParameters.HighlightExtractionMode;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.types.StructType;
import org.junit.rules.ExternalResource;

import java.io.IOException;
import java.util.Arrays;

public class ReportResource extends ExternalResource {

    private IDatasetProfiler datasetProfiler;
    private String absoluteDatasetPath;
    private String datasetPath;

    public IDatasetProfiler getDatasetProfiler() {
        return datasetProfiler;
    }

    public DatasetProfile getModelProfile(){
        return datasetProfiler.getDatasetProfile();
    }

    public String getAbsoluteDatasetPath() {
        return absoluteDatasetPath;
    }
    public String getDatasetPath() {
        return datasetPath;
    }

    @Override
    protected void before() throws Throwable {
        super.before();
        TestsUtilities.setupResultsDir("report");
        initializeProfile();
    }

    private void initializeProfile() throws AnalysisException, IOException {
        StructType schema = TestsDatasetSchemas.getPeopleJsonSchema();
        datasetProfiler = new IDatasetProfilerFactory().createDatasetProfiler();
        absoluteDatasetPath = TestsUtilities.getAbsoluteDatasetPath("people.json");
        datasetPath = TestsUtilities.getDatasetPath("people.json");
        datasetProfiler.registerDataset("people", absoluteDatasetPath, schema);
        datasetProfiler.declareOutlierParameters(OutlierType.Z_SCORE, 3.0);
        RegressionRequest regressionRequest = new RegressionRequest();
        regressionRequest.addRegression(new RegressionParameters(
        		null, "age", RegressionType.AUTOMATED, 0.05));
        datasetProfiler.declareRegressionRequest(regressionRequest);
        datasetProfiler.getDatasetProfile().getPatternsProfile().setOutlierType("Z Score");
		datasetProfiler.declareDominanceParameters(
				DominanceColumnSelectionMode.EXHAUSTIVE,
				null, null);
		
		datasetProfiler.declareClusteringParameters(ClusteringType.KMEANS, 3,
	    		Arrays.asList("name"));
		
		boolean shouldRunDescriptiveStats = true;
		boolean shouldRunHistograms = true;
		boolean shouldRunAllPairsCorrelations = true;
		boolean shouldRunDecisionTrees = true;
		boolean shouldRunDominancePatterns = true;
		boolean shouldRunOutlierDetection = false;
		boolean shouldRunRegression = true;
		boolean shouldRunClustering = true;
	    HighlightParameters highlightParameters = new HighlightParameters(HighlightExtractionMode.ALL, Double.MIN_VALUE);


		datasetProfiler.computeProfileOfDataset(
				new DatasetProfilerParameters(
						TestsUtilities.getResultsDir("report"), shouldRunDescriptiveStats,
						shouldRunHistograms, shouldRunAllPairsCorrelations,
						shouldRunDecisionTrees, shouldRunDominancePatterns,
						shouldRunOutlierDetection,  shouldRunRegression, shouldRunClustering, highlightParameters));

    }
    
}
