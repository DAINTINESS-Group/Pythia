package gr.uoi.cs.pythia.correlations;

import gr.uoi.cs.pythia.engine.IDatasetProfiler;
import gr.uoi.cs.pythia.engine.IDatasetProfilerFactory;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.testshelpers.TestsDatasetSchemas;
import gr.uoi.cs.pythia.testshelpers.TestsUtilities;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.types.StructType;
import org.junit.rules.ExternalResource;

import java.io.IOException;

public class CorrelationsResource extends ExternalResource {

    private DatasetProfile datasetProfile;

    public DatasetProfile getDatasetProfile() {
        return datasetProfile;
    }

    @Override
    protected void before() throws Throwable {
        super.before();
        initializeProfile();
    }

    private void initializeProfile() throws AnalysisException, IOException {
        StructType schema = TestsDatasetSchemas.getPeopleJsonSchema();
        IDatasetProfiler datasetProfiler = new IDatasetProfilerFactory().createDatasetProfiler();
        String datasetPath = TestsUtilities.getDatasetPath("people.json");
        datasetProfiler.registerDataset("people", datasetPath, schema);
        datasetProfile = datasetProfiler.computeProfileOfDataset("");
    }
}
