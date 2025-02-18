package gr.uoi.cs.pythia.histogram;

import gr.uoi.cs.pythia.histogram.generator.QuartilesHistogramGenerator;
import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DescriptiveStatisticsProfile;
import gr.uoi.cs.pythia.model.histogram.Bin;
import gr.uoi.cs.pythia.model.histogram.Histogram;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
public class QuartileHistogramTests {
    private static SparkSession sparkSession;


    // Initialize Spark session before tests are run
    @BeforeClass
    public static void setUpClass() {
        sparkSession = SparkSession.builder()
                .master("local[*]")
                .appName("Test")
                .getOrCreate();
    }

    // Stop the Spark session after tests are complete
    @AfterClass
    public static void tearDownClass() {
        sparkSession.stop();
    }

    // Helper method to create a Dataset from a list of rows
    private Dataset<Row> createDataset(List<Row> data) {
        StructType schema = new StructType()
                .add("values", DataTypes.DoubleType, true);
        return sparkSession.createDataFrame(data, schema);
    }

    /**
     * Test case to verify the creation of a histogram for quartiles with a dataset containing multiple values.
     *
     * <p>
     * This test case checks the correct generation of a quartile histogram when the dataset contains multiple distinct values.
     * </p>
     *
     * <p>
     * Preconditions:
     * <ul>
     *   <li>Spark session is initialized.</li>
     *   <li>Dataset is created with a list of double values.</li>
     *   <li>DescriptiveStatisticsProfile is set for the column with pre-calculated quartiles.</li>
     * </ul>
     * </p>
     *
     * <p>
     * This test asserts that:
     * <ul>
     *   <li>The generated histogram has the correct number of bins (4 for quartiles).</li>
     *   <li>The lower and upper bounds of each bin are correctly calculated based on the quartile values.</li>
     *   <li>The count of values within each bin is accurate.</li>
     * </ul>
     * </p>
     */
    @Test
    public void testCreateHistogramForQuartiles() {
        List<Row> data = Arrays.asList(
                RowFactory.create(10.0),
                RowFactory.create(20.0),
                RowFactory.create(80.0),
                RowFactory.create(40.0),
                RowFactory.create(50.0),
                RowFactory.create(60.0),
                RowFactory.create(70.0),
                RowFactory.create(30.0)
        );
        StructType schema = new StructType()
                .add("values", DataTypes.DoubleType, true);
        Dataset<Row> dataset = SparkSession.builder()
                .master("local[*]")
                .appName("Test")
                .getOrCreate()
                .createDataFrame(data, schema);

        // Define column and its descriptive statistics (min, max, quartiles)
        Column column = new Column(0, "values", "Double");
        DescriptiveStatisticsProfile profile = new DescriptiveStatisticsProfile(
                "", "", "", "20.0", "45.0", "60.0", "10.0", "80.0", Collections.emptyList());
        column.setDescriptiveStatisticsProfile(profile);

        // Instantiate QuartilesHistogramGenerator and generate histogram
        QuartilesHistogramGenerator generator = new QuartilesHistogramGenerator(dataset, column);
        Histogram actualHistogram = generator.generateHistogram(4); // 4 bins for quartiles

        List<Bin> actualBinList = actualHistogram.getBins();
        assertEquals(4, actualBinList.size()); // Ensure 4 bins for quartiles

        // Verify the bounds and counts for each bin
        assertEquals(10.0, actualBinList.get(0).getLowerBound(), 0.001);
        assertEquals(20.0, actualBinList.get(0).getUpperBound(), 0.001);
        assertEquals(1, actualBinList.get(0).getCount()); // Check count for [10,20)

        assertEquals(20.0, actualBinList.get(1).getLowerBound(), 0.001);
        assertEquals(45.0, actualBinList.get(1).getUpperBound(), 0.001);
        assertEquals(3, actualBinList.get(1).getCount()); // Check count for [20,45)

        assertEquals(45.0, actualBinList.get(2).getLowerBound(), 0.001);
        assertEquals(60.0, actualBinList.get(2).getUpperBound(), 0.001);
        assertEquals(1, actualBinList.get(2).getCount()); // Check count for [45,60)

        assertEquals(60.0, actualBinList.get(3).getLowerBound(), 0.001);
        assertEquals(80.0, actualBinList.get(3).getUpperBound(), 0.001);
        assertEquals(3, actualBinList.get(3).getCount()); // Check count for [60,80]
    }

    /**
     * Test case to verify the creation of a histogram for quartiles with a dataset containing a single value.
     *
     * <p>
     * This test ensures that when the dataset contains a single value, the histogram is created correctly.
     * </p>
     */
    @Test
    public void testCreateHistogramForQuartiles_SingleValue() {
        List<Row> data = Collections.singletonList(RowFactory.create(42.0));
        Dataset<Row> dataset = createDataset(data);
        Column column = new Column(0, "values", "Double");
        DescriptiveStatisticsProfile profile = new DescriptiveStatisticsProfile(
                "", "", "", "42.0", "42.0", "42.0", "42.0", "42.0", Collections.emptyList());
        column.setDescriptiveStatisticsProfile(profile);

        // Generate histogram for single value
        QuartilesHistogramGenerator generator = new QuartilesHistogramGenerator(dataset, column);
        Histogram actualHistogram = generator.generateHistogram(4); // 4 bins for quartiles

        assertEquals(2, actualHistogram.getBins().size()); // 2 bins for the single value
        assertEquals(42.0 - 0.0001, actualHistogram.getBins().get(0).getLowerBound(), 0.001);
        assertEquals(42.0, actualHistogram.getBins().get(0).getUpperBound(), 0.001);
        assertEquals(42.0, actualHistogram.getBins().get(1).getLowerBound(), 0.001);
        assertEquals(42.0 + 0.0001, actualHistogram.getBins().get(1).getUpperBound(), 0.001);

        // Ensure the correct count for each bin
        assertEquals(0, actualHistogram.getBins().get(0).getCount()); // No value in the first bin
        assertEquals(1, actualHistogram.getBins().get(1).getCount()); // Single value in the second bin
    }

    /**
     * Test case to verify the creation of a histogram for quartiles with a dataset containing duplicated values.
     *
     * <p>
     * This test checks how the histogram behaves when the dataset contains duplicated values.
     * </p>
     */
    @Test
    public void testCreateHistogramForQuartiles_DuplicatedValues() {
        List<Row> data = Arrays.asList(
                RowFactory.create(50.0),
                RowFactory.create(50.0),
                RowFactory.create(50.0),
                RowFactory.create(50.0),
                RowFactory.create(50.0)
        );
        Dataset<Row> dataset = createDataset(data);
        Column column = new Column(0, "values", "Double");
        DescriptiveStatisticsProfile profile = new DescriptiveStatisticsProfile(
                "", "", "", "50", "50.0", "50.0", "50.0", "50.0", Collections.emptyList());
        column.setDescriptiveStatisticsProfile(profile);

        // Generate histogram for duplicated values
        QuartilesHistogramGenerator generator = new QuartilesHistogramGenerator(dataset, column);
        Histogram actualHistogram = generator.generateHistogram(4); // 4 bins for quartiles

        assertEquals(2, actualHistogram.getBins().size()); // All values in the same bin
        assertEquals(50.0 - 0.0001, actualHistogram.getBins().get(0).getLowerBound(), 0.001);
        assertEquals(50.0, actualHistogram.getBins().get(0).getUpperBound(), 0.001);
        assertEquals(50.0, actualHistogram.getBins().get(1).getLowerBound(), 0.001);
        assertEquals(50.0 + 0.0001, actualHistogram.getBins().get(1).getUpperBound(), 0.001);

        // Ensure the correct count for duplicated values
        assertEquals(0, actualHistogram.getBins().get(0).getCount()); // All 5 values in the second bin
        assertEquals(5, actualHistogram.getBins().get(1).getCount());
    }

    /**
     * Test case to verify the creation of a histogram for quartiles with a dataset containing negative values.
     *
     * <p>
     * This test ensures that negative values are correctly handled when generating the quartile histogram.
     * </p>
     */
    @Test
    public void testCreateHistogramForQuartiles_NegativeValues() {
        List<Row> data = Arrays.asList(
                RowFactory.create(-50.0),
                RowFactory.create(-40.0),
                RowFactory.create(-30.0),
                RowFactory.create(-20.0),
                RowFactory.create(-10.0)
        );
        Dataset<Row> dataset = createDataset(data);
        Column column = new Column(0, "values", "Double");
        DescriptiveStatisticsProfile profile = new DescriptiveStatisticsProfile(
                "", "", "", "-40.0", "-30.0", "-20.0", "-50.0", "-10.0", Collections.emptyList());
        column.setDescriptiveStatisticsProfile(profile);

        // Generate histogram for negative values
        QuartilesHistogramGenerator generator = new QuartilesHistogramGenerator(dataset, column);
        Histogram actualHistogram = generator.generateHistogram(4); // 4 bins for quartiles

        List<Bin> actualBinList = actualHistogram.getBins();
        assertEquals(4, actualBinList.size());

        // Check bounds and counts for each bin
        assertEquals(-50.0, actualBinList.get(0).getLowerBound(), 0.001);
        assertEquals(-40.0, actualBinList.get(0).getUpperBound(), 0.001);
        assertEquals(1, actualBinList.get(0).getCount()); // [-50,-40): 1 value (-50.0)

        assertEquals(-40.0, actualBinList.get(1).getLowerBound(), 0.001);
        assertEquals(-30.0, actualBinList.get(1).getUpperBound(), 0.001);
        assertEquals(1, actualBinList.get(1).getCount()); // [-40,-30): 1 value (-40.0)

        assertEquals(-30.0, actualBinList.get(2).getLowerBound(), 0.001);
        assertEquals(-20.0, actualBinList.get(2).getUpperBound(), 0.001);
        assertEquals(1, actualBinList.get(2).getCount()); // [-30,-20): 1 value (-30.0)

        assertEquals(-20.0, actualBinList.get(3).getLowerBound(), 0.001);
        assertEquals(-10.0, actualBinList.get(3).getUpperBound(), 0.001);
        assertEquals(2, actualBinList.get(3).getCount()); // [-20,-10): 2 values (-20.0, -10.0)
    }
}
