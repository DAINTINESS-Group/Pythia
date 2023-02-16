package gr.uoi.cs.pythia.correlations;

import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.CorrelationsProfile;
import gr.uoi.cs.pythia.model.DatasetProfile;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PearsonCorrelationsTests {

    @Test
    public void testPearsonCorrelations() {
        DatasetProfile datasetProfile = AllCorrelationsTests.correlationsResource.getDatasetProfile();
        List<Column> columns = datasetProfile.getColumns();

        for (Column column : columns) {
            CorrelationsProfile correlationsProfile = column.getCorrelationsProfile();

            switch (column.getName()) {
                case "name":
                    assertNull(correlationsProfile);
                    break;
                case "age": {
                    Map<String, Double> correlations = correlationsProfile.getAllCorrelations();
                    assertEquals(correlations.get("money"), -0.9862413826124555, 0.01);
                    assertNull(correlations.get("name"));
                    break;
                }
                case "money": {
                    Map<String, Double> correlations = correlationsProfile.getAllCorrelations();
                    assertEquals(correlations.get("age"), -0.9862413826124555, 0.01);
                    assertNull(correlations.get("name"));
                    break;
                }
            }
        }
    }


}
