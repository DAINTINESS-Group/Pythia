package gr.uoi.cs.pythia.report;

import gr.uoi.cs.pythia.model.DatasetProfile;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class TxtReportGenerator implements IReportGenerator {

  public void produceReport(DatasetProfile datasetProfile, String path) throws IOException {
    PrintWriter printWriter = new PrintWriter(new FileWriter(path));
    printWriter.write(datasetProfile.toString());
    printWriter.flush();
    printWriter.close();
  }
}
