package gr.uoi.cs.pythia.generalinfo;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SparkBasicInfoCalculatorTestsRainy {


    private IBasicInfoCalculator basicInfoCalculator;


    /**
     * Test case to handle the calculation of the number of lines in the dataset under rainy scenario V1.
     *
     * <p>
     * Rainy Scenario V1:
     * <ul>
     *   <li>Dataset: Null</li>
     *   <li>Session: Not Null</li>
     * </ul>
     *
     * <p>
     * This test simulates the scenario where the dataset is null while the session is not null.
     * It asserts that:
     * <ul>
     *   <li>The calculated number of lines is not null.</li>
     *   <li>The calculated number of lines matches the expected value (-1).</li>
     * </ul>
     */
    @Test
    public void calculateNumberOfLinesInDatasetRainyV1(){
        Dataset<Row> nullDataset = null;
        SparkSession session = AllGenInfoTests.genInfoResource.getSession();
        String datasetPath = AllGenInfoTests.genInfoResource.getDatasetPath();
        basicInfoCalculator = new SparkBasicInfoCalculator(nullDataset,session,datasetPath);
        basicInfoCalculator.calculateNumberOfLinesInDataset();
        long calculatedlines = basicInfoCalculator.getNumberOfLines();
        long expectedLines = SparkBasicInfoCalculator.ERROR_VALUE_NUMBER_OF_LINES;
        assertNotNull(calculatedlines);
        assertEquals(expectedLines, calculatedlines);
        //System.out.println(basicInfoSparkManager.getNumberOfLines());
    }
    /**
     * Test case to handle the calculation of the number of lines in the dataset under rainy scenario V2.
     *
     * <p>
     * Rainy Scenario V2:
     * <ul>
     *   <li>Dataset: Empty</li>
     *   <li>Session: Not Null</li>
     * </ul>
     *
     * <p>
     * This test simulates the scenario where the dataset is empty while the session is not null.
     * It asserts that:
     * <ul>
     *   <li>The calculated number of lines is not null.</li>
     *   <li>The calculated number of lines matches the expected value (0 for an empty dataset).</li>
     * </ul>
     */
    @Test
    public void calculateNumberOfLinesInDatasetRainyV2(){

        SparkSession sparkSession = AllGenInfoTests.genInfoResource.getSession();
        Dataset<Row> emptyDataset = sparkSession.emptyDataFrame();
        String datasetPath = AllGenInfoTests.genInfoResource.getDatasetPath();
        basicInfoCalculator = new SparkBasicInfoCalculator(emptyDataset,sparkSession,datasetPath);
        basicInfoCalculator.calculateNumberOfLinesInDataset();
        long calculatedlines = basicInfoCalculator.getNumberOfLines();
        long expectedLines = 0;
        assertNotNull(calculatedlines);
        assertEquals(expectedLines, calculatedlines);

    }
    /**
     * Test case to handle the calculation of the file size in megabytes in the dataset under rainy scenario V1.
     *
     * <p>
     * Rainy Scenario V1:
     * <ul>
     *   <li>Dataset: Not Null</li>
     *   <li>Session: Null</li>
     * </ul>
     *
     * <p>
     * This test simulates the scenario where the dataset is not null while the session is null.
     * It asserts that:
     * <ul>
     *   <li>The calculated file size is not null.</li>
     *   <li>The calculated file size matches the expected value (-1.0 for a null session).</li>
     * </ul>
     */

    @Test
    public void calculateFileSizeRainyV1(){

        SparkSession sessionInManager = null;
       // SparkSession session = AllGenInfoTests.genInfoResource.getSession();
        Dataset<Row> dataset = AllGenInfoTests.genInfoResource.getDataset();
        String datasetPath = AllGenInfoTests.genInfoResource.getDatasetPath();
        basicInfoCalculator =  new SparkBasicInfoCalculator(dataset,sessionInManager,datasetPath);
        basicInfoCalculator.calculateFileSize();
        Double calculatedfileSize = basicInfoCalculator.getFileSize();
        Double expectedfileSize = SparkBasicInfoCalculator.ERROR_VALUE_FILE_SIZE;
        assertNotNull(calculatedfileSize);
        assertEquals(expectedfileSize, calculatedfileSize);

    }
    /**
     * Test case to handle the calculation of the file size in megabytes in the dataset under rainy scenario V2.
     *
     * <p>
     * Rainy Scenario V2:
     * <ul>
     *   <li>Dataset: Not Null</li>
     *   <li>Dataset Path: Null</li>
     * </ul>
     *
     * <p>
     * This test simulates the scenario where the dataset is not null while the dataset path is null.
     * It asserts that:
     * <ul>
     *   <li>The calculated file size is not null.</li>
     *   <li>The calculated file size matches the expected value (-1.0 for a null dataset path).</li>
     * </ul>
     */
    @Test
    public void calculateFileSizeRainyV2(){
        SparkSession session = AllGenInfoTests.genInfoResource.getSession();
        Dataset<Row> dataset = AllGenInfoTests.genInfoResource.getDataset();
        String datasetPath = null;
        basicInfoCalculator = new SparkBasicInfoCalculator(dataset,session,datasetPath);
        basicInfoCalculator.calculateFileSize();
        Double calculatedfileSize = basicInfoCalculator.getFileSize();
        Double expectedfileSize = SparkBasicInfoCalculator.ERROR_VALUE_FILE_SIZE;
        assertNotNull(calculatedfileSize);
        assertEquals(expectedfileSize, calculatedfileSize);

    }


}
