package gr.uoi.cs.pythia.report;

import java.io.IOException;

import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.types.StructType;
import org.junit.rules.ExternalResource;

import gr.uoi.cs.pythia.engine.DatasetProfilerParameters;
import gr.uoi.cs.pythia.engine.IDatasetProfiler;
import gr.uoi.cs.pythia.engine.IDatasetProfilerFactory;
import gr.uoi.cs.pythia.model.outlier.OutlierType;
import gr.uoi.cs.pythia.patterns.dominance.DominanceColumnSelectionMode;
import gr.uoi.cs.pythia.testshelpers.TestsDatasetSchemas;
import gr.uoi.cs.pythia.testshelpers.TestsUtilities;

public class ReportResource extends ExternalResource {

    private IDatasetProfiler datasetProfiler;
    private String datasetPath;

    public IDatasetProfiler getDatasetProfiler() {
        return datasetProfiler;
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
        datasetPath = TestsUtilities.getDatasetPath("people.json");
        datasetProfiler.registerDataset("people", datasetPath, schema);
        datasetProfiler.declareOutlierParameters(OutlierType.Z_SCORE, 3.0);
//        datasetProfiler.setOutlierThreshold(3.0);
//        datasetProfiler.setOutlierType(OutlierType.Z_SCORE);
        datasetProfiler.getDatasetProfile().getPatternsProfile().setOutlierType("Z Score");
		datasetProfiler.declareDominanceParameters(
				DominanceColumnSelectionMode.EXHAUSTIVE,
				null, null);
		
		boolean shouldRunDescriptiveStats = true;
		boolean shouldRunHistograms = true;
		boolean shouldRunAllPairsCorrelations = true;
		boolean shouldRunDecisionTrees = true;
		boolean shouldRunDominancePatterns = true;
		boolean shouldRunOutlierDetection = false;

		datasetProfiler.computeProfileOfDataset(
				new DatasetProfilerParameters(
						TestsUtilities.getResultsDir("report"), shouldRunDescriptiveStats,
						shouldRunHistograms, shouldRunAllPairsCorrelations,
						shouldRunDecisionTrees, shouldRunDominancePatterns,
						shouldRunOutlierDetection));
    }
    
}
