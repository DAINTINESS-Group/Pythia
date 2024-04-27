package gr.uoi.cs.pythia.generalinfo;

import gr.uoi.cs.pythia.config.SparkConfig;
import gr.uoi.cs.pythia.reader.IDatasetReaderFactory;
import gr.uoi.cs.pythia.testshelpers.TestsDatasetSchemas;
import gr.uoi.cs.pythia.testshelpers.TestsUtilities;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;
import org.junit.rules.ExternalResource;

public class GenInfoResource extends ExternalResource {

    private Dataset<Row> dataset;
    private SparkSession session;
    private String datasetPath;
    private SparkInfoCalculator sparkInfoCalculator;



    @Override
    protected void before() throws Throwable {
        super.before();

        initializeSparkSession();
        initializeDatasetWithReadTask();
        createBasicInfoSparkManager();
    }

    private void createBasicInfoSparkManager() {
        sparkInfoCalculator = new SparkInfoCalculator(dataset,session,datasetPath);
    }

    private void initializeSparkSession() {
        SparkConfig sparkConfig = new SparkConfig();
        session = SparkSession.builder().appName(sparkConfig.getAppName()).master(sparkConfig.getMaster()).config("spark.sql.warehouse.dir", sparkConfig.getSparkWarehouse()).getOrCreate();

    }

    private void initializeDatasetWithReadTask() throws AnalysisException {
        IDatasetReaderFactory dataFrameReaderFactory = new IDatasetReaderFactory(session);
        datasetPath = TestsUtilities.getDatasetPath("carsEdit100kMore.csv");
        StructType schema = TestsDatasetSchemas.getCarsCsvSchema();
        dataset = dataFrameReaderFactory.createDataframeReader(datasetPath, schema).read();
    }


    public Dataset<Row> getDataset() {
        return dataset;
    }
    public SparkSession getSession() {
        return session;
    }
    public String getDatasetPath() {
        return datasetPath;
    }
    public SparkInfoCalculator getBasicInfoSparkManager() {
        return sparkInfoCalculator;
    }


}
