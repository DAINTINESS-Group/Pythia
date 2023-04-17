package gr.uoi.cs.pythia.report;

import gr.uoi.cs.pythia.model.DatasetProfile;

import java.io.IOException;

public interface IReportGenerator {

  void produceReport(DatasetProfile datasetProfile, String outputDirectoryPath) throws IOException;
}
