package gr.uoi.cs.pythia.report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.report.md.components.MdCorrelations;
import gr.uoi.cs.pythia.report.md.components.MdDecisionTrees;
import gr.uoi.cs.pythia.report.md.components.MdDescriptiveStatistics;
import gr.uoi.cs.pythia.report.md.components.MdHeader;
import gr.uoi.cs.pythia.report.md.components.MdHistograms;

public class MdReportGenerator implements IReportGenerator {

    @Override
    public void produceReport(DatasetProfile datasetProfile, String outputDirectoryPath) 
    		throws IOException {
    	produceStatisticalProfileReport(datasetProfile, outputDirectoryPath);
    	producePatternsProfileReports(datasetProfile, outputDirectoryPath);
    }

	private void produceStatisticalProfileReport(DatasetProfile datasetProfile,
			String outputDirectoryPath) throws IOException {
		writeToFile(outputDirectoryPath, "statistical_report.md", getReportString(datasetProfile));
	}

	private String getReportString(DatasetProfile datasetProfile) {
        StringBuilder bobOMastoras = new StringBuilder();
        bobOMastoras.append(new MdHeader(datasetProfile.getAlias()));
        bobOMastoras.append(new MdDescriptiveStatistics(datasetProfile.getColumns()));
        bobOMastoras.append(new MdCorrelations(datasetProfile.getColumns()));
        bobOMastoras.append(new MdDecisionTrees(datasetProfile));
        bobOMastoras.append(new MdHistograms(datasetProfile.getColumns()));
        return bobOMastoras.toString();
    }
	
	private void producePatternsProfileReports(DatasetProfile datasetProfile,
			String outputDirectoryPath) throws IOException {
    	if (!datasetProfile.getPatternsProfile().getHighDominanceResults().isEmpty()) {
        	writeToFile(outputDirectoryPath, "high_dominance_report.md",
        			datasetProfile.getPatternsProfile().highDominanceResultsToString());
    	}

    	if (!datasetProfile.getPatternsProfile().getLowDominanceResults().isEmpty()) {
        	writeToFile(outputDirectoryPath, "low_dominance_report.md",
        			datasetProfile.getPatternsProfile().lowDominanceResultsToString());
    	}

    	if (!datasetProfile.getPatternsProfile().getOutlierResults().isEmpty()) {
        	writeToFile(outputDirectoryPath, "outliers_report.md",
        			datasetProfile.getPatternsProfile().outlierResultsToString());
    	}

	}
    
	private void writeToFile(String outputDirectoryPath, String fileName, String contents) 
			throws IOException {
		String absoluteFileName = new File(String.format("%s%s%s",
    			outputDirectoryPath, File.separator, fileName)).getAbsolutePath();
		try (FileWriter fileWriter = new FileWriter(absoluteFileName)) {
			fileWriter.write(contents);
		}
	}
    
}
