package gr.uoi.cs.pythia.writer;

import gr.uoi.cs.pythia.testshelpers.TestsUtilities;
import gr.uoi.cs.pythia.engine.IDatasetProfiler;
import gr.uoi.cs.pythia.engine.IDatasetProfilerFactory;
import gr.uoi.cs.pythia.testshelpers.TestsDatasetSchemas;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.types.StructType;
import org.junit.rules.ExternalResource;

public class WriterResource extends ExternalResource {

    private IDatasetProfiler datasetProfiler;

    public IDatasetProfiler getDatasetProfiler() {
        return datasetProfiler;
    }

    @Override
    protected void before() throws Throwable {
        super.before();
        TestsUtilities.setupResultsDir("writer");
        initializeProfile();
    }

    private void initializeProfile() throws AnalysisException {
        String filePath = TestsUtilities.getDatasetPath("people.json");
        StructType schema = TestsDatasetSchemas.getPeopleJsonSchema();
        datasetProfiler = new IDatasetProfilerFactory().createDatasetProfiler();
        datasetProfiler.registerDataset("people", filePath, schema);
    }
}
