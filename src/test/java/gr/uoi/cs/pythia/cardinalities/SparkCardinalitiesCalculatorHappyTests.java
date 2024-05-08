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

import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * Test class for verifying the functionality of the SparkCardinalitiesCalculator.
 */
public class SparkCardinalitiesCalculatorHappyTests {

    private ICardinalitiesCalculator cardinalitiesTasks;
    private Dataset<Row> dataset;
    private SparkSession session;
    private String datasetPath;

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
        CardinalitiesCalculatorFactory factory = new CardinalitiesCalculatorFactory();
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
        datasetPath = TestsUtilities.getDatasetPath("car_20_NotNullEmptyValues.csv");
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
     *   <li>Dataset is loaded with data.</li>
     * </ul>
     * </p>
     *
     * <p>
     * This test asserts that:
     * <ul>
     *   <li>The calculated number of distinct values matches the expected value.</li>
     * </ul>
     * </p>
     */
    @Test
    public void calculateDistinctValuesTest() {
        cardinalitiesTasks.calculateDistincValues();
        long expectedNumberOfDistinctValues = 5;
        long actualNumberOfDistinctValues = cardinalitiesTasks.getNumberOfDistinctValues();
        assertEquals(expectedNumberOfDistinctValues, actualNumberOfDistinctValues);
    }

    /**
     * Test case to verify the calculation of the number of null values in an empty dataset.
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
    public void calculateNumberOfNullValuesEmptyDatasetTest() throws AnalysisException {
        StructType schema = new StructType().add("empty_column", "string");
        Dataset<Row> emptyDataset = session.createDataFrame(Collections.emptyList(), schema);
        String columnName = "empty_column";
        cardinalitiesTasks = new SparkCardinalitiesCalculator(emptyDataset, columnName);
        cardinalitiesTasks.calculateNumberOfNullValues();
        long actualNumberOfNullValues = cardinalitiesTasks.getNumberOfNullValues();
        assertEquals(0, actualNumberOfNullValues);
    }
}




