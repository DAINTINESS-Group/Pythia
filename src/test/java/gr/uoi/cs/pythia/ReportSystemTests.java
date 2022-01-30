package gr.uoi.cs.pythia;

import gr.uoi.cs.pythia.config.SparkConfig;
import gr.uoi.cs.pythia.engine.IDatasetProfiler;
import gr.uoi.cs.pythia.engine.IDatasetProfilerFactory;
import gr.uoi.cs.pythia.model.DatasetProfile;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.junit.Test;
import org.sparkproject.guava.io.Resources;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.junit.Assert.assertEquals;

public class ReportSystemTests {

  @Test
  public void testProduceReportTxt() throws IOException {
    SparkConfig sparkConfig = new SparkConfig();
    SparkSession sparkSession =
        SparkSession.builder()
            .appName(sparkConfig.getAppName())
            .master(sparkConfig.getMaster())
            .config("spark.sql.warehouse.dir", sparkConfig.getSparkWarehouse())
            .getOrCreate();

    StructType schema =
        new StructType(
            new StructField[] {
              new StructField("name", DataTypes.StringType, false, Metadata.empty()),
              new StructField("age", DataTypes.IntegerType, false, Metadata.empty()),
              new StructField("money", DataTypes.IntegerType, false, Metadata.empty()),
            });

    IDatasetProfiler datasetProfiler = IDatasetProfilerFactory.createDatasetProfiler();
    datasetProfiler.registerDataset("people", getResource("people.json").getAbsolutePath(), schema);
    DatasetProfile datasetProfile = datasetProfiler.computeProfileOfDataset();
    datasetProfile.setPath("");

    URL url = Resources.getResource("dummy_txt_report1.txt");
    String text = Resources.toString(url, StandardCharsets.UTF_8);
    assertEquals("File contents differ!", text, datasetProfile.toString());
    sparkSession.stop();
  }

  private File getResource(String resourceName) {
    return new File(
        Objects.requireNonNull(getClass().getClassLoader().getResource(resourceName)).getFile());
  }
}
