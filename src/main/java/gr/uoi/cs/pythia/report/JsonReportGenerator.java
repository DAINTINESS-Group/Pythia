package gr.uoi.cs.pythia.report;

import com.google.gson.GsonBuilder;
import gr.uoi.cs.pythia.model.DatasetProfile;
import java.io.FileWriter;
import java.io.IOException;

public class JsonReportGenerator implements IReportGenerator {

  @Override
  public void produceReport(DatasetProfile datasetProfile, String path) throws IOException {
    FileWriter writer = new FileWriter(path);
    new GsonBuilder()
        .serializeSpecialFloatingPointValues()
        .setPrettyPrinting()
        .create()
        .toJson(datasetProfile, writer);
  }
}
