package gr.uoi.cs.pythia.generalinfo;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
public class SparkBasicInfoCalculatorTestsHappy {

    private IBasicInfoCalculator basicInfoCalculator;

    @Before
    public void init() {
         basicInfoCalculator = AllGenInfoTests.genInfoResource.getBasicInfoSparkManager();
    }


    /**
     * Test case to verify the calculation of the number of lines in the dataset under happy scenario V1.
     *
     * <p>
     * Happy Scenario V1:
     * <ul>
     *   <li>Dataset: Not Null</li>
     *   <li>Session: Not Null</li>
     * </ul>
     *
     * <p>
     * This test asserts that:
     * <ul>
     *   <li>The calculated number of lines is not null.</li>
     *   <li>The calculated number of lines matches the expected value.</li>
     * </ul>
     */
    /**
     * This file is a copy of cars_100k.csv with modifications in the last lines
     * such as missing values and empty lines.
     *
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
        long calculatedlines = basicInfoCalculator.getNumberOfLines();
        long expectedLines = 108539;
        assertNotNull(calculatedlines);
        assertEquals(expectedLines, calculatedlines);
        //System.out.println(basicInfoSparkManager.getNumberOfLines());
    }


    /**
     * Test case to verify the calculation of the file size in megabytes in the dataset under happy scenario V1.
     *
     * <p>
     * Happy Scenario V1:
     * <ul>
     *   <li>Dataset: Not Null</li>
     *   <li>Session: Not Null</li>
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
        Double calculatedFileSize = basicInfoCalculator.getFileSize();
        assertNotNull(calculatedFileSize);
        Double expectedFileSizeInMb = 5.83;
        assertEquals(expectedFileSizeInMb, calculatedFileSize);
    }

}
