package gr.uoi.cs.pythia.histogram;

import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.histogram.Histogram;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class HistogramTests {

    @Test
    public void test() throws IOException {
        List<Column> columns = AllHistogramTests.histogramResource.getDatasetProfile().getColumns();
        List<Histogram> histograms = new ArrayList<>();
        for (Column column : columns) {
            if (column.getHistogram() == null)
                continue;
            histograms.add(column.getHistogram());
        }
        assertEquals(histograms.get(0).toString(), getExpectedHistogramFromNoNaNsColumn());
        assertEquals(histograms.get(5).toString(), getExpectedHistogramFromNaNsColumn());
    }

    private String getExpectedHistogramFromNoNaNsColumn() {
        return  "[1,1.9): 145 values\n" +
                "[1.9,2.8): 50 values\n" +
                "[2.8,3.7): 108 values\n" +
                "[3.7,4.6): 80 values\n" +
                "[4.6,5.5): 130 values\n" +
                "[5.5,6.4): 34 values\n" +
                "[6.4,7.3): 23 values\n" +
                "[7.3,8.2): 46 values\n" +
                "[8.2,9.1): 14 values\n" +
                "[9.1,10]: 69 values\n";
    }

    private String getExpectedHistogramFromNaNsColumn() {
        return  "NaN: 16 values\n" +
                "[1,1.9): 402 values\n" +
                "[1.9,2.8): 30 values\n" +
                "[2.8,3.7): 28 values\n" +
                "[3.7,4.6): 19 values\n" +
                "[4.6,5.5): 30 values\n" +
                "[5.5,6.4): 4 values\n" +
                "[6.4,7.3): 8 values\n" +
                "[7.3,8.2): 21 values\n" +
                "[8.2,9.1): 9 values\n" +
                "[9.1,10]: 132 values\n";
    }

}
