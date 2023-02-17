package gr.uoi.cs.pythia.report;

import gr.uoi.cs.pythia.testshelpers.TestsUtilities;
import gr.uoi.cs.pythia.engine.IDatasetProfiler;
import gr.uoi.cs.pythia.engine.IDatasetProfilerFactory;
import gr.uoi.cs.pythia.testshelpers.TestsDatasetSchemas;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.types.StructType;
import org.junit.rules.ExternalResource;

import java.io.IOException;

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
        datasetProfiler.computeProfileOfDataset(TestsUtilities.getResultsDir("report"));
    }
}
