package gr.uoi.cs.pythia.decisiontree;

import gr.uoi.cs.pythia.TestsUtilities;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
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
        StructType schema = TestsUtilities.getTweetsCsvSchema();
        String filePath = TestsUtilities.getResourcePath("datasets/tweets.csv");
        dataset = AllDecisionTreeTests.dtResource.getSparkSession()
                .read()
                .option("header", "true")
                .schema(schema)
                .csv(filePath);
    }

    @Test
    public void testRemoveTooManyDistinctValuesCountCategoricalColumns() {
        dataset = new DecisionTreeOptimizer(dataset).getOptimizedDataset();

        List<String> expectedColumns = Arrays.asList("user_created", "user_followers", "user_friends",
                "user_favourites", "user_verified", "date", "retweets", "favorites", "is_retweet");
        List<String> actualColumns = Arrays.stream(dataset.schema().fields())
                .map(StructField::name)
                .collect(Collectors.toList());
        assertEquals(expectedColumns, actualColumns);
    }
}
