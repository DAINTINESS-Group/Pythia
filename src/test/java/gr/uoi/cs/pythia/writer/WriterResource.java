package gr.uoi.cs.pythia.writer;

import gr.uoi.cs.pythia.testshelpers.TestsUtilities;
import gr.uoi.cs.pythia.config.SparkConfig;
import gr.uoi.cs.pythia.engine.IDatasetProfiler;
import gr.uoi.cs.pythia.engine.IDatasetProfilerFactory;
import gr.uoi.cs.pythia.testshelpers.TestsDatasetSchemas;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;
import org.junit.rules.ExternalResource;

public class WriterResource extends ExternalResource {
    private SparkSession sparkSession;
    private IDatasetProfiler datasetProfiler;

    public SparkSession getSparkSession() {
        return sparkSession;
    }

    public IDatasetProfiler getDatasetProfiler() {
        return datasetProfiler;
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

    private void initializeProfile() throws AnalysisException {
        String filePath = TestsUtilities.getDatasetPath("people.json");
        StructType schema = TestsDatasetSchemas.getPeopleJsonSchema();
        datasetProfiler = new IDatasetProfilerFactory().createDatasetProfiler();
        datasetProfiler.registerDataset("people", filePath, schema);
    }

    @Override
    protected void after() {
        super.after();
        sparkSession.stop();
    }
}
