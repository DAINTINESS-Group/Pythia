package gr.uoi.cs.pythia.report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.GsonBuilder;

import gr.uoi.cs.pythia.model.DatasetProfile;

public class JsonReportGenerator implements IReportGenerator {

  @Override
  public void produceReport(DatasetProfile datasetProfile, String outputDirectoryPath) 
		  throws IOException {
	  String absoluteFileName = new File(String.format("%s%sstatistical_report.json",
	  			outputDirectoryPath, File.separator)).getAbsolutePath();
    FileWriter writer = new FileWriter(absoluteFileName);
    new GsonBuilder()
        .serializeSpecialFloatingPointValues()
        .setPrettyPrinting()
        .create()
        .toJson(datasetProfile, writer);
  }
}
