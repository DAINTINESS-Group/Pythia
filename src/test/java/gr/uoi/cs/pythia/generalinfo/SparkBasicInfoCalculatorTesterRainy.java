package gr.uoi.cs.pythia.generalinfo;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SparkBasicInfoCalculatorTesterRainy {


    private SparkBasicInfoCalculator basicInfoCalculator;


    /**
     * Test case to handle the calculation of the number of lines in the dataset under rainy scenario V1.
     *
     * <p>
     * Rainy Scenario V1:
     * <ul>
     *   <li>Dataset: Null</li>
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
        String datasetPath = AllGenInfoTests.genInfoResource.getDatasetPath();
        basicInfoCalculator = new SparkBasicInfoCalculator(null,datasetPath);
        basicInfoCalculator.calculateNumberOfLinesInDataset();
        long actualLines = basicInfoCalculator.getNumberOfLines();
        long expectedLines = SparkBasicInfoCalculator.ERROR_VALUE_NUMBER_OF_LINES;
        assertEquals(expectedLines, actualLines);
    }
    /**
     * Test case to handle the calculation of the number of lines in the dataset under rainy scenario V2.
     *
     * <p>
     * Rainy Scenario V2:
     * <ul>
     *   <li>Dataset: Empty</li>
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
        basicInfoCalculator = new SparkBasicInfoCalculator(emptyDataset,datasetPath);
        basicInfoCalculator.calculateNumberOfLinesInDataset();
        long actualLines = basicInfoCalculator.getNumberOfLines();
        long expectedLines = 0;

        assertEquals(expectedLines, actualLines);



    }
    /**
     * Test case to handle the calculation of the file size in megabytes in the dataset under rainy scenario V2.
     *
     * <p>
     * Rainy Scenario V2:
     * <ul>
     *   <li>FilePath: Null</li>
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
        Dataset<Row> dataset = AllGenInfoTests.genInfoResource.getDataset();
        basicInfoCalculator = new SparkBasicInfoCalculator(dataset,null);
        basicInfoCalculator.calculateFileSize();
        Double actualFileSize = basicInfoCalculator.getFileSize();
        Double expectedFileSize = SparkBasicInfoCalculator.ERROR_VALUE_FILE_SIZE;
        assertNotNull(actualFileSize);
        assertEquals(expectedFileSize, actualFileSize);

    }

    /**
     * Test case to handle the calculation of the file size in megabytes in the dataset under rainy scenario V3.
     *
     * <p>
     * Rainy Scenario V3:
     * <ul>
     *   <li>Dataset: Empty</li>
     *   <li>FilePath:Not Null</li>
     * </ul>
     *
     * <p>
     * This test simulates the scenario where the dataset is empty while the dataset path is not Null.
     * It asserts that:
     * <ul>
     *   <li>The calculated file size is not null.</li>
     *   <li>The calculated file size matches the expected value (0.0 for a empty dataset).</li>
     * </ul>
     */
    @Test
    public void calculateFileSizeRainyV3(){

        String pathFile = "src/test/resources/datasets/empty.csv";
        basicInfoCalculator = new SparkBasicInfoCalculator(null,pathFile);
        Double expectedFileSize = 0.0;
        basicInfoCalculator.calculateFileSize();
        Double actualFileSize = basicInfoCalculator.getFileSize();
        assertNotNull(actualFileSize);
        assertEquals(expectedFileSize, actualFileSize);

    }

    /**
     * Test case to handle the calculation of the file size in megabytes in the dataset under rainy scenario V3.
     *
     * <p>
     * Rainy Scenario V3:
     * <ul>
     *   <li>FilePath: Wrong Path</li>
     * </ul>
     *
     * <p>
     * This test simulates the scenario where the dataset is empty while the dataset path is not Null.
     * It asserts that:
     * <ul>
     *   <li>The calculated file size is not null.</li>
     *   <li>The calculated file size matches the expected value (-1.0 for a empty dataset).</li>
     * </ul>
     */
    @Test
    public void calculateFileSizeRainyV4(){
        String pathFile = "WrongPath";
        basicInfoCalculator = new SparkBasicInfoCalculator(null,pathFile);
        Double expectedFileSize = SparkBasicInfoCalculator.ERROR_VALUE_FILE_SIZE;
        basicInfoCalculator.calculateFileSize();
        Double actualFileSize = basicInfoCalculator.getFileSize();
        assertNotNull(actualFileSize);
        assertEquals(expectedFileSize, actualFileSize);

    }




}
