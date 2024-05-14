package gr.uoi.cs.pythia.generalinfo;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SparkBasicInfoCalculatorTesterHappy {

    private SparkBasicInfoCalculator basicInfoCalculator;

    @Before
    public void init() {
         basicInfoCalculator = (SparkBasicInfoCalculator) AllGenInfoTests.genInfoResource.getBasicInfoSparkManager();
    }


    /**
     * Test case to verify the calculation of the number of lines in the dataset under happy scenario V1.
     *
     * <p>
     * Happy Scenario V1:
     * <ul>
     *   <li>Dataset: Not Null</li>
     * </ul>
     *
     * <p>
     * This test asserts that:
     * <ul>
     *   <li>The calculated number of lines is not null.</li>
     *   <li>The calculated number of lines matches the expected value.</li>
     * </ul>
     *
     * This file is a copy of cars_100k.csv with modifications in the last lines
     * such as missing values and empty lines.
     * Example:
     * <pre>
     * NumberLines
     * 108533   vw,Eos,,3695,Automatic,,,,34.5,2.0
     * 108534   vw,Eos,,12495,Manual,,,125,58.9,2.0
     * 108535   vw,,2014,8950,,,,125,58.9,2.0
     * 108536
     * 108537   vw,,,,,,,,,
     * 108538   vw,Fox,2008,,,88102,,
     * 108539   vw,Fox,2009,1590,,,Petrol,200,42.0,1.4
     * 108540
     * 108541   vw,Fox,,1250,,82704,,150,46.3,1.2
     * 108542   vw,Fox,2007,2295,,,Petrol,,46.3,1.2
     * </pre>
     *
     * Using dataset = .read(); and .option("header", "true") in the read() method
     * ignores the empty lines (without characters) resulting in totalLines = 108539
     * (-2 empty lines, -1 header)
     */

    @Test
    public void calculateNumberOfLinesInDatasetHappyV1(){
        basicInfoCalculator.calculateNumberOfLinesInDataset();
        long actualLines = basicInfoCalculator.getNumberOfLines();
        long expectedLines = 108539;
        assertEquals(expectedLines, actualLines);

    }


    /**
     * Test case to verify the calculation of the file size in megabytes in the dataset under happy scenario V1.
     *
     * <p>
     * Happy Scenario V1:
     * <ul>
     *   <li>Dataset: Not Null</li>
     *   <li>FilePath: Not Null or Wrong </li>
     * </ul>
     *
     * <p>
     * This test asserts that:
     * <ul>
     *   <li>The calculated file size is not null.</li>
     *   <li>The calculated file size matches the expected value in megabytes.</li>
     * </ul>
     */

    @Test
    public void calculateFileSizeHappyV1(){
        basicInfoCalculator.calculateFileSize();
        Double actualFileSize = basicInfoCalculator.getFileSize();
        assertNotNull(actualFileSize);

        long realSizeExpected = 6114251;
        assertEquals(realSizeExpected, basicInfoCalculator.getFileSizeInBytes());
        /*
         * After asserting the file size in bytes, we use the same function (convertBytesInMegaBytes in SparkBasicInfoCalculator) to convert bytes to megabytes and check if the assertion holds.
         * The precise conversion from bytes to megabytes is 6114251 / (1024.0 * 1024.0) = 5.8310041427612305,
         * but the output in AllGenInfoTests.genInfoResource.convertBytesInMegaBytes(realSizeExpected) is 5.83.
         * Therefore, we need to verify this value with Double actualFileSize = basicInfoCalculator.getFileSize();
         */
        Double expectedFileSizeInMb = AllGenInfoTests.genInfoResource.convertBytesInMegaBytes(realSizeExpected);
        assertEquals(expectedFileSizeInMb , actualFileSize, 0.01);
    }


}
