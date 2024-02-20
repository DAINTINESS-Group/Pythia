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
    File reportFile = new File(reportPath);
    AllReportTests.reportResource.getDatasetProfiler()
    	.generateReport(ReportGeneratorConstants.TXT_REPORT,
    			reportFile.getAbsolutePath());

    String expectedStatisticalReport = TestsUtilities.getExpectedDatasetReport(
    		"people/expected_people_statistical_report.txt");
    String actualStatisticalReport = TestsUtilities.getTextFromFile(
    		new File(reportPath + File.separator + "statistical_report.txt"))
    		.replace(AllReportTests.reportResource.getDatasetPath(), "");
    assertEquals(expectedStatisticalReport, actualStatisticalReport);
    
    String expectedHighDominanceReport = TestsUtilities.getExpectedDatasetReport(
            "people/expected_people_high_dominance_report.txt");
    String actualHighDominanceReport = TestsUtilities.getTextFromFile(
            new File(reportPath + File.separator + "high_dominance_report.txt"))
            .replace(AllReportTests.reportResource.getDatasetPath(), "");
    assertEquals(expectedHighDominanceReport, actualHighDominanceReport);
    
    String expectedLowDominanceReport = TestsUtilities.getExpectedDatasetReport(
            "people/expected_people_low_dominance_report.txt");
    String actualLowDominanceReport = TestsUtilities.getTextFromFile(
            new File(reportPath + File.separator + "low_dominance_report.txt"))
            .replace(AllReportTests.reportResource.getDatasetPath(), "");
    assertEquals(expectedLowDominanceReport, actualLowDominanceReport);
    
    String expectedOutliersReport = TestsUtilities.getExpectedDatasetReport(
            "people/expected_people_outliers_report.txt");
    String actualOutliersReport = TestsUtilities.getTextFromFile(
            new File(reportPath + File.separator + "outliers_report.txt"))
            .replace(AllReportTests.reportResource.getDatasetPath(), "");
    assertEquals(expectedOutliersReport, actualOutliersReport);
    
    String expectedRegressionReport = TestsUtilities.getExpectedDatasetReport(
            "people/expected_people_regression_report.txt");
    String actualRegressionReport = TestsUtilities.getTextFromFile(
            new File(reportPath + File.separator + "regression_report.txt"))
            .replace(AllReportTests.reportResource.getDatasetPath(), "");
    assertEquals(expectedRegressionReport, actualRegressionReport);
    
    String expectedClusteringReport = TestsUtilities.getExpectedDatasetReport(
            "people/expected_people_clustering_report.txt");
    String actualClusteringReport = TestsUtilities.getTextFromFile(
            new File(reportPath + File.separator + "clustering_report.txt"))
            .replace(AllReportTests.reportResource.getDatasetPath(), "");
    assertEquals(expectedClusteringReport, actualClusteringReport);
  }
}
