package gr.uoi.cs.pythia.report;

public class ReportGeneratorFactory {

  public IReportGenerator createReportGenerator(String type) {
    switch (type) {
      case ReportGeneratorConstants.TXT_REPORT:
        return new TxtReportGenerator();
      case ReportGeneratorConstants.JSON_REPORT:
        return new JsonReportGenerator();
      case ReportGeneratorConstants.MD_REPORT:
        return new MdReportGenerator();
    }
    throw new IllegalArgumentException(
        String.format("Report system %s is not a supported report system type.", type));
  }
}
