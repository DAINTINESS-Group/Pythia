package gr.uoi.cs.pythia.cardinalities;

import gr.uoi.cs.pythia.config.SparkConfig;
import gr.uoi.cs.pythia.reader.IDatasetReaderFactory;
import gr.uoi.cs.pythia.testshelpers.TestsDatasetSchemas;
import gr.uoi.cs.pythia.testshelpers.TestsUtilities;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test class for verifying the functionality of the SparkCardinalitiesCalculator.
 */
public class SparkCardinalitiesCalculatorHappyTests {

    private ICardinalitiesCalculator cardinalitiesTasks;
    private Dataset<Row> dataset;
    private SparkSession session;
    private String datasetPath;
    private CardinalitiesCalculatorFactory factory;
    /**
     * Initializes necessary objects before each test case.
     *
     * @throws AnalysisException if an error occurs during dataset initialization
     */
    @Before
    public void init() throws AnalysisException {
        initializeSparkSession();
        initializeDatasetWithReadTask();
        StructField[] fields = dataset.schema().fields();
        //Take first column !!
        String columnName = fields[0].name(); /* Column to Test: manufacturer */
        factory = new CardinalitiesCalculatorFactory();
        cardinalitiesTasks = factory.createCardinalitiesCalculator(dataset, columnName);
    }

    /**
     * Initializes the Spark session.
     */
    private void initializeSparkSession() {
        SparkConfig sparkConfig = new SparkConfig();
        session = SparkSession.builder()
                .appName(sparkConfig.getAppName())
                .master(sparkConfig.getMaster())
                .config("spark.sql.warehouse.dir", sparkConfig.getSparkWarehouse())
                .getOrCreate();
    }

    /**
     * Initializes the dataset with read task.
     *
     * @throws AnalysisException if an error occurs during dataset initialization
     *   file to test
     *   Column to Test: manufacturer
     * </p>

     * <pre>{@car_20_NotNullEmptyValues.csv:
     * manufacturer,model,year,price,transmission,mileage,fuelType,tax,mpg,engineSize
     * audi,A1,2017,12500,Manual,15735,Petrol,150,55.4,1.4
     * audi,A6,2016,1650,Automatic,36203,Diesel,20,64.2,2
     * audi,A1,2016,11000,Manual,29946,Petrol,30,55.4,1.4
     * audi,A4,2017,500,Automatic,25952,Diesel,145,67.3,2
     * test1,A3,2019,17300,Manual,1998,Petrol,145,49.6,1
     * test2,A1,2016,13900,Automatic,32260,Petrol,30,58.9,1.4
     * test3,A6,2016,1325,Automatic,76788,Diesel,30,61.4,2
     * test4,A4,2016,500,Manual,75185,Diesel,20,70.6,2
     * test4,A3,2015,10200,Manual,46112,Petrol,20,60.1,1.4
     * test4,A1,2016,12000,Manual,22451,Petrol,30,55.4,1.4
     * test4,A3,2017,161000,Manual,28955,Petrol,145,58.9,1.4
     * test4,A6,2016,1650,Automatic,52198,Diesel,125,57.6,2
     * test2,Q3,2016,91700,Manual,44915,Diesel,145,52.3,2
     * test2,A3,2017,164000,Manual,21695,Petrol,30,58.9,1.4
     * test2,A6,2015,1540,Manual,47348,Diesel,30,61.4,2
     * test2,A3,2017,145000,Automatic,26156,Petrol,145,58.9,1.4
     * test1,Q3,2016,915700,Automatic,28396,Diesel,145,53.3,2
     * audi,A3,2014,139000,Automatic,30516,Petrol,30,56.5,1.4
     * audi,Q5,2016,919000,Automatic,37652,Diesel,200,47.1,2
     * audi,Q3,2016,919000,Automatic,37652,Diesel,200,47.1,2
     * }</pre>
     */
    private void initializeDatasetWithReadTask() throws AnalysisException {
        IDatasetReaderFactory dataFrameReaderFactory = new IDatasetReaderFactory(session);
        datasetPath = TestsUtilities.getAbsoluteDatasetPath("car_20_NotNullEmptyValues.csv");
        StructType schema = TestsDatasetSchemas.getCarsCsvSchema();
        dataset = dataFrameReaderFactory.createDataframeReader(datasetPath, schema).read();
    }

    /**
     * Test case to verify the calculation of the number of null values in the Column.
     *
     * <p>
     * This test case checks the calculation of the number of null values in the Column.
     * </p>
     *
     * <p>
     * Preconditions:
     * <ul>
     *   <li>Spark session is initialized.</li>
     *   <li>Dataset is loaded with data with NotNulls,empties.</li>
     * </ul>
     * </p>
     *
     * <p>
     * This test asserts that:
     * <ul>
     *   <li>The calculated number of null values matches the expected value.The expected value is zero.</li>
     * </ul>
     * </p>
     */
    @Test
    public void calculateNumberOfNullValuesTest() {
        cardinalitiesTasks.calculateNumberOfNullValues();
        long expectedNumberOfNullValues = 0;
        long actualNumberOfNullValues = cardinalitiesTasks.getNumberOfNullValues();
        assertEquals(expectedNumberOfNullValues, actualNumberOfNullValues);
    }

    /**
     * Test case to verify the calculation of the distinct values in the column.
     *
     * <p>
     * This test case checks the calculation of the number of distinct values in the column.
     * </p>
     *
     * <p>
     * Preconditions:
     * <ul>
     *   <li>Spark session is initialized.</li>
     *   <li>Dataset is loaded with data with NotNulls,empties.</li>
     * </ul>
     * </p>
     *
     * <p>
     * This test asserts that:
     * <ul>
     *   <li>The calculated number of distinct values matches the expected value 5. {audi,test1,test2,test3,test4 }</li>
     * </ul>
     * </p>
     * <p>
     */

    @Test
    public void calculateDistinctValuesTest() {
        cardinalitiesTasks.calculateDistincValues();
        long expectedNumberOfDistinctValues = 5;
        long actualNumberOfDistinctValues = cardinalitiesTasks.getNumberOfDistinctValues();
        assertEquals(expectedNumberOfDistinctValues, actualNumberOfDistinctValues);
    }

    /**
     * Test case to verify the calculation of the number of null values in the dataset.
     *
     * <p>
     * This test case checks the calculation of the number of null values in the dataset.
     * </p>
     *
     * <p>
     * Preconditions:
     * <ul>
     *   <li>Spark session is initialized.</li>
     *   <li>Dataset is loaded with data.</li>
     * </ul>
     * </p>
     *
     * <p>
     * This test asserts that:
     * <ul>
     *   <li>The calculated number of null values matches the expected value.</li>
     * </ul>
     * </p>
     */




    /**
     * Initializes the dataset with read task.
     *
     * @throws AnalysisException if an error occurs during dataset initialization
     *
     * File to test:
     * Column to Test: manufacturer
     * <pre>{@car_20_NullEmpty.csv
     * manufacturer,model,year,price,transmission,mileage,fuelType,tax,mpg,engineSize
     * ,A1,2017,12500,Manual,15735,Petrol,150,55.4,1.4
     *         ,A6,2016,1650,Automatic,36203,Diesel,20,64.2,2
     *   ,A1,2016,11000,Manual,29946,Petrol,30,55.4,1.4
     * ,A4,2017,500,Automatic,25952,Diesel,145,67.3,2
     * test1,A3,2019,17300,Manual,1998,Petrol,145,49.6,1
     * test2,A1,2016,13900,Automatic,32260,Petrol,30,58.9,1.4
     * test3,A6,2016,1325,Automatic,76788,Diesel,30,61.4,2
     * test4,A4,2016,500,Manual,75185,Diesel,20,70.6,2
     * test4,A3,2015,10200,Manual,46112,Petrol,20,60.1,1.4
     * test4,A1,2016,12000,Manual,22451,Petrol,30,55.4,1.4
     * test4,A3,2017,161000,Manual,28955,Petrol,145,58.9,1.4
     * test4,A6,2016,1650,Automatic,52198,Diesel,125,57.6,2
     * test2,Q3,2016,91700,Manual,44915,Diesel,145,52.3,2
     * test2,A3,2017,164000,Manual,21695,Petrol,30,58.9,1.4
     * test2,A6,2015,1540,Manual,47348,Diesel,30,61.4,2
     * test2,A3,2017,145000,Automatic,26156,Petrol,145,58.9,1.4
     * test1,Q3,2016,915700,Automatic,28396,Diesel,145,53.3,2
     * audi,A3,2014,139000,Automatic,30516,Petrol,30,56.5,1.4
     * audi,Q5,2016,919000,Automatic,37652,Diesel,200,47.1,2
     * audi,Q3,2016,919000,Automatic,37652,Diesel,200,47.1,2
     * }</pre>
     */


    private void initializeDatasetWithReadTaskVersionWithNulls() throws AnalysisException {
        IDatasetReaderFactory dataFrameReaderFactory = new IDatasetReaderFactory(session);
        datasetPath = TestsUtilities.getAbsoluteDatasetPath("car_20_NullEmpty.csv");
        StructType schema = TestsDatasetSchemas.getCarsCsvSchema();
        dataset = dataFrameReaderFactory.createDataframeReader(datasetPath, schema).read();
        StructField[] fields = dataset.schema().fields();
        //Take first column !!
        String columnName = fields[0].name();
        cardinalitiesTasks = factory.createCardinalitiesCalculator(dataset, columnName);
    }

    /**
     * Test to verify the calculation of the number of null values.
     *
     * <p>
     * This test checks the calculation of the number of null values in the dataset.
     * </p>
     *
     * <p>
     * Preconditions:
     * <ul>
     *   <li>Spark session is initialized.</li>
     *   <li>Dataset is loaded with data containing null values.</li>
     * </ul>
     * </p>
     *
     * <p>
     * This test asserts that:
     * <ul>
     *   <li>The calculated number of null values matches the expected value 4. we count as null values and the empties like "\t"..</li>
     * </ul>
     * </p>
     */

    @Test
    public void calculateNumberOfNullValues() throws AnalysisException {
        initializeDatasetWithReadTaskVersionWithNulls();
        cardinalitiesTasks.calculateNumberOfNullValues();
        long expectedNumberOfNullValues = 4;
        long actualNumberOfNullValues = cardinalitiesTasks.getNumberOfNullValues();
        assertEquals(expectedNumberOfNullValues, actualNumberOfNullValues);
    }



}




