package gr.uoi.cs.pythia.report;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import gr.uoi.cs.pythia.testshelpers.TestsUtilities;

public class MdReportTests {

  @Test
  public void testProduceReportMd() throws IOException {
    String reportPath = TestsUtilities.getResultsDir("report");
    File reportFile = new File(reportPath);
    AllReportTests.reportResource.getDatasetProfiler()
            .generateReport(ReportGeneratorConstants.MD_REPORT,
                    reportFile.getAbsolutePath());

    String expectedStatisticalReport = TestsUtilities.getExpectedDatasetReport(
            "people/expected_people_statistical_report.md");
    String actualStatisticalReport = TestsUtilities.getTextFromFile(
    		new File(reportPath + File.separator + "statistical_report.md"))
            .replace(AllReportTests.reportResource.getDatasetPath(), "");
    assertEquals(expectedStatisticalReport, actualStatisticalReport);

    String expectedHighDominanceReport = TestsUtilities.getExpectedDatasetReport(
            "people/expected_people_high_dominance_report.md");
    String actualHighDominanceReport = TestsUtilities.getTextFromFile(
            new File(reportPath + File.separator + "high_dominance_report.md"))
            .replace(AllReportTests.reportResource.getDatasetPath(), "");
    assertEquals(expectedHighDominanceReport, actualHighDominanceReport);

    String expectedLowDominanceReport = TestsUtilities.getExpectedDatasetReport(
            "people/expected_people_low_dominance_report.md");
    String actualLowDominanceReport = TestsUtilities.getTextFromFile(
            new File(reportPath + File.separator + "low_dominance_report.md"))
            .replace(AllReportTests.reportResource.getDatasetPath(), "");
    assertEquals(expectedLowDominanceReport, actualLowDominanceReport);

    String expectedOutliersReport = TestsUtilities.getExpectedDatasetReport(
            "people/expected_people_outliers_report.md");
    String actualOutliersReport = TestsUtilities.getTextFromFile(
            new File(reportPath + File.separator + "outliers_report.md"))
            .replace(AllReportTests.reportResource.getDatasetPath(), "");
    assertEquals(expectedOutliersReport, actualOutliersReport);
    
    String expectedRegressionReport = TestsUtilities.getExpectedDatasetReport(
            "cars/expected_regression_cars_report.md");
    String actualRegressionReport = TestsUtilities.getTextFromFile(
            new File(reportPath + File.separator + "regression_report.md"))
            .replace(AllReportTests.reportResource.getDatasetPath(), "");
    assertEquals(expectedRegressionReport, actualRegressionReport);
  }
}
