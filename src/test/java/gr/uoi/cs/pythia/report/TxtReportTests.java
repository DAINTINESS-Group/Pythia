package gr.uoi.cs.pythia.report;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import gr.uoi.cs.pythia.testshelpers.TestsUtilities;

public class TxtReportTests {

  @Test
  public void testProduceReportTxt() throws IOException {
    String reportPath = TestsUtilities.getResultsDir("report");
    AllReportTests.reportResource.getDatasetProfiler()
            .generateReport(ReportGeneratorConstants.TXT_REPORT,
            		new File(reportPath).getAbsolutePath());

    String expectedStatisticalReport = TestsUtilities.getExpectedDatasetReport(
    		"people/expected_people_statistical_report.txt");
    String actualStatisticalReport = TestsUtilities.getTextFromFile(
    		new File(reportPath + File.separator + "statistical_report.txt"))
    		.replace(AllReportTests.reportResource.getDatasetPath(), "");
    assertEquals(expectedStatisticalReport, actualStatisticalReport);
  }
}
