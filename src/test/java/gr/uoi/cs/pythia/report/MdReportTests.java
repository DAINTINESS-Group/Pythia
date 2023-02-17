package gr.uoi.cs.pythia.report;

import gr.uoi.cs.pythia.testshelpers.TestsUtilities;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class MdReportTests {

    @Test
    public void testProduceReportMd() throws IOException {
        String reportPath = TestsUtilities.getResultsDir("report") + File.separator + "test.md";
        File reportFile = new File(reportPath);
    	AllReportTests.reportResource.getDatasetProfiler()
                .generateReport(ReportGeneratorConstants.MD_REPORT,
                        reportFile.getAbsolutePath());

        String expectedText = TestsUtilities.getExpectedDatasetReport("people/expected_people_md_report.md");
        String actualString = TestsUtilities.getTextFromFile(reportFile);        
        assertEquals(expectedText, actualString);
    }
}
