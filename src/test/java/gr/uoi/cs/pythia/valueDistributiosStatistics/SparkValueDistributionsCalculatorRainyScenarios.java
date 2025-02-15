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
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;


public class SparkValueDistributionsCalculatorRainyScenarios{
    private Dataset<Row> dataset;
    private StructType schema = new StructType()
            .add("values", DataTypes.StringType, true);
    private final Column column = new Column(0, "values", "String");

    /**
     * Test case to verify the behavior of mode calculation with an empty dataset.
     * <p>
     * This test ensures that:
     * <ul>
     *   <li>The result of calculating the mode for an empty dataset is not null.</li>
     *   <li>The mode list is empty, as there is no data to calculate the mode.</li>
     * </ul>
     * </p>
     */
    @Test
    public void testCalculateModeWithEmptyDataset(){
        List<Row> emptyData = Collections.emptyList();
        Dataset<Row> emptyDataset = SparkSession.builder()
                .master("local[*]")
                .appName("Test")
                .getOrCreate()
                .createDataFrame(emptyData, schema);

        SparkValueDistributionsCalculator calculator = new SparkValueDistributionsCalculator(emptyDataset, column);
        calculator.calculateMode();
        List<Mode> modes = calculator.getListModes();

        assertNotNull(modes);
        assertTrue(modes.isEmpty());
    }

    /**
     * Test case to verify the behavior of mode calculation with a non-existent column.
     * <p>
     * This test ensures that:
     * <ul>
     *   <li>The result of mode calculation for a non-existent column is not null.</li>
     *   <li>The mode list is empty because the column does not exist.</li>
     * </ul>
     * </p>
     */
    @Test
    public void testCalculateModeWithWrongColumn(){
        List<Row> data = Collections.singletonList(
                RowFactory.create("apple")
        );
        Dataset<Row> dataset = SparkSession.builder()
                .master("local[*]")
                .appName("Test")
                .getOrCreate()
                .createDataFrame(data, schema);

        Column wrongColumn = new Column(0, "wrongColumn", "String");
        SparkValueDistributionsCalculator calculator = new SparkValueDistributionsCalculator(dataset, wrongColumn);
        calculator.calculateMode();
        List<Mode> modes = calculator.getListModes();

        assertNotNull(modes);
        assertTrue(modes.isEmpty());
    }

    /**
     * Test case to verify the behavior of mode calculation when the column name is null.
     * <p>
     * This test ensures that:
     * <ul>
     *   <li>The result of mode calculation with a null column name is not null.</li>
     *   <li>The mode list is empty, as no column was provided for calculation.</li>
     * </ul>
     * </p>
     */
    @Test
    public void testCalculateModeWithNullColumn(){
        List<Row> data = Arrays.asList(
                RowFactory.create("apple"),
                RowFactory.create("banana")
        );
        Dataset<Row> dataset = SparkSession.builder()
                .master("local[*]")
                .appName("Test")
                .getOrCreate()
                .createDataFrame(data, schema);

        SparkValueDistributionsCalculator calculator = new SparkValueDistributionsCalculator(dataset, null);
        calculator.calculateMode();
        List<Mode> modes = calculator.getListModes();

        assertNotNull(modes);
        assertTrue(modes.isEmpty());
    }

    /**
     * Test case to verify the calculation of quartiles for an empty list.
     *
     * <p>
     * Happy Scenario:
     * <ul>
     *   <li>dataset: Contains no values.</li>
     *   <li>The list is empty, so quartiles will be set to null.</li>
     * </ul>
     *
     * <p>
     * This test verifies that:
     * <ul>
     *   <li>All quartiles (Q1, Q2, Q3) are correctly set to null.</li>
     * </ul>
     */
    @Test
    public void testCalculateQuartiles_EmptyList(){
        List<Row> data = Collections.emptyList();
        schema = new StructType()
                .add("values", DataTypes.DoubleType, true);

        dataset = SparkSession.builder()
                .master("local[*]")
                .appName("Test")
                .getOrCreate()
                .createDataFrame(data, schema);
        Column column = new Column(0, "values", DataTypes.DoubleType.toString());
        SparkValueDistributionsCalculator calculator = new SparkValueDistributionsCalculator(dataset, column);
        DescriptiveStatisticsProfile profile = new DescriptiveStatisticsProfile(null, null, null, null, null, null);
        column.setDescriptiveStatisticsProfile(profile);
        calculator.createQuartilesProfile();
        DistributionsValuesProfile distributionsValuesProfile = column.getDistributionsValuesProfile();
        QuartilesProfile quartiles = distributionsValuesProfile.getQuartileProfile();

        assertNull("Q1 should be null for empty list", quartiles.getQ1());
        assertNull("Q2 should be null for empty list", quartiles.getQ2());
        assertNull("Q3 should be null for empty list", quartiles.getQ3());
    }


    /**
     * Test case to verify the handling of missing values (null or NaN).
     * <p>
     * Happy Scenario:
     * <ul>
     *   <li>Dataset: Contains a mix of valid values and null/NaN values.</li>
     *   <li>The quartiles (Q1, Q2, Q3) should be calculated based on the non-null values.</li>
     * </ul>
     * <p>
     * This test verifies that:
     * <ul>
     *   <li>The first quartile (Q1) is correctly calculated.</li>
     *   <li>The second quartile (Q2, median) is correctly calculated.</li>
     *   <li>The third quartile (Q3) is correctly calculated.</li>
     * </ul>
     */
    @Test
    public void testCalculateQuartiles_WithMissingValuesV2(){
        List<Row> data = Arrays.asList(
                RowFactory.create(10.0),
                RowFactory.create(20.0),
                RowFactory.create((Object) null),  // Missing value
                RowFactory.create(30.0),
                RowFactory.create(Double.NaN)  // NaN value
        );
        schema = new StructType().add("values", DataTypes.DoubleType, true);
        dataset = SparkSession.builder().master("local[*]").appName("Test").getOrCreate().createDataFrame(data, schema);
        Column column = new Column(0, "values", DataTypes.DoubleType.toString());
        SparkValueDistributionsCalculator calculator = new SparkValueDistributionsCalculator(dataset, column);
        DescriptiveStatisticsProfile profile = new DescriptiveStatisticsProfile(null, "20.0", null, null, "10.0", "30.0");
        column.setDescriptiveStatisticsProfile(profile);
        calculator.createQuartilesProfile();
        DistributionsValuesProfile distributionsValuesProfile = column.getDistributionsValuesProfile();
        QuartilesProfile quartiles = distributionsValuesProfile.getQuartileProfile();

        // Check that quartiles are calculated based on the non-null values
        assertEquals(10.0, quartiles.getQ1().doubleValue(), 0.01);
        assertEquals(20.0, quartiles.getQ2().doubleValue(), 0.01);
        assertEquals(30.0, quartiles.getQ3().doubleValue(), 0.01);
    }

    /**
     * Test case to verify the handling of non-numeric values in the dataset during quartile calculation.
     * <p>
     * Happy Scenario:
     * <ul>
     *   <li>Dataset: Contains a mix of valid numeric values and non-numeric values (e.g., strings).</li>
     *   <li>The function should gracefully handle or reject non-numeric values without throwing errors.</li>
     * </ul>
     * <p>
     * This test verifies that:
     * <ul>
     *   <li>Non-numeric values are properly skipped or ignored during processing.</li>
     *   <li>Quartiles (Q1, Q2, Q3) are calculated based solely on the valid numeric values.</li>
     *   <li>The function ensures that non-numeric values do not interfere with the quartile calculation.</li>
     * </ul>
     * <p>
     * Expected behavior:
     * <ul>
     *   <li>The non-numeric value "INVALID" will be ignored.</li>
     *   <li>The quartiles will be calculated for the numeric values [10.0, 20.0, 30.0], resulting in Q1 = 10.0, Q2 = 20.0, Q3 = 30.0.</li>
     * </ul>
     */
    @Test
    public void testCalculateQuartiles_WithNonNumericValues(){
        List<Row> data = Arrays.asList(
                RowFactory.create("10.0"),
                RowFactory.create("20.0"),
                RowFactory.create("INVALID"),  // Non-numeric value
                RowFactory.create("30.0")
        );
        schema = new StructType().add("values", DataTypes.StringType, true);
        dataset = SparkSession.builder().master("local[*]").appName("Test").getOrCreate().createDataFrame(data, schema);
        Column column = new Column(0, "values", DataTypes.DoubleType.toString());
        DescriptiveStatisticsProfile profile = new DescriptiveStatisticsProfile(null, "20.0", null, null, "10.0", "30.0");
        column.setDescriptiveStatisticsProfile(profile);

        SparkValueDistributionsCalculator calculator = new SparkValueDistributionsCalculator(dataset, column);
        calculator.createQuartilesProfile();
        DistributionsValuesProfile distributionsValuesProfile = column.getDistributionsValuesProfile();
        QuartilesProfile quartiles = distributionsValuesProfile.getQuartileProfile();
        assertEquals(10.0, quartiles.getQ1().doubleValue(), 0.01);
        assertEquals(20.0, quartiles.getQ2().doubleValue(), 0.01);
        assertEquals(30.0, quartiles.getQ3().doubleValue(), 0.01);
    }


    /**
     * Test case to verify the handling of missing values (null or NaN) during quartile calculation.
     * <p>
     * Happy Scenario:
     * <ul>
     *   <li>Dataset: A collection containing both valid numerical values as well as missing values (null or NaN).</li>
     *   <li>The quartiles (Q1, Q2, Q3) should be calculated only based on the valid non-null numerical values.</li>
     * </ul>
     * <p>
     * This test ensures that:
     * <ul>
     *   <li>Missing values (null or NaN) are correctly ignored and do not interfere with the quartile calculations.</li>
     *   <li>The first quartile (Q1) is correctly calculated based on the remaining valid values.</li>
     *   <li>The second quartile (Q2, median) is correctly calculated from the valid data.</li>
     *   <li>The third quartile (Q3) is correctly computed from the non-missing values.</li>
     * </ul>
     * <p>
     * The test case includes:
     * <ul>
     *   <li>A mixture of valid numbers (10.0, 20.0, 30.0) along with missing data (null and NaN).</li>
     *   <li>Validation that the quartile calculations are based on the available, non-null data.</li>
     * </ul>
     */

    @Test
    public void testCalculateQuartiles_WithMissingValues(){
        List<Row> data = Arrays.asList(
                RowFactory.create(10.0),
                RowFactory.create(20.0),
                RowFactory.create((Object) null),  // Missing value
                RowFactory.create(30.0),
                RowFactory.create(Double.NaN)  // NaN value
        );
        schema = new StructType().add("values", DataTypes.DoubleType, true);
        dataset = SparkSession.builder().master("local[*]").appName("Test").getOrCreate().createDataFrame(data, schema);
        Column column = new Column(0, "values", DataTypes.DoubleType.toString());
        DescriptiveStatisticsProfile profile = new DescriptiveStatisticsProfile(null, "20.0", null, null, "10.0", "30.0");
        column.setDescriptiveStatisticsProfile(profile);
        SparkValueDistributionsCalculator calculator = new SparkValueDistributionsCalculator(dataset, column);
        calculator.createQuartilesProfile();
        DistributionsValuesProfile distributionsValuesProfile = column.getDistributionsValuesProfile();
        QuartilesProfile quartiles = distributionsValuesProfile.getQuartileProfile();

        // Check that quartiles are calculated based on the non-null values
        assertEquals(10.0, quartiles.getQ1().doubleValue(), 0.01);
        assertEquals(20.0, quartiles.getQ2().doubleValue(), 0.01);
        assertEquals(30.0, quartiles.getQ3().doubleValue(), 0.01);
    }
}
