package gr.uoi.cs.pythia.cardinalities;

import gr.uoi.cs.pythia.config.SparkConfig;
import gr.uoi.cs.pythia.model.CardinalitiesProfile;
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
import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * Test class for verifying the functionality of the SparkCardinalitiesCalculator under rainy scenarios.
 */
public class SparkCardinalitiesCalculatorRainyTestsV1 {

    private ICardinalitiesCalculator cardinalitiesTasks;
    private CardinalitiesCalculatorFactory factory = new CardinalitiesCalculatorFactory();
    private Dataset<Row> dataset;
    private SparkSession session;
    private String datasetPath;
    private CardinalitiesProfile profile;

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
        String columnName = fields[0].name();
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
     */
    private void initializeDatasetWithReadTask() throws AnalysisException {
        IDatasetReaderFactory dataFrameReaderFactory = new IDatasetReaderFactory(session);
        datasetPath = TestsUtilities.getAbsoluteDatasetPath("car_20_NullEmpty.csv");
        StructType schema = TestsDatasetSchemas.getCarsCsvSchema();
        dataset = dataFrameReaderFactory.createDataframeReader(datasetPath, schema).read();
    }

    private void setNullParametersDatasetColumnName(){
        cardinalitiesTasks = factory.createCardinalitiesCalculator(null, null);
        profile = cardinalitiesTasks.computeCardinalityProfile();
    }

    /**
     * Test method for asserting the number of null values calculation.
     */
    @Test
    public void assertNumberOfNullValues() {
        setNullParametersDatasetColumnName();
        // Assert the number of null values
        long actualNumberOfNullValues = profile.getNumberOfNullValues();
        assertEquals(-1, actualNumberOfNullValues);
    }

    /**
     * Test method for asserting the number of distinct values calculation.
     */
    @Test
    public void assertNumberOfDistinctValues() {
        setNullParametersDatasetColumnName();
        // Assert the number of distinct values
        long actualNumberOfDistinctValues = profile.getNumberOfDistinctValues();
        assertEquals(-1, actualNumberOfDistinctValues);
    }

    /**
     * Test method for asserting the number of null values calculation with valid column name.
     */

    private void setNullDatasetParameter(){
        StructField[] fields = dataset.schema().fields();
        String columnName = fields[0].name();
        cardinalitiesTasks = factory.createCardinalitiesCalculator(null, columnName);
        profile = cardinalitiesTasks.computeCardinalityProfile();
    }


    @Test
    public void assertNumberOfNullValuesWithValidColumnName() throws AnalysisException {
        setNullDatasetParameter();
        // Assert the number of null values
        long actualNumberOfNullValues = profile.getNumberOfNullValues();
        assertEquals(-1, actualNumberOfNullValues);
    }

    /**
     * Test method for asserting the number of distinct values calculation with valid column name.
     */
    @Test
    public void assertNumberOfDistinctValuesWithValidColumnName() throws AnalysisException {
        setNullDatasetParameter();
        // Assert the number of distinct values
        long actualNumberOfDistinctValues = profile.getNumberOfDistinctValues();
        assertEquals(-1, actualNumberOfDistinctValues);
    }

    /**
     * Test method for asserting the number of null values calculation with valid dataset and null column name.
     */

    private void setNullColumnNameParameter(){
        // Execute the test scenario
        cardinalitiesTasks = factory.createCardinalitiesCalculator(dataset, null);
        profile = cardinalitiesTasks.computeCardinalityProfile();

    }

    @Test
    public void assertNumberOfNullValuesWithValidDatasetAndNullColumnName() {
        setNullColumnNameParameter();
        // Assert the number of null values
        long actualNumberOfNullValues = profile.getNumberOfNullValues();
        assertEquals(-1, actualNumberOfNullValues);
    }

    /**
     * Test method for asserting the number of distinct values calculation with valid dataset and null column name.
     */
    @Test
    public void assertNumberOfDistinctValuesWithValidDatasetAndNullColumnName() {
        setNullColumnNameParameter();
        // Assert the number of distinct values
        long actualNumberOfDistinctValues = profile.getNumberOfDistinctValues();
        assertEquals(-1, actualNumberOfDistinctValues);
    }

    /**
     * Test method for asserting the calculation of the number of null values and distinct values in an empty dataset.
     */

    private void setupEmptyDataset(){
        String columnName = "empty_column";
        StructType schema = new StructType().add(columnName, "string");
        Dataset<Row> emptyDataset = session.createDataFrame(Collections.emptyList(), schema);
        cardinalitiesTasks = new SparkCardinalitiesCalculator(emptyDataset,columnName);
        profile = cardinalitiesTasks.computeCardinalityProfile();

    }


    /**
     * Test case to verify the calculation of the number of null values  in an empty dataset.
     *
     * <p>
     * This test case checks the calculation of the number of null values in an empty column.
     * </p>
     *
     * <p>
     * Preconditions:
     * <ul>
     *   <li>Spark session is initialized.</li>
     *   <li>An empty dataset is created.</li>
     * </ul>
     * </p>
     *
     * <p>
     * This test asserts that:
     * <ul>
     *   <li>The calculated number of null values in the empty dataset is zero.</li>
     * </ul>
     * </p>
     *
     * @throws AnalysisException if an error occurs during dataset initialization
     */
    @Test
    public void calculateNullValuesInEmptyDataset() throws AnalysisException {
        setupEmptyDataset();
        // Assert the number of null values
        long actualNumberOfNullValues = profile.getNumberOfNullValues();
        assertEquals(0, actualNumberOfNullValues);

    }
    /**
     * Test case to verify the calculation of the number of distinct values  in an empty dataset.
     *
     * <p>
     * This test case checks the calculation of the number of distinct values in an empty column.
     * </p>
     *
     * <p>
     * Preconditions:
     * <ul>
     *   <li>Spark session is initialized.</li>
     *   <li>An empty dataset is created.</li>
     * </ul>
     * </p>
     *
     * <p>
     * This test asserts that:
     * <ul>
     *   <li>The calculated number of distinct values in the empty dataset is zero.</li>
     * </ul>
     * </p>
     *
     * @throws AnalysisException if an error occurs during dataset initialization
     */
    @Test
    public void calculateDistinctValuesInEmptyDataset() throws AnalysisException {
        setupEmptyDataset();
        // Assert the number of distinct values
        long actualNumberOfDistinctValues = profile.getNumberOfDistinctValues();
        assertEquals(0, actualNumberOfDistinctValues);
    }



    /**
     * Test method for asserting the calculation of the number of null values and distinct values with a wrong column name.
     */
    private void setWrongColumnNameParameter(){
        String columnName = "Wrong_column";
        cardinalitiesTasks = factory.createCardinalitiesCalculator(dataset, columnName);
        profile = cardinalitiesTasks.computeCardinalityProfile();


    }


    /**
     * Test case to verify the calculation of the number of null values in dataset.
     *
     * <p>
     * This test case checks the calculation of the number of null values with a wrong_name column.
     * </p>
     *
     * <p>
     * Preconditions:
     * <ul>
     *   <li>Spark session is initialized.</li>
     *   <li>Dataset is created.</li>
     * </ul>
     * </p>
     *
     * <p>
     * This test asserts that:
     * <ul>
     *   <li>The calculated number of null values in the dataset is -1. We cannot calculate with wrong Column.</li>
     * </ul>
     * </p>
     *
     * @throws AnalysisException if an error occurs during dataset initialization
     */


    @Test
    public void calculateNullValuesWithWrongColumnName() {
        setWrongColumnNameParameter();
        // Assert the number of null values
        long actualNumberOfNullValues = profile.getNumberOfNullValues();
        assertEquals(-1, actualNumberOfNullValues);
    }
    /**
     * Test case to verify the calculation of the number of distinct values in dataset.
     *
     * <p>
     * This test case checks the calculation of the number of distinct values with a wrong_name column.
     * </p>
     *
     * <p>
     * Preconditions:
     * <ul>
     *   <li>Spark session is initialized.</li>
     *   <li>Dataset is created.</li>
     * </ul>
     * </p>
     *
     * <p>
     * This test asserts that:
     * <ul>
     *   <li>The calculated number of distinct values in the dataset is -1 we cannot calculated with wrong columName.</li>
     *
     * </ul>
     * </p>
     *
     * @throws AnalysisException if an error occurs during dataset initialization
     */
    @Test
    public void calculateDistinctValuesWithWrongColumnName() {
        setWrongColumnNameParameter();
        // Assert the number of distinct values
        long actualNumberOfDistinctValues = profile.getNumberOfDistinctValues();
        assertEquals(-1, actualNumberOfDistinctValues);
    }




}
