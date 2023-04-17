package gr.uoi.cs.pythia.report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import gr.uoi.cs.pythia.model.DatasetProfile;

public class TxtReportGenerator implements IReportGenerator {

  public void produceReport(DatasetProfile datasetProfile, String outputDirectoryPath) 
		  throws IOException {
	  String absoluteFileName = new File(String.format("%s%sstatistical_report.txt",
  			outputDirectoryPath, File.separator)).getAbsolutePath();
    PrintWriter printWriter = new PrintWriter(new FileWriter(absoluteFileName));
    printWriter.write(datasetProfile.toString());
    printWriter.flush();
    printWriter.close();
  }
}
