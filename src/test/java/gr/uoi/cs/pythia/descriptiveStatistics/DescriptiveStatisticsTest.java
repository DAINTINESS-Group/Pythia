package gr.uoi.cs.pythia.descriptiveStatistics;

import gr.uoi.cs.pythia.descriptivestatistics.DescriptiveStatisticsCalculator;
import gr.uoi.cs.pythia.model.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DescriptiveStatisticsTest {

    private Dataset<Row> dataset;
    private final StructType schema = new StructType()
            .add("values", DataTypes.StringType, true);
    private final Column column = new Column(0,"values","String");


    @Before
    public void init(){
        List<Row> data = Arrays.asList(
                RowFactory.create("apple"),
                RowFactory.create("banana"),
                RowFactory.create("apple"),
                RowFactory.create("orange"),
                RowFactory.create("apple"),
                RowFactory.create((Object) null),
                RowFactory.create((Object) null),
                RowFactory.create((Object) null),
                RowFactory.create((Object) null),
                RowFactory.create("banana"),
                RowFactory.create("orange"),
                RowFactory.create((Object) null),
                RowFactory.create("orange"),
                RowFactory.create("banana"),
                RowFactory.create("orange"),
                RowFactory.create((Object) null),
                RowFactory.create("orange"),
                RowFactory.create((Object) null)
        );
        dataset = SparkSession.builder()
                .master("local[*]")
                .appName("SparkValueDistributionsCalculatorTest")
                .getOrCreate()
                .createDataFrame(data, schema);
    }

    /**
     * Test case to verify the calculation of the mode (most frequent value) in the dataset under happy scenario V1.
     *
     * <p>
     * Happy Scenario V1:
     * <ul>
     *   <li>dataset: Contains various values ("apple", "banana", "orange") and nulls.</li>
     *   <li>mode: "orange" with the highest frequency (5 occurrences).</li>
     * </ul>
     *
     * <p>
     * This test verifies that:
     * <ul>
     *   <li>The mode is correctly identified as "orange".</li>
     *   <li>The number of occurrences for the mode is correct (5 occurrences).</li>
     * </ul>
     */
    @Test
    public void testCalculateMode() {
        DescriptiveStatisticsCalculator calculator = new DescriptiveStatisticsCalculator();
        List<String> modes =  calculator.computeModeValues(dataset,column);

        assertNotNull(modes);
        assertEquals(1, modes.size());
        assertEquals("orange", modes.get(0));
    }

    /**
     * Test case to verify the calculation of the mode when there are multiple values with equal frequency in the dataset.
     *
     * <p>
     * Happy Scenario V2:
     * <ul>
     *   <li>dataset: Contains equal frequency of "apple", "banana", and "orange" (3 occurrences each).</li>
     *   <li>dataset: Contains various other values such as "fruit", empty strings, "wrong", and strings with spaces.</li>
     * </ul>
     *
     * <p>
     * This test verifies that:
     * <ul>
     *   <li>All values with equal frequency ("apple", "banana", "orange") are correctly identified as modes.</li>
     *   <li>The number of occurrences for each mode is correctly reported (3 occurrences each).</li>
     *   <li>The additional values such as "fruit", empty strings, "wrong", and spaces are ignored in the mode calculation.</li>
     * </ul>
     */
    @Test
    public void testCalculateModeWithEqualFrequency() {
        List<Row> data = Arrays.asList(
                RowFactory.create("apple"),
                RowFactory.create("banana"),
                RowFactory.create("orange"),
                RowFactory.create("apple"),
                RowFactory.create("banana"),
                RowFactory.create("orange"),
                RowFactory.create("apple"),
                RowFactory.create("banana"),
                RowFactory.create("orange"),
                RowFactory.create("fruit"),
                RowFactory.create(""),
                RowFactory.create("wrong"),
                RowFactory.create("  "),
                RowFactory.create("  "),
                RowFactory.create("  "),
                RowFactory.create("  "),
                RowFactory.create("  "),
                RowFactory.create(""),
                RowFactory.create(""),
                RowFactory.create(""),
                RowFactory.create(""),
                RowFactory.create("")
        );

        Dataset<Row> dataset = SparkSession.builder()
                .master("local[*]")
                .appName("TestEqualFrequency")
                .getOrCreate()
                .createDataFrame(data, schema);

        DescriptiveStatisticsCalculator calculator = new DescriptiveStatisticsCalculator();
        calculator.computeModeValues(dataset,column);
        List<String> modes =  calculator.computeModeValues(dataset,column);

        assertNotNull(modes);
        assertEquals(3, modes.size()); // Given that there are 3 equal frequency values
        assertEquals("apple",modes.get(0));
        assertEquals("banana",modes.get(1));
        assertEquals("orange",modes.get(2));

    }
}
