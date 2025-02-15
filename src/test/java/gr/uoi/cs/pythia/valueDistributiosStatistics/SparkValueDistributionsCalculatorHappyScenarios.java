package gr.uoi.cs.pythia.valueDistributiosStatistics;

import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DescriptiveStatisticsProfile;
import gr.uoi.cs.pythia.model.DistributionsValues.Mode;
import gr.uoi.cs.pythia.model.DistributionsValues.QuartilesProfile;
import gr.uoi.cs.pythia.model.DistributionsValuesProfile;
import gr.uoi.cs.pythia.valueDistributionStatistics.SparkValueDistributionsCalculator;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SparkValueDistributionsCalculatorHappyScenarios{

    private Dataset<Row> dataset;
    private StructType schema = new StructType()
            .add("values", DataTypes.StringType, true);
    private final Column column = new Column(0, "values", "String");


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
    public void testCalculateMode(){
        SparkValueDistributionsCalculator calculator = new SparkValueDistributionsCalculator(dataset, column);
        calculator.calculateMode();
        List<Mode> modes = calculator.getListModes();

        assertNotNull(modes);
        assertEquals(1, modes.size());
        assertEquals("orange", modes.get(0).getValue());
        assertEquals(5, modes.get(0).getOccurrences());
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
    public void testCalculateModeWithEqualFrequency(){
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

        SparkValueDistributionsCalculator calculator = new SparkValueDistributionsCalculator(dataset, column);
        calculator.calculateMode();
        List<Mode> modes = calculator.getListModes();

        assertNotNull(modes);
        assertEquals(3, modes.size()); // Given that there are 3 equal frequency values
        assertEquals("apple", modes.get(0).getValue());
        assertEquals("banana", modes.get(1).getValue());
        assertEquals("orange", modes.get(2).getValue());
        assertEquals(3, modes.get(0).getOccurrences());
        assertEquals(3, modes.get(1).getOccurrences());
        assertEquals(3, modes.get(2).getOccurrences());
    }

    /**
     * Test case to verify the calculation of quartiles for a list of odd size.
     *
     * <p>
     * Happy Scenario:
     * <ul>
     *   <li>dataset: Contains the values: 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, and 9.0.</li>
     *   <li>The list has 9 elements, so the median (Q2) will be the middle value, and Q1 and Q3 will be calculated accordingly.</li>
     * </ul>
     *
     * <p>
     * This test verifies that:
     * <ul>
     *   <li>The first quartile (Q1) is correctly calculated as 2.5.</li>
     *   <li>The second quartile (Q2, median) is correctly calculated as 5.0.</li>
     *   <li>The third quartile (Q3) is correctly calculated as 7.5.</li>
     * </ul>
     */
    @Test
    public void testCalculateQuartiles_OddSizeList(){
        List<Row> data = Arrays.asList(
                RowFactory.create(1.0),
                RowFactory.create(2.0),
                RowFactory.create(3.0),
                RowFactory.create(4.0),
                RowFactory.create(5.0),
                RowFactory.create(6.0),
                RowFactory.create(7.0),
                RowFactory.create(8.0),
                RowFactory.create(9.0)
        );
        schema = new StructType()
                .add("values", DataTypes.DoubleType, true);

        dataset = SparkSession.builder()
                .master("local[*]")
                .appName("Test")
                .getOrCreate()
                .createDataFrame(data, schema);

        Column column = new Column(0, "values", DataTypes.DoubleType.toString());
        SparkValueDistributionsCalculator calculator = new SparkValueDistributionsCalculator(dataset, column);
        calculator.createQuartilesProfile();
        DescriptiveStatisticsProfile profile = new DescriptiveStatisticsProfile(null, "20.0", null, null, "10.0", "30.0");
        column.setDescriptiveStatisticsProfile(profile);
        DistributionsValuesProfile distributionsValuesProfile = column.getDistributionsValuesProfile();
        QuartilesProfile quartiles = distributionsValuesProfile.getQuartileProfile();

        assertEquals(2.5, quartiles.getQ1().doubleValue(), 0.01);
        assertEquals(5.0, quartiles.getQ2().doubleValue(), 0.01);
        assertEquals(7.5, quartiles.getQ3().doubleValue(), 0.01);
    }

    /**
     * Test case to verify the calculation of quartiles for a list of even size.
     *
     * <p>
     * Happy Scenario:
     * <ul>
     *   <li>dataset: Contains the values: 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, and 80.0.</li>
     *   <li>The list has 8 elements, so Q1 and Q3 will be calculated as averages of the appropriate adjacent values.</li>
     * </ul>
     *
     * <p>
     * This test verifies that:
     * <ul>
     *   <li>The first quartile (Q1) is correctly calculated as 25.0.</li>
     *   <li>The second quartile (Q2, median) is correctly calculated as 45.0.</li>
     *   <li>The third quartile (Q3) is correctly calculated as 65.0.</li>
     * </ul>
     */
    @Test
    public void testCalculateQuartiles_EvenSizeList(){
        List<Row> data = Arrays.asList(
                RowFactory.create(10.0),
                RowFactory.create(20.0),
                RowFactory.create(30.0),
                RowFactory.create(40.0),
                RowFactory.create(50.0),
                RowFactory.create(60.0),
                RowFactory.create(70.0),
                RowFactory.create(80.0)
        );
        schema = new StructType()
                .add("values", DataTypes.DoubleType, true);
        dataset = SparkSession.builder()
                .master("local[*]")
                .appName("Test")
                .getOrCreate()
                .createDataFrame(data, schema);
        Column column = new Column(0, "values", DataTypes.DoubleType.toString());
        SparkValueDistributionsCalculator calculator = new SparkValueDistributionsCalculator(dataset, column);
        calculator.createQuartilesProfile();
        DistributionsValuesProfile profile = column.getDistributionsValuesProfile();
        QuartilesProfile quartiles = profile.getQuartileProfile();

        assertEquals(25.0, quartiles.getQ1().doubleValue(), 0.01);
        assertEquals(45.0, quartiles.getQ2().doubleValue(), 0.01);
        assertEquals(65.0, quartiles.getQ3().doubleValue(), 0.01);
    }

    /**
     * Test case to verify the calculation of quartiles for a small size list.
     *
     * <p>
     * Happy Scenario:
     * <ul>
     *   <li>dataset: Contains the values: 2.0, 4.0, 6.0, and 8.0.</li>
     *   <li>The list has 4 elements, so Q1, Q2, and Q3 will be calculated as the average of the appropriate adjacent values.</li>
     * </ul>
     *
     * <p>
     * This test verifies that:
     * <ul>
     *   <li>The first quartile (Q1) is correctly calculated as 3.0.</li>
     *   <li>The second quartile (Q2, median) is correctly calculated as 5.0.</li>
     *   <li>The third quartile (Q3) is correctly calculated as 7.0.</li>
     * </ul>
     */
    @Test
    public void testCalculateQuartiles_SmallSizeList(){
        List<Row> data = Arrays.asList(
                RowFactory.create(2.0),
                RowFactory.create(4.0),
                RowFactory.create(6.0),
                RowFactory.create(8.0)
        );
        schema = new StructType()
                .add("values", DataTypes.DoubleType, true);
        dataset = SparkSession.builder()
                .master("local[*]")
                .appName("Test")
                .getOrCreate()
                .createDataFrame(data, schema);
        Column column = new Column(0, "values", DataTypes.DoubleType.toString());

        SparkValueDistributionsCalculator calculator = new SparkValueDistributionsCalculator(dataset, column);
        calculator.createQuartilesProfile();
        DistributionsValuesProfile profile = column.getDistributionsValuesProfile();
        QuartilesProfile quartiles = profile.getQuartileProfile();

        assertEquals(3.0, quartiles.getQ1().doubleValue(), 0.01);
        assertEquals(5.0, quartiles.getQ2().doubleValue(), 0.01);
        assertEquals(7.0, quartiles.getQ3().doubleValue(), 0.01);
    }

    /**
     * Test case to verify the calculation of quartiles for a single-element list.
     *
     * <p>
     * Happy Scenario:
     * <ul>
     *   <li>dataset: Contains a single value: 42.0.</li>
     *   <li>With only one element, all quartiles should be equal to the single value.</li>
     * </ul>
     *
     * <p>
     * This test verifies that:
     * <ul>
     *   <li>All quartiles (Q1, Q2, Q3) are correctly calculated as 42.0.</li>
     * </ul>
     */
    @Test
    public void testCalculateQuartiles_SingleElementList(){
        List<Row> data = Collections.singletonList(RowFactory.create(42.0));
        schema = new StructType()
                .add("values", DataTypes.DoubleType, true);
        dataset = SparkSession.builder()
                .master("local[*]")
                .appName("Test")
                .getOrCreate()
                .createDataFrame(data, schema);
        Column column = new Column(0, "values", DataTypes.DoubleType.toString());
        SparkValueDistributionsCalculator calculator = new SparkValueDistributionsCalculator(dataset, column);
        calculator.createQuartilesProfile();
        DistributionsValuesProfile profile = column.getDistributionsValuesProfile();
        QuartilesProfile quartiles = profile.getQuartileProfile();

        assertEquals(42.0, quartiles.getQ1().doubleValue(), 0.01);
        assertEquals(42.0, quartiles.getQ2().doubleValue(), 0.01);
        assertEquals(42.0, quartiles.getQ3().doubleValue(), 0.01);
    }

    /**
     * Test case to verify the calculation of quartiles for a large dataset.
     *
     * <p>
     * Happy Scenario:
     * <ul>
     *   <li>dataset: Contains a list of 24 values, ranging from 1.0 to 5000.0.</li>
     *   <li>Quartiles (Q1, Q2, Q3) will be calculated based on the data distribution.</li>
     * </ul>
     *
     * <p>
     * This test verifies that:
     * <ul>
     *   <li>The first quartile (Q1) is correctly calculated as 20.0.</li>
     *   <li>The second quartile (Q2, median) is correctly calculated as 47.5.</li>
     *   <li>The third quartile (Q3) is correctly calculated as 80.0.</li>
     * </ul>
     */
    @Test
    public void testCalculateQuartiles_LargeDataset(){
        List<Row> data = Arrays.asList(
                RowFactory.create(1.0), RowFactory.create(2.0), RowFactory.create(5.0), RowFactory.create(8.0),
                RowFactory.create(12.0), RowFactory.create(15.0), RowFactory.create(20.0), RowFactory.create(22.0),
                RowFactory.create(25.0), RowFactory.create(30.0), RowFactory.create(35.0), RowFactory.create(40.0),
                RowFactory.create(45.0), RowFactory.create(50.0), RowFactory.create(55.0), RowFactory.create(60.0),
                RowFactory.create(65.0), RowFactory.create(70.0), RowFactory.create(75.0), RowFactory.create(80.0),
                RowFactory.create(100.0), RowFactory.create(150.0), RowFactory.create(200.0), RowFactory.create(500.0),
                RowFactory.create(1000.0), RowFactory.create(5000.0)
        );

        schema = new StructType()
                .add("values", DataTypes.DoubleType, true);
        dataset = SparkSession.builder()
                .master("local[*]")
                .appName("Test")
                .getOrCreate()
                .createDataFrame(data, schema);
        Column column = new Column(0, "values", DataTypes.DoubleType.toString());
        SparkValueDistributionsCalculator calculator = new SparkValueDistributionsCalculator(dataset, column);
        calculator.createQuartilesProfile();
        DistributionsValuesProfile profile = column.getDistributionsValuesProfile();
        QuartilesProfile quartiles = profile.getQuartileProfile();

        assertEquals(20.0, quartiles.getQ1().doubleValue(), 0.01);
        assertEquals(47.5, quartiles.getQ2().doubleValue(), 0.01);
        assertEquals(80.0, quartiles.getQ3().doubleValue(), 0.01);
    }

    /**
     * Test case to verify the calculation of quartiles for a dataset with negative and positive values.
     * <p>
     * Happy Scenario:
     * <ul>
     *   <li>Dataset: Contains a mix of negative, zero, and positive values ranging from -50.0 to 40.0.</li>
     *   <li>The quartiles (Q1, Q2, Q3) are calculated based on the data distribution.</li>
     * </ul>
     * <p>
     * This test verifies that:
     * <ul>
     *   <li>The first quartile (Q1) is correctly calculated as -30.0.</li>
     *   <li>The second quartile (Q2, median) is correctly calculated as -5.0.</li>
     *   <li>The third quartile (Q3) is correctly calculated as 20.0.</li>
     * </ul>
     */
    @Test
    public void testCalculateQuartiles_NegativeValues(){
        List<Row> data = Arrays.asList(
                RowFactory.create(-50.0),
                RowFactory.create(-40.0),
                RowFactory.create(-30.0),
                RowFactory.create(-20.0),
                RowFactory.create(-10.0),
                RowFactory.create(0.0),
                RowFactory.create(10.0),
                RowFactory.create(20.0),
                RowFactory.create(30.0),
                RowFactory.create(40.0)
        );
        schema = new StructType().add("values", DataTypes.DoubleType, true);
        dataset = SparkSession.builder().master("local[*]").appName("Test").getOrCreate().createDataFrame(data, schema);
        Column column = new Column(0, "values", DataTypes.DoubleType.toString());
        SparkValueDistributionsCalculator calculator = new SparkValueDistributionsCalculator(dataset, column);
        calculator.createQuartilesProfile();
        DistributionsValuesProfile profile = column.getDistributionsValuesProfile();
        QuartilesProfile quartiles = profile.getQuartileProfile();

        assertEquals(-30.0, quartiles.getQ1().doubleValue(), 0.01);
        assertEquals(-5.0, quartiles.getQ2().doubleValue(), 0.01);
        assertEquals(20.0, quartiles.getQ3().doubleValue(), 0.01);
    }

    /**
     * Test case to verify the calculation of quartiles for a dataset with repeated values.
     * <p>
     * Happy Scenario:
     * <ul>
     *   <li>Dataset: Contains three repeated values of 10.0, 20.0, and 30.0.</li>
     *   <li>The quartiles (Q1, Q2, Q3) are calculated based on the data distribution.</li>
     * </ul>
     * <p>
     * This test verifies that:
     * <ul>
     *   <li>The first quartile (Q1) is correctly calculated as 10.0.</li>
     *   <li>The second quartile (Q2, median) is correctly calculated as 20.0.</li>
     *   <li>The third quartile (Q3) is correctly calculated as 30.0.</li>
     * </ul>
     */
    @Test
    public void testCalculateQuartiles_RepeatedValues(){
        List<Row> data = Arrays.asList(
                RowFactory.create(10.0),
                RowFactory.create(10.0),
                RowFactory.create(10.0),
                RowFactory.create(20.0),
                RowFactory.create(20.0),
                RowFactory.create(20.0),
                RowFactory.create(30.0),
                RowFactory.create(30.0),
                RowFactory.create(30.0)
        );
        schema = new StructType().add("values", DataTypes.DoubleType, true);
        dataset = SparkSession.builder().master("local[*]").appName("Test").getOrCreate().createDataFrame(data, schema);
        Column column = new Column(0, "values", DataTypes.DoubleType.toString());
        SparkValueDistributionsCalculator calculator = new SparkValueDistributionsCalculator(dataset, column);
        calculator.createQuartilesProfile();
        DistributionsValuesProfile profile = column.getDistributionsValuesProfile();
        QuartilesProfile quartiles = profile.getQuartileProfile();

        assertEquals(10.0, quartiles.getQ1().doubleValue(), 0.01);
        assertEquals(20.0, quartiles.getQ2().doubleValue(), 0.01);
        assertEquals(30.0, quartiles.getQ3().doubleValue(), 0.01);
    }

    /**
     * Test case to verify the calculation of quartiles for a dataset with only zero values.
     * <p>
     * Happy Scenario:
     * <ul>
     *   <li>Dataset: Contains multiple zero values.</li>
     *   <li>The quartiles (Q1, Q2, Q3) will all be zero as the dataset only contains zeros.</li>
     * </ul>
     * <p>
     * This test verifies that:
     * <ul>
     *   <li>The first quartile (Q1) is correctly calculated as 0.0.</li>
     *   <li>The second quartile (Q2, median) is correctly calculated as 0.0.</li>
     *   <li>The third quartile (Q3) is correctly calculated as 0.0.</li>
     * </ul>
     */
    @Test
    public void testCalculateQuartiles_ZeroValues(){
        List<Row> data = Arrays.asList(
                RowFactory.create(0.0),
                RowFactory.create(0.0),
                RowFactory.create(0.0),
                RowFactory.create(0.0),
                RowFactory.create(0.0),
                RowFactory.create(0.0)
        );
        schema = new StructType().add("values", DataTypes.DoubleType, true);
        dataset = SparkSession.builder().master("local[*]").appName("Test").getOrCreate().createDataFrame(data, schema);
        Column column = new Column(0, "values", DataTypes.DoubleType.toString());
        SparkValueDistributionsCalculator calculator = new SparkValueDistributionsCalculator(dataset, column);
        calculator.createQuartilesProfile();
        DistributionsValuesProfile profile = column.getDistributionsValuesProfile();
        QuartilesProfile quartiles = profile.getQuartileProfile();

        assertEquals(0.0, quartiles.getQ1().doubleValue(), 0.01);
        assertEquals(0.0, quartiles.getQ2().doubleValue(), 0.01);
        assertEquals(0.0, quartiles.getQ3().doubleValue(), 0.01);
    }

    /**
     * Test case to verify the calculation of quartiles for a dataset with all negative values.
     * <p>
     * Happy Scenario:
     * <ul>
     *   <li>Dataset: Contains negative values ranging from -100.0 to 0.0.</li>
     *   <li>The quartiles (Q1, Q2, Q3) are calculated based on the data distribution.</li>
     * </ul>
     * <p>
     * This test verifies that:
     * <ul>
     *   <li>The first quartile (Q1) is correctly calculated as -80.0.</li>
     *   <li>The second quartile (Q2, median) is correctly calculated as -50.0.</li>
     *   <li>The third quartile (Q3) is correctly calculated as -20.0.</li>
     * </ul>
     */
    @Test
    public void testCalculateQuartiles_AllNegativeValues(){
        List<Row> data = Arrays.asList(
                RowFactory.create(-100.0),
                RowFactory.create(-80.0),
                RowFactory.create(-60.0),
                RowFactory.create(-40.0),
                RowFactory.create(-20.0),
                RowFactory.create(0.0)
        );
        schema = new StructType().add("values", DataTypes.DoubleType, true);
        dataset = SparkSession.builder().master("local[*]").appName("Test").getOrCreate().createDataFrame(data, schema);
        Column column = new Column(0, "values", DataTypes.DoubleType.toString());
        SparkValueDistributionsCalculator calculator = new SparkValueDistributionsCalculator(dataset, column);
        calculator.createQuartilesProfile();
        DistributionsValuesProfile profile = column.getDistributionsValuesProfile();
        QuartilesProfile quartiles = profile.getQuartileProfile();

        assertEquals(-80.0, quartiles.getQ1().doubleValue(), 0.01);
        assertEquals(-50.0, quartiles.getQ2().doubleValue(), 0.01);
        assertEquals(-20.0, quartiles.getQ3().doubleValue(), 0.01);
    }

    /**
     * Test case to verify the calculation of quartiles for a dataset with repeated values.
     * <p>
     * Happy Scenario:
     * <ul>
     *   <li>Dataset: Contains three repeated values of 5.0, 10.0, and 15.0.</li>
     *   <li>The quartiles (Q1, Q2, Q3) are calculated based on the data distribution.</li>
     * </ul>
     * <p>
     * This test verifies that:
     * <ul>
     *   <li>The first quartile (Q1) is correctly calculated as 5.0.</li>
     *   <li>The second quartile (Q2, median) is correctly calculated as 10.0.</li>
     *   <li>The third quartile (Q3) is correctly calculated as 15.0.</li>
     * </ul>
     */
    @Test
    public void testCalculateQuartiles_RepeatedValuesV2(){
        List<Row> data = Arrays.asList(
                RowFactory.create(5.0),
                RowFactory.create(5.0),
                RowFactory.create(5.0),
                RowFactory.create(10.0),
                RowFactory.create(10.0),
                RowFactory.create(10.0),
                RowFactory.create(15.0),
                RowFactory.create(15.0),
                RowFactory.create(15.0)
        );
        schema = new StructType().add("values", DataTypes.DoubleType, true);
        dataset = SparkSession.builder().master("local[*]").appName("Test").getOrCreate().createDataFrame(data, schema);
        Column column = new Column(0, "values", DataTypes.DoubleType.toString());
        SparkValueDistributionsCalculator calculator = new SparkValueDistributionsCalculator(dataset, column);
        calculator.createQuartilesProfile();
        DistributionsValuesProfile profile = column.getDistributionsValuesProfile();
        QuartilesProfile quartiles = profile.getQuartileProfile();

        assertEquals(5.0, quartiles.getQ1().doubleValue(), 0.01);
        assertEquals(10.0, quartiles.getQ2().doubleValue(), 0.01);
        assertEquals(15.0, quartiles.getQ3().doubleValue(), 0.01);
    }

    /**
     * Test case to verify the calculation of quartiles for a dataset with large and small values.
     * <p>
     * Happy Scenario:
     * <ul>
     *   <li>Dataset: Contains small values ranging from 0.0001 to 0.01 and large values 1000.0 to 10000.0.</li>
     *   <li>The quartiles (Q1, Q2, Q3) are calculated based on the data distribution.</li>
     * </ul>
     * <p>
     * This test verifies that:
     * <ul>
     *   <li>The first quartile (Q1) is correctly calculated as 0.001.</li>
     *   <li>The second quartile (Q2, median) is correctly calculated as 0.01.</li>
     *   <li>The third quartile (Q3) is correctly calculated as 5500.0.</li>
     * </ul>
     */
    @Test
    public void testCalculateQuartiles_LargeSmallValues(){
        List<Row> data = Arrays.asList(
                RowFactory.create(0.0001),
                RowFactory.create(0.001),
                RowFactory.create(0.01),
                RowFactory.create(1000.0),
                RowFactory.create(10000.0)
        );
        schema = new StructType().add("values", DataTypes.DoubleType, true);
        dataset = SparkSession.builder().master("local[*]").appName("Test").getOrCreate().createDataFrame(data, schema);
        Column column = new Column(0, "values", DataTypes.DoubleType.toString());
        SparkValueDistributionsCalculator calculator = new SparkValueDistributionsCalculator(dataset, column);
        calculator.createQuartilesProfile();
        DistributionsValuesProfile profile = column.getDistributionsValuesProfile();
        QuartilesProfile quartiles = profile.getQuartileProfile();

        assertEquals(0.001, quartiles.getQ1().doubleValue(), 0.01);
        assertEquals(0.01, quartiles.getQ2().doubleValue(), 0.01); // Κεντρική τιμή (Q2)
        assertEquals(5500.0, quartiles.getQ3().doubleValue(), 0.01);
    }


}

