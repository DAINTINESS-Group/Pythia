package gr.uoi.cs.pythia.gui.guiScores;


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
      CustomTableCellRendererTest.class,
        DataPanelTest.class,
        DataTypeDetectorUITest.class,
        ScorePanelTest.class
})
public class AllGuiScoreTests{

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

}
