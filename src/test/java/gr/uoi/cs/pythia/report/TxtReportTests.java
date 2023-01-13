package gr.uoi.cs.pythia.report;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sparkproject.guava.io.Resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class TxtReportTests {

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  @Test
  public void testProduceReportTxt() throws IOException {
    File reportFile = tempFolder.newFile("txt_report.txt");
    AllReportTests.reportResource.getDatasetProfiler()
            .generateReport(ReportGeneratorConstants.TXT_REPORT,
                            reportFile.getAbsolutePath());

    String expectedText = getExpectedString();
    String actualString = getTextFromFile(reportFile);
    assertEquals(expectedText, actualString);
  }

  private String getExpectedString() throws IOException {
    URL url = Resources.getResource("dummy_txt_report_tweets.txt");
    return Resources.toString(url, StandardCharsets.UTF_8).replace("\r", "");
  }

  private String getTextFromFile(File file) throws IOException {
     String text = new BufferedReader(new InputStreamReader(file.toURI().toURL().openStream()))
             .lines()
             .collect(Collectors.joining(System.lineSeparator()))
             .replace("\r", "")
             .replace(AllReportTests.reportResource.getDatasetPath(), "");
     return text + "\n";
  }
}
