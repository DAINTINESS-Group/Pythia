package gr.uoi.cs.pythia.generalinfo;

import gr.uoi.cs.pythia.model.DatasetProfile;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RainyManagerTests {

    private DatasetProfile datasetProfile;
    private InfoManager manager;

    @Before
    public void setUp() {
        IBasicInfoCalculator calculator = AllGenInfoTests.genInfoResource.getBasicInfoSparkManager();
        datasetProfile = null;
        manager = new InfoManager(datasetProfile, calculator);
    }


    /**
     * Test case to verify the calculation of the extra information in the dataset under Rainy scenario V1.
     *
     * <p>
     * Rainy Scenario V1:
     * <ul>
     *   <li>datasetProfile: Null</li>
     *   <li>manager: Not Null</li>
     * </ul>
     *
     * <p>
     * This test verifies that:
     * <ul>
     *   <li>No executions just print errors </li>
     *
     * </ul>
     */
    @Test
    public void testCalculateExtraInfoV1() {
        int status = manager.runAllCalculations();
        assertEquals(0,status);
        //No Asserts just prints
    }


    /**
     * Test case to verify the calculation of the extra information in the dataset under Rainy scenario V2.
     *
     * <p>
     * Rainy Scenario V2:
     * <ul>
     *   <li>datasetProfile:Not Null</li>
     *   <li>manager:Null</li>
     * </ul>
     *
     * <p>
     * This test verifies that:
     * <ul>
     *   <li>No executions just prints errors </li>
     *
     * </ul>
     */

    @Test
    public void testCalculateExtraInfoV2() {
        IBasicInfoCalculator calculator = null;
        manager = new InfoManager(new DatasetProfile(null,null,null,null),calculator);
       int status= manager.runAllCalculations();
        assertEquals(0,status);
        //No Asserts just prints
    }

}
