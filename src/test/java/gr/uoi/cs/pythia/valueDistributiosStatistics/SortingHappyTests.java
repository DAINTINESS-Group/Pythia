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

public class SortingHappyTests{

    private SparkSession sparkSession;
    private Dataset<Row> dataset;
    private Column column;

    @Before
    public void setUp(){
        sparkSession = SparkSession.builder().appName("MergeSortTest").master("local[*]").getOrCreate();
    }

    /**
     * Test case to verify the sorting of a valid list of numbers.
     * <p>
     * Happy Scenario:
     * <ul>
     *   <li>Dataset: A list of numbers [31.0, 57.0, 88.0, -39.0, 3.0].</li>
     *   <li>The function should sort the list in ascending order.</li>
     *   <li>Expected sorted list: [-39.0, 3.0, 31.0, 57.0, 88.0].</li>
     * </ul>
     */

    @Test
    public void testValidListSort(){
        // Sample data: list of numbers
        List<Row> data = Arrays.asList(
                RowFactory.create(31.0),
                RowFactory.create(57.0),
                RowFactory.create(88.0),
                RowFactory.create(-39.0),
                RowFactory.create(3.0)
        );

        StructType schema = new StructType().add("values", DataTypes.DoubleType, true);
        dataset = sparkSession.createDataFrame(data, schema);

        column = new Column(0, "values", "Double");

        Sorting sorter = new Sorting(dataset, column);
        sorter.sort();
        List<Number> sortedList = sorter.getsortedList();

        // Validate if the list is sorted correctly
        assertEquals(-39.0, sortedList.get(0).doubleValue(), 0.01);
        assertEquals(3.0, sortedList.get(1).doubleValue(), 0.01);
        assertEquals(31.0, sortedList.get(2).doubleValue(), 0.01);
        assertEquals(57.0, sortedList.get(3).doubleValue(), 0.01);
        assertEquals(88.0, sortedList.get(4).doubleValue(), 0.01);
    }


    /**
     * Test case to verify the sorting of a list of mixed values (positive, negative, zero).
     * <p>
     * Happy Scenario:
     * <ul>
     *   <li>Dataset: A list of mixed values [-28.0, 3.0, 88.0, 57.0, 31.0].</li>
     *   <li>The function should sort the list in ascending order.</li>
     *   <li>Expected sorted list: [-28.0, 3.0, 31.0, 57.0, 88.0].</li>
     * </ul>
     */
    @Test
    public void testMixedValuesListSort(){
        // Sample data: list of mixed values
        List<Row> data = Arrays.asList(
                RowFactory.create(-28.0),
                RowFactory.create(3.0),
                RowFactory.create(88.0),
                RowFactory.create(57.0),
                RowFactory.create(31.0)
        );

        StructType schema = new StructType().add("values", DataTypes.DoubleType, true);
        dataset = sparkSession.createDataFrame(data, schema);

        column = new Column(0, "values", "Double");

        Sorting sorter = new Sorting(dataset, column);
        sorter.sort();
        List<Number> sortedList = sorter.getsortedList();

        // Validate if the list is sorted correctly
        assertEquals(-28.0, sortedList.get(0).doubleValue(), 0.01);
        assertEquals(3.0, sortedList.get(1).doubleValue(), 0.01);
        assertEquals(31.0, sortedList.get(2).doubleValue(), 0.01);
        assertEquals(57.0, sortedList.get(3).doubleValue(), 0.01);
        assertEquals(88.0, sortedList.get(4).doubleValue(), 0.01);
    }
}
