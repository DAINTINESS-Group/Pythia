package gr.uoi.cs.pythia.outliers;

import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.OutlierProfile;
import gr.uoi.cs.pythia.model.outlier.OutlierResult;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
public abstract class AbstractOutlierTest {

    protected abstract void identifyOutliers(Dataset<Row> dataset, DatasetProfile datasetProfile);
    protected abstract List<OutlierResult> createExpectedOutlierResults();

    // Todo:
    //  1) add checks to nameColumn e.g tax 3 4 6 4 -> tax 3 4 6 4
    //  2) refactoring tests



    @Test
    public void testIdentifyOutliers() {
        Dataset<Row> dataset = AllOutlierTests.outlierResource.getDataset();
        DatasetProfile datasetProfile = AllOutlierTests.outlierResource.getDatasetProfile();
        List<OutlierResult> expected = createExpectedOutlierResults();
        identifyOutliers(dataset, datasetProfile);
        List<OutlierResult> actual = getOutlierResults(datasetProfile);

        assertOutlierResults(expected, actual);


        System.out.print("Expected:\n");
        for (int i=0; i<expected.size(); i++) {
            System.out.println(expected.get(i));
        }
        System.out.print("Actual:\n");
        System.out.println("----------");
        for (int i=0; i<actual.size(); i++) {
            System.out.println(actual.get(i));
        }
    }

    private List<OutlierResult> getOutlierResults(DatasetProfile datasetProfile) {
        List<OutlierResult> actual = new ArrayList<>();
        for (Column column : datasetProfile.getColumns()) {
            OutlierProfile columnOutlierProfile = column.getOutlierProfile();
            if (columnOutlierProfile == null) continue;
            actual.addAll(columnOutlierProfile.getOutlierResultList());
        }
        return actual;
    }

    private void assertOutlierResults(List<OutlierResult> expected, List<OutlierResult> actual) {
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).getValue(), actual.get(i).getValue());
            assertEquals(expected.get(i).getScore(), actual.get(i).getScore());
            assertEquals(expected.get(i).getPosition(), actual.get(i).getPosition());
        }
    }
}
