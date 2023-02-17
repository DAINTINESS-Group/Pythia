package gr.uoi.cs.pythia.writer;

import gr.uoi.cs.pythia.testshelpers.TestsDatasetSchemas;
import gr.uoi.cs.pythia.testshelpers.TestsUtilities;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class NaiveWriterTests {

    @Test
    public void testNaiveDatasetWriter() throws IOException {
        String reportPath = TestsUtilities.getResultsDir("writer") + File.separator + "test.csv";
        File testCsv = new File(reportPath);
        AllWriterTests.writerResource.getDatasetProfiler()
                .writeDataset(DatasetWriterConstants.NAIVE, testCsv.getAbsolutePath());
        Dataset<Row> dataset = SparkSession.builder()
                .getOrCreate()
                .read()
                .option("header", "true")
                .schema(TestsDatasetSchemas.getPeopleJsonSchema())
                .csv(testCsv.getAbsolutePath());

        List<Object> actualFirstColumn = dataset.select("name").toJavaRDD().map(row -> row.get(0)).collect();
        List<Object> actualSecondColumn = dataset.select("age").toJavaRDD().map(row -> row.get(0)).collect();
        List<Object> actualThirdColumn = dataset.select("money").toJavaRDD().map(row -> row.get(0)).collect();

        List<String> expectedFirstColumn = new ArrayList<>(Arrays.asList("Michael", "Andy", "Justin"));
        List<Integer> expectedSecondColumn = new ArrayList<>(Arrays.asList(50, 30, 19));
        List<Integer> expectedThirdColumn = new ArrayList<>(Arrays.asList(10, 20, 30));

        assertEquals(expectedFirstColumn, actualFirstColumn);
        assertEquals(expectedSecondColumn, actualSecondColumn);
        assertEquals(expectedThirdColumn, actualThirdColumn);
    }
}
