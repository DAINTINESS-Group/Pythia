package gr.uoi.cs.pythia.report;

import gr.uoi.cs.pythia.testshelpers.TestsUtilities;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class MdReportTests {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testProduceReportTxt() throws IOException {
        File reportFile = tempFolder.newFile("test.md");
        AllReportTests.reportResource.getDatasetProfiler()
                .generateReport(ReportGeneratorConstants.MD_REPORT,
                        reportFile.getAbsolutePath());

        String expectedText = TestsUtilities.getExpectedDatasetReport("people/expected_people_md_report.md");
        String actualString = TestsUtilities.getTextFromFile(reportFile);
        assertEquals(expectedText, actualString);
    }
}
