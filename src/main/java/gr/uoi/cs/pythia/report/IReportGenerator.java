package gr.uoi.cs.pythia.report;

import gr.uoi.cs.pythia.model.DatasetProfile;

public interface IReportGenerator {

  void produceReport(DatasetProfile datasetProfile, String path);
}
