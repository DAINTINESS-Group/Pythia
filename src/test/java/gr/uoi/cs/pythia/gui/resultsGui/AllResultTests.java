/*package gr.uoi.cs.pythia.gui.resultsGui;


import gr.uoi.cs.pythia.Appcontroller.AppController;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
        AnalysisPanelFactoryTest.class,
        AnalysisPanelTest.class,
        ClusteringPanelTest.class,
        CorrelationsPanelTest.class,
        DominancePanelTest.class,
        HistogramPanelTest.class,
        LabelingPanelTest.class,
        OutlierPanelTest.class,
        ResultsPanelManagerTest.class,
        RegressionPanelTest.class,
        StatisticsPanelTest.class

})

public class AllResultTests{


    @AfterClass
    public static void tearDown() {
        AppController.getInstance().setDataset(null);
        AppController.getInstance().setDatasetProfile(null);
    }
    public static void main(String[] args) {
        for (int i = 0; i < 30; i++) {
            System.out.println("Running tests, iteration: " + (i + 1));
            org.junit.runner.JUnitCore.runClasses(AllResultTests.class);
        }
    }
}
*/
package gr.uoi.cs.pythia.gui.resultsGui;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import gr.uoi.cs.pythia.model.DatasetProfile;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.AfterClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.lang.reflect.Field;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        AnalysisPanelFactoryTest.class,
        AnalysisPanelTest.class,
        ClusteringPanelTest.class,
        CorrelationsPanelTest.class,
        DominancePanelTest.class,
        HistogramPanelTest.class,
        LabelingPanelTest.class,
        OutlierPanelTest.class,
        ResultsPanelManagerTest.class,
        RegressionPanelTest.class,
        StatisticsPanelTest.class
})
public class AllResultTests {

    @AfterClass
    public static void tearDown() throws Exception{
        setDataset(null);
        setDatasetProfile(null);

    }
    private static void setDataset(Dataset<Row> dataset) throws Exception{
        Field field = AppController.class.getDeclaredField("dataset");
        field.setAccessible(true);
        field.set(AppController.getInstance(), dataset);
    }

    private static void setDatasetProfile(DatasetProfile datasetProfile) throws Exception{
        Field field = AppController.class.getDeclaredField("datasetProfile");
        field.setAccessible(true);
        field.set(AppController.getInstance(), datasetProfile);
    }
    /*
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 30; i++) {
            System.out.println("Running tests, iteration: " + (i + 1));

            Result result = org.junit.runner.JUnitCore.runClasses(AllResultTests.class);

            // Εμφάνιση αποτελεσμάτων
            if (result.wasSuccessful()) {
                //System.out.println("All tests passed in iteration " + (i + 1));
            } else {
                System.out.println("Some tests failed in iteration " + (i + 1));
                for (Failure failure : result.getFailures()) {
                    System.out.println("Failed test: " + failure.getDescription().getMethodName());
                    System.out.println("Failure message: " + failure.getMessage());
                    System.out.println("Exception: " + failure.getException());
                }
            }

            System.out.println("----------------------------------------");
        }
    }*/
}