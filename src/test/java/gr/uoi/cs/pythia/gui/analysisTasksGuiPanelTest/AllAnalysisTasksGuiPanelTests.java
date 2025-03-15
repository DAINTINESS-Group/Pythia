package gr.uoi.cs.pythia.gui.analysisTasksGuiPanelTest;

import gr.uoi.cs.pythia.Appcontroller.AppController;
import org.junit.AfterClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.lang.reflect.Field;


@RunWith(Suite.class)
@Suite.SuiteClasses({
        ClusteringGUITest.class,
        ClusteringParameterValidatorTest.class,
        CorrelationsGUITest.class,
        CorrelationsParameterValidatorTest.class,
        DatasetWriterGUITest.class,
        DatasetWriterParameterValidatorTest.class,
        DominanceParametersGUITest.class,
        DominanceParameterValidatorTest.class,
        HighlightParametersGUITest.class,
        HighlightParameterValidatorTest.class,
        HistogramGUITest.class,
        HistogramParameterValidatorTest.class,
        LabelingSystemGUITest.class,
        OutlierAnalysisGUITest.class,
        OutlierParameterValidatorTest.class,
        RegressionGUITest.class,
        ReportParameterValidatorTest.class,
        RegressionGUITest.class,
        ReportGeneratorGUITest.class,


})


public class AllAnalysisTasksGuiPanelTests{

    @AfterClass
    public static void tearDown() throws Exception{
        setDataset();
        setDatasetProfile();

    }
    private static void setDataset() throws Exception{
        Field field = AppController.class.getDeclaredField("dataset");
        field.setAccessible(true);
        field.set(AppController.getInstance(), null);
    }

    private static void setDatasetProfile() throws Exception{
        Field field = AppController.class.getDeclaredField("datasetProfile");
        field.setAccessible(true);
        field.set(AppController.getInstance(), null);
    }
}
