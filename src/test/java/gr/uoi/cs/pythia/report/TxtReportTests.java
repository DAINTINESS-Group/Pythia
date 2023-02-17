package gr.uoi.cs.pythia.report;

import gr.uoi.cs.pythia.testshelpers.TestsUtilities;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TxtReportTests {

  @Test
  public void testProduceReportTxt() throws IOException {
    String reportPath = TestsUtilities.getResultsDir("report") + File.separator + "test.txt";
    File reportFile = new File(reportPath);
    AllReportTests.reportResource.getDatasetProfiler()
            .generateReport(ReportGeneratorConstants.TXT_REPORT,
                            reportFile.getAbsolutePath());

    String expectedText = TestsUtilities.getExpectedDatasetReport("people/expected_people_txt_report.txt");
    String actualString = TestsUtilities.getTextFromFile(reportFile)
                          .replace(AllReportTests.reportResource.getDatasetPath(), "");
    assertEquals(expectedText, actualString);
  }
}
