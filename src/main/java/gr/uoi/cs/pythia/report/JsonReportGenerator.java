package gr.uoi.cs.pythia.report;

import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.util.JsonUtil;

public class JsonReportGenerator implements IReportGenerator {
  @Override
  public void produceReport(DatasetProfile datasetProfile, String path) {
    JsonUtil.saveToJson(datasetProfile, path);
  }
}
