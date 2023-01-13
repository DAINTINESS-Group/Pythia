package gr.uoi.cs.pythia.writer;

import gr.uoi.cs.pythia.config.SparkConfig;
import gr.uoi.cs.pythia.engine.IDatasetProfiler;
import gr.uoi.cs.pythia.engine.IDatasetProfilerFactory;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.junit.rules.ExternalResource;

import java.io.File;
import java.util.Objects;

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
        StructType schema =
                new StructType(
                        new StructField[] {
                                new StructField("name", DataTypes.StringType, false, Metadata.empty()),
                                new StructField("age", DataTypes.IntegerType, false, Metadata.empty()),
                                new StructField("money", DataTypes.IntegerType, false, Metadata.empty()),
                        });

        datasetProfiler = new IDatasetProfilerFactory().createDatasetProfiler();
        datasetProfiler.registerDataset("people", getResource("people.json").getAbsolutePath(), schema);
    }

    private File getResource(String resourceName) {
        return new File(
                Objects.requireNonNull(getClass().getClassLoader().getResource(resourceName)).getFile());
    }

    @Override
    protected void after() {
        super.after();
        sparkSession.stop();
    }
}
