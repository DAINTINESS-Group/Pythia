package gr.uoi.cs.pythia.valueDistributiosStatistics;

import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DescriptiveStatisticsProfile;
import gr.uoi.cs.pythia.model.histogram.Bin;
import gr.uoi.cs.pythia.model.histogram.Histogram;
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

import static org.junit.Assert.assertEquals;

public class HistogramTest{

    private Dataset<Row> dataset;
    private StructType schema = new StructType()
            .add("values", DataTypes.StringType, true);
    private final Column column = new Column(0, "values", DataTypes.DoubleType.toString());

    /**
     * Test case to verify the creation of a histogram for quartiles with a dataset containing multiple values.
     * <p>
     * Happy Scenario:
     * <ul>
     *   <li>Dataset: A list of values [10.0, 20.0, 80.0, 40.0, 50.0, 60.0, 70.0, 30.0].</li>
     *   <li>The function should create a histogram with 4 bins, each representing a quartile range:</li>
     *     <ul>
     *       <li>Bin 1: [10, 25) with 2 values (10.0, 20.0).</li>
     *       <li>Bin 2: [25, 45) with 2 values (30.0, 40.0).</li>
     *       <li>Bin 3: [45, 65) with 2 values (50.0, 60.0).</li>
     *       <li>Bin 4: [65, 80] with 2 values (70.0, 80.0).</li>
     *     </ul>
     * </ul>
     */
    @Test
    public void testCreateHistogramForQuartiles(){
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
        schema = new StructType()
                .add("values", DataTypes.DoubleType, true);
        dataset = SparkSession.builder()
                .master("local[*]")
                .appName("Test")
                .getOrCreate()
                .createDataFrame(data, schema);
        Column column = new Column(0, "values", DataTypes.DoubleType.toString());
        SparkValueDistributionsCalculator calculator = new SparkValueDistributionsCalculator(dataset, column);
        DescriptiveStatisticsProfile prof = new DescriptiveStatisticsProfile("", "", "", "", "10.0", "80.0");
        column.setDescriptiveStatisticsProfile(prof);
        calculator.createQuartilesProfile();
        calculator.createHistogramForQuartiles();
        Histogram actualHistogram = calculator.getHistogram();
        System.out.println((actualHistogram));

        List<Bin> actualBinList = actualHistogram.getBins();
        // Έλεγχος των ορίων και των αριθμών των τιμών σε κάθε bin
        assertEquals(10, actualBinList.get(0).getLowerBound(), 0.001);
        assertEquals(25, actualBinList.get(0).getUpperBound(), 0.001);
        assertEquals(2, actualBinList.get(0).getCount(), 0.001); // [10,25): 2 τιμές (10.0, 20.0)

        assertEquals(25, actualBinList.get(1).getLowerBound(), 0.001);
        assertEquals(45, actualBinList.get(1).getUpperBound(), 0.001);
        assertEquals(2, actualBinList.get(1).getCount(), 0.001); // [25,45): 2 τιμές (30.0, 40.0)

        assertEquals(45, actualBinList.get(2).getLowerBound(), 0.001);
        assertEquals(65, actualBinList.get(2).getUpperBound(), 0.001);
        assertEquals(2, actualBinList.get(2).getCount(), 0.001); // [45,65): 2 τιμές (50.0, 60.0)

        assertEquals(65, actualBinList.get(3).getLowerBound(), 0.001);
        assertEquals(80, actualBinList.get(3).getUpperBound(), 0.001);
        assertEquals(2, actualBinList.get(3).getCount(), 0.001); // [65,80]: 2 τιμές (70.0, 80.0)

    }

    /**
     * Test case to verify the creation of a histogram for quartiles with an empty dataset.
     * <p>
     * Edge Case:
     * <ul>
     *   <li>Dataset: An empty list of values.</li>
     *   <li>The function should return a histogram with 0 bins.</li>
     * </ul>
     */
    @Test
    public void testCreateHistogramForQuartiles_EmptyDataset(){
        List<Row> data = Collections.emptyList();
        dataset = SparkSession.builder()
                .master("local[*]")
                .appName("Test")
                .getOrCreate()
                .createDataFrame(data, schema);
        Column column = new Column(0, "values", DataTypes.DoubleType.toString());
        SparkValueDistributionsCalculator calculator = new SparkValueDistributionsCalculator(dataset, column);
        calculator.createHistogramForQuartiles();
        Histogram actualHistogram = calculator.getHistogram();

        assertEquals(0, actualHistogram.getBins().size()); // Κενό histogram
    }

    /**
     * Test case to verify the creation of a histogram for quartiles with a dataset containing a single value.
     * <p>
     * Edge Case:
     * <ul>
     *   <li>Dataset: A list containing a single value [42.0].</li>
     *   <li>The function should create a histogram with 2 bins:</li>
     *     <ul>
     *       <li>Bin 1: [42.0, 42.0] with 1 value (42.0).</li>
     *       <li>Bin 2: A bin to represent the single value.</li>
     *     </ul>
     * </ul>
     */
    @Test
    public void testCreateHistogramForQuartiles_SingleValue(){
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
        DescriptiveStatisticsProfile profile = new DescriptiveStatisticsProfile("", "", "", "", "42.0", "42.0");
        column.setDescriptiveStatisticsProfile(profile);
        calculator.createQuartilesProfile();
        calculator.createHistogramForQuartiles();
        Histogram actualHistogram = calculator.getHistogram();
        assertEquals(2, actualHistogram.getBins().size()); // Ένα bin μόνο
        assertEquals(42.0, actualHistogram.getBins().get(0).getLowerBound(), 0.001);
        assertEquals(42.0, actualHistogram.getBins().get(0).getUpperBound(), 0.001);
        assertEquals(1, actualHistogram.getBins().get(1).getCount(), 0.001);
    }

    /**
     * Test case to verify the creation of a histogram for quartiles with a dataset containing duplicated values.
     * <p>
     * Edge Case:
     * <ul>
     *   <li>Dataset: A list containing 5 identical values [50.0, 50.0, 50.0, 50.0, 50.0].</li>
     *   <li>The function should create a histogram with 2 bins:</li>
     *     <ul>
     *       <li>Bin 1: [50.0, 50.0] with 5 values (all 50.0).</li>
     *       <li>Bin 2: A bin to represent the duplicated values.</li>
     *     </ul>
     * </ul>
     */
    @Test
    public void testCreateHistogramForQuartiles_DuplicatedValues(){
        List<Row> data = Arrays.asList(
                RowFactory.create(50.0),
                RowFactory.create(50.0),
                RowFactory.create(50.0),
                RowFactory.create(50.0),
                RowFactory.create(50.0)
        );
        schema = new StructType()
                .add("values", DataTypes.DoubleType, true);
        dataset = SparkSession.builder()
                .master("local[*]")
                .appName("Test")
                .getOrCreate()
                .createDataFrame(data, schema);
        SparkValueDistributionsCalculator calculator = new SparkValueDistributionsCalculator(dataset, column);
        DescriptiveStatisticsProfile profile = new DescriptiveStatisticsProfile("", "", "", "", "50.0", "50.0");
        column.setDescriptiveStatisticsProfile(profile);
        calculator.createQuartilesProfile();
        calculator.createHistogramForQuartiles();
        Histogram actualHistogram = calculator.getHistogram();
        System.out.println(actualHistogram);
        assertEquals(2, actualHistogram.getBins().size()); // Όλες οι τιμές στο ίδιο bin
        assertEquals(50.0, actualHistogram.getBins().get(0).getLowerBound(), 0.001);
        assertEquals(50.0, actualHistogram.getBins().get(0).getUpperBound(), 0.001);
        assertEquals(5, actualHistogram.getBins().get(1).getCount(), 0.001);
    }


    /**
     * Test case to verify the creation of a histogram for quartiles with a dataset containing negative values.
     * <p>
     * Edge Case:
     * <ul>
     *   <li>Dataset: A list of negative values [-50.0, -40.0, -30.0, -20.0, -10.0].</li>
     *   <li>The function should create a histogram with 4 bins:</li>
     *     <ul>
     *       <li>Bin 1: [-50, -45) with 1 value (-50.0).</li>
     *       <li>Bin 2: [-45, -30) with 1 value (-40.0).</li>
     *       <li>Bin 3: [-30, -15) with 2 values (-30.0, -20.0).</li>
     *       <li>Bin 4: [-15, -10] with 1 value (-10.0).</li>
     *     </ul>
     * </ul>
     */
    @Test
    public void testCreateHistogramForQuartiles_NegativeValues(){
        List<Row> data = Arrays.asList(
                RowFactory.create(-50.0),
                RowFactory.create(-40.0),
                RowFactory.create(-30.0),
                RowFactory.create(-20.0),
                RowFactory.create(-10.0)
        );
        schema = new StructType()
                .add("values", DataTypes.DoubleType, true);
        dataset = SparkSession.builder()
                .master("local[*]")
                .appName("Test")
                .getOrCreate()
                .createDataFrame(data, schema);
        SparkValueDistributionsCalculator calculator = new SparkValueDistributionsCalculator(dataset, column);
        DescriptiveStatisticsProfile profile = new DescriptiveStatisticsProfile("", "", "", "", "-50.0", "-10.0");
        column.setDescriptiveStatisticsProfile(profile);
        calculator.createQuartilesProfile();
        calculator.createHistogramForQuartiles();
        Histogram actualHistogram = calculator.getHistogram();
        System.out.println((actualHistogram));
        List<Bin> actualBinList = actualHistogram.getBins();
        assertEquals(4, actualHistogram.getBins().size());
        // Έλεγχος των ορίων και του αριθμού των τιμών σε κάθε bin
        assertEquals(-50, actualBinList.get(0).getLowerBound(), 0.001);
        assertEquals(-45, actualBinList.get(0).getUpperBound(), 0.001);
        assertEquals(1, actualBinList.get(0).getCount(), 0.001); // [-50,-45): 1 value (-50.0)

        assertEquals(-45, actualBinList.get(1).getLowerBound(), 0.001);
        assertEquals(-30, actualBinList.get(1).getUpperBound(), 0.001);
        assertEquals(1, actualBinList.get(1).getCount(), 0.001); // [-45,-30): 1 value (-40.0)

        assertEquals(-30, actualBinList.get(2).getLowerBound(), 0.001);
        assertEquals(-15, actualBinList.get(2).getUpperBound(), 0.001);
        assertEquals(2, actualBinList.get(2).getCount(), 0.001); // [-30,-15): 2 values (-30.0, -20.0)

        assertEquals(-15, actualBinList.get(3).getLowerBound(), 0.001);
        assertEquals(-10, actualBinList.get(3).getUpperBound(), 0.001);
        assertEquals(1, actualBinList.get(3).getCount(), 0.001); // [-15,-10]: 1 value (-10.0)
    }


}
