package gr.uoi.cs.pythia.valueDistributiosStatistics;

import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.valueDistributionStatistics.Sorting;
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

public class SortingRainyTests{

    private SparkSession sparkSession;
    private Dataset<Row> dataset;
    private Column column;

    @Before
    public void setUp(){
        sparkSession = SparkSession.builder().appName("MergeSortTest").master("local[*]").getOrCreate();
    }

    /**
     * Test case to verify the handling of NaN and null values in the dataset.
     * <p>
     * Happy Scenario:
     * <ul>
     *   <li>Dataset: Contains a mix of valid values and NaN/null values [10.0, 30.0, null, 1.2, 0.3, NaN, 121.2].</li>
     *   <li>The sorting function should skip NaN and null values.</li>
     *   <li>Expected sorted list: [0.3, 1.2, 10.0, 30.0, 121.2].</li>
     * </ul>
     */
    @Test
    public void testListWithNaNAndNull(){
        // Sample data: list of numbers with NaN and null values
        List<Row> data = Arrays.asList(
                RowFactory.create(10.0),
                RowFactory.create(30.0),
                RowFactory.create((Object) null),  // null value
                RowFactory.create(1.2),
                RowFactory.create(0.3),
                RowFactory.create(Double.NaN),
                RowFactory.create(121.2)// NaN value
        );

        StructType schema = new StructType().add("values", DataTypes.DoubleType, true);
        dataset = sparkSession.createDataFrame(data, schema);

        column = new Column(0, "values", "Double");

        Sorting sorter = new Sorting(dataset, column);
        sorter.sort();
        List<Number> sortedList = sorter.getsortedList();

        // Validate that NaN and null values are skipped
        assertEquals(0.3, sortedList.get(0).doubleValue(), 0.01);
        assertEquals(1.2, sortedList.get(1).doubleValue(), 0.01);
        assertEquals(10.0, sortedList.get(2).doubleValue(), 0.01);
        assertEquals(30.0, sortedList.get(3).doubleValue(), 0.01);
        assertEquals(121.2, sortedList.get(4).doubleValue(), 0.01);
    }

    /**
     * Test case to verify the sorting of an empty list.
     * <p>
     * Happy Scenario:
     * <ul>
     *   <li>Dataset: An empty list of values.</li>
     *   <li>The function should return an empty list.</li>
     * </ul>
     */
    @Test
    public void testEmptyListSort(){
        // Sample data: empty list
        List<Row> data = Arrays.asList();

        StructType schema = new StructType().add("values", DataTypes.DoubleType, true);
        dataset = sparkSession.createDataFrame(data, schema);

        column = new Column(0, "values", "Double");

        Sorting sorter = new Sorting(dataset, column);
        sorter.sort();
        List<Number> sortedList = sorter.getsortedList();

        // Validate if the result is an empty list
        assertEquals(0, sortedList.size());
    }

    /**
     * Test case to verify the handling of invalid or non-numeric data.
     * <p>
     * Edge Case:
     * <ul>
     *   <li>Dataset: Contains non-numeric values ["apple", "banana", "3.5", "invalid"].</li>
     *   <li>The function should skip non-numeric values and only include valid numeric values.</li>
     *   <li>Expected sorted list: [3.5].</li>
     * </ul>
     */
    @Test
    public void testInvalidData(){
        List<Row> data = Arrays.asList(
                RowFactory.create("apple"),
                RowFactory.create("banana"),
                RowFactory.create("3.5"),
                RowFactory.create("invalid")
        );

        StructType schema = new StructType().add("values", DataTypes.StringType, true);
        dataset = sparkSession.createDataFrame(data, schema);

        column = new Column(0, "values", "String");

        Sorting sorter = new Sorting(dataset, column);
        sorter.sort();
        List<Number> sortedList = sorter.getsortedList();

        // Only the numeric value "3.5" should be included
        assertEquals(1, sortedList.size());
        assertEquals(3.5, sortedList.get(0).doubleValue(), 0.01);
    }

    /**
     * Test case to verify the behavior when all elements in the dataset are NaN.
     * <p>
     * Edge Case:
     * <ul>
     *   <li>Dataset: Contains only NaN values [NaN, NaN, NaN].</li>
     *   <li>The function should return an empty list as all values are invalid.</li>
     * </ul>
     */
    @Test
    public void testAllNaNValues(){
        List<Row> data = Arrays.asList(
                RowFactory.create(Double.NaN),
                RowFactory.create(Double.NaN),
                RowFactory.create(Double.NaN)
        );

        StructType schema = new StructType().add("values", DataTypes.DoubleType, true);
        dataset = sparkSession.createDataFrame(data, schema);

        column = new Column(0, "values", "Double");

        Sorting sorter = new Sorting(dataset, column);
        sorter.sort();
        List<Number> sortedList = sorter.getsortedList();

        // Verify that the result is an empty list due to all NaN values
        assertEquals(0, sortedList.size());
    }

}
