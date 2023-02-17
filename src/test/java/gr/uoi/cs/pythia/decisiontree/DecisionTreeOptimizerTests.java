package gr.uoi.cs.pythia.decisiontree;

import gr.uoi.cs.pythia.testshelpers.TestsUtilities;
import gr.uoi.cs.pythia.testshelpers.TestsDatasetSchemas;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class DecisionTreeOptimizerTests {

    private Dataset<Row> dataset;

    @Before
    public void init() {
        StructType schema = TestsDatasetSchemas.getTweetsCsvSchema();
        String filePath = TestsUtilities.getDatasetPath("tweets.csv");
        dataset = SparkSession.builder()
                .getOrCreate()
                .read()
                .option("header", "true")
                .schema(schema)
                .csv(filePath);
    }

    @Test
    public void testRemoveCategoricalColumnsWithTooManyDistinctValues() {
        dataset = new DecisionTreeOptimizer(dataset).getOptimizedDataset();

        List<String> expectedColumns = Arrays.asList("user_created", "user_followers", "user_friends",
                "user_favourites", "user_verified", "date", "retweets", "favorites", "is_retweet");
        List<String> actualColumns = Arrays.stream(dataset.schema().fields())
                .map(StructField::name)
                .collect(Collectors.toList());
        assertEquals(expectedColumns, actualColumns);
    }
}

