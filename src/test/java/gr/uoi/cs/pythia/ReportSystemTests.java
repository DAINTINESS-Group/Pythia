package gr.uoi.cs.pythia;

import gr.uoi.cs.pythia.config.SparkConfig;
import gr.uoi.cs.pythia.engine.IDatasetProfiler;
import gr.uoi.cs.pythia.engine.IDatasetProfilerFactory;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.writer.DatasetWriterConstants;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.sparkproject.guava.io.Resources;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ReportSystemTests {

  private static SparkSession sparkSession;

  @Before
  public void init() {
    SparkConfig sparkConfig = new SparkConfig();
    sparkSession =
        SparkSession.builder()
            .appName(sparkConfig.getAppName())
            .master(sparkConfig.getMaster())
            .config("spark.sql.warehouse.dir", sparkConfig.getSparkWarehouse())
            .getOrCreate();
  }

  @Test
  public void testProduceReportTxt() throws IOException, AnalysisException, IllegalAccessException {
    StructType schema =
        new StructType(
            new StructField[] {
              new StructField("name", DataTypes.StringType, false, Metadata.empty()),
              new StructField("age", DataTypes.IntegerType, false, Metadata.empty()),
              new StructField("money", DataTypes.IntegerType, false, Metadata.empty()),
            });

    IDatasetProfiler datasetProfiler = new IDatasetProfilerFactory().createDatasetProfiler();
    datasetProfiler.registerDataset("people", getResource("people.json").getAbsolutePath(), schema);
    DatasetProfile datasetProfile = datasetProfiler.computeProfileOfDataset();
    FieldUtils.writeField(datasetProfile, "path", "", true);

    URL url = Resources.getResource("dummy_txt_report_tweets.txt");
    String text = Resources.toString(url, StandardCharsets.UTF_8);
    assertEquals("File contents differ!", text.replace("\r", ""), datasetProfile.toString());
  }

  @Test
  public void testNaiveDatasetWriter() throws AnalysisException, IOException {
    StructType schema =
            new StructType(
                    new StructField[] {
                            new StructField("name", DataTypes.StringType, false, Metadata.empty()),
                            new StructField("age", DataTypes.IntegerType, false, Metadata.empty()),
                            new StructField("money", DataTypes.IntegerType, false, Metadata.empty()),
                    });

    IDatasetProfiler datasetProfiler = new IDatasetProfilerFactory().createDatasetProfiler();
    datasetProfiler.registerDataset("people", getResource("people.json").getAbsolutePath(), schema);
    File testCsv =
            new File(
                    String.format(
                            "src%stest%sresources%stest.csv", File.separator, File.separator, File.separator));
    datasetProfiler.writeDataset(DatasetWriterConstants.NAIVE, testCsv.getAbsolutePath());
    Dataset<Row> dataset = sparkSession.read().csv(testCsv.getAbsolutePath());
    List<Object> actualFirstColumn = dataset.select("_c0").toJavaRDD().map(row -> row.get(0)).collect();
    List<Object> actualSecondColumn = dataset.select("_c1").toJavaRDD().map(row -> row.get(0)).collect();
    List<Object> actualThirdColumn = dataset.select("_c2").toJavaRDD().map(row -> row.get(0)).collect();

    List<String> expectedFirstColumn = new ArrayList<>(Arrays.asList("name", "Michael", "Andy", "Justin"));
    List<String> expectedSecondColumn = new ArrayList<>(Arrays.asList("age", "50", "30", "19"));
    List<String> expectedThirdColumn = new ArrayList<>(Arrays.asList("money", "10", "20", "30"));

    assertEquals(expectedFirstColumn, actualFirstColumn);
    assertEquals(expectedSecondColumn, actualSecondColumn);
    assertEquals(expectedThirdColumn, actualThirdColumn);
    assertTrue(testCsv.delete());
  }

  @AfterClass
  public static void closeSparkSession() {
    sparkSession.stop();
  }

  private File getResource(String resourceName) {
    return new File(
        Objects.requireNonNull(getClass().getClassLoader().getResource(resourceName)).getFile());
  }
}
