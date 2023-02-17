package gr.uoi.cs.pythia.report;

import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.report.IReportGenerator;
import gr.uoi.cs.pythia.report.md.components.MdCorrelations;
import gr.uoi.cs.pythia.report.md.components.MdDecisionTrees;
import gr.uoi.cs.pythia.report.md.components.MdDescriptiveStatistics;
import gr.uoi.cs.pythia.report.md.components.MdHeader;

import java.io.FileWriter;
import java.io.IOException;

public class MdReportGenerator implements IReportGenerator {

    @Override
    public void produceReport(DatasetProfile datasetProfile, String path) throws IOException {
        try (FileWriter fileWriter = new FileWriter(path)) {
            fileWriter.write(getReportString(datasetProfile));
        }
    }

    private String getReportString(DatasetProfile datasetProfile) {
        StringBuilder bobOMastoras = new StringBuilder();
        bobOMastoras.append(new MdHeader(datasetProfile.getAlias()));
        bobOMastoras.append(new MdDescriptiveStatistics(datasetProfile.getColumns()));
        bobOMastoras.append(new MdCorrelations(datasetProfile.getColumns()));
        bobOMastoras.append(new MdDecisionTrees(datasetProfile));
        return bobOMastoras.toString();
    }
}
