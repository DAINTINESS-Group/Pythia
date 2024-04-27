package gr.uoi.cs.pythia.generalinfo;

import gr.uoi.cs.pythia.model.DatasetProfile;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HappyInfoManagerTests {

    private DatasetProfile datasetProfile;
    private InfoManager manager;

    @Before
    public void setUp() {
        IBasicInfoCalculator calculator = AllGenInfoTests.genInfoResource.getBasicInfoSparkManager();
        datasetProfile = new DatasetProfile(null,null,null,null);
        manager = new InfoManager(datasetProfile, calculator);
    }


    /**
     * Test case to verify the calculation of the extra information in the dataset under happy scenario V1.
     *
     * <p>
     * Happy Scenario V1:
     * <ul>
     *   <li>datasetProfile: Not Null</li>
     *   <li>manager: Not Null</li>
     * </ul>
     *
     * <p>
     * This test verifies that:
     * <ul>
     *   <li>The number of lines calculated matches the expected value.</li>
     *   <li>The file size calculated matches the expected value.</li>
     * </ul>
     */
    @Test
    public void testCalculateExtraInfoV1() {
       int status = manager.runAllCalculations();
        // Assert
        assertEquals(108539, datasetProfile.getNumberOfLines());
        assertEquals(5.83, datasetProfile.getFileSize(), 0.0);
        assertEquals(1, status);
    }
}
