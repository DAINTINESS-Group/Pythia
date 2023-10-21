package gr.uoi.cs.pythia.report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.PatternsProfile;
import gr.uoi.cs.pythia.model.outlier.OutlierResult;
import gr.uoi.cs.pythia.patterns.dominance.DominanceResult;

public class TxtReportGenerator implements IReportGenerator {

	private static final String horizontalLine =  "\n\n===================================" +
			"==========================================\n\n";
	private static final String statisticalReportFileName = "statistical_report.txt";
	private static final String highDominanceReportFileName = "high_dominance_report.txt";
	private static final String lowDominanceReportFileName = "low_dominance_report.txt";
	private static final String outliersReportFileName = "outliers_report.txt";

	private static final boolean isExtensiveReport = true;
	
	public void produceReport(DatasetProfile datasetProfile, String outputDirectoryPath)
			throws IOException {
		produceStatisticalProfileReport(datasetProfile, outputDirectoryPath);
		producePatternsProfileReports(datasetProfile, outputDirectoryPath);
	}

	private void produceStatisticalProfileReport(DatasetProfile datasetProfile,
			String outputDirectoryPath) throws IOException {
		writeToFile(outputDirectoryPath, statisticalReportFileName, datasetProfile.toString());
	}
	
	private void producePatternsProfileReports(DatasetProfile datasetProfile,
			String outputDirectoryPath) throws IOException {
		PatternsProfile patternsProfile = datasetProfile.getPatternsProfile();
		
		List<DominanceResult> highDominanceResults = patternsProfile.getHighDominanceResults();
		produceHighDominanceReport(highDominanceResults, outputDirectoryPath);
		
		List<DominanceResult> lowDominanceResults = patternsProfile.getLowDominanceResults();
		produceLowDominanceReport(lowDominanceResults, outputDirectoryPath);
		
		produceOutliersReport(datasetProfile, outputDirectoryPath);
	}
	
	private void produceHighDominanceReport(List<DominanceResult> highDominanceResults,
			String outputDirectoryPath) throws IOException {
		StringBuilder str = new StringBuilder("High Dominance Pattern Extensive Report\n");
		for (DominanceResult result : highDominanceResults) {
			str.append(buildDominanceResultString(result));
		}
		writeToFile(outputDirectoryPath, highDominanceReportFileName, String.valueOf(str));
	}

	private void produceLowDominanceReport(List<DominanceResult> lowDominanceResults,
		 	String outputDirectoryPath) throws IOException {
		StringBuilder str = new StringBuilder("Low Dominance Pattern Extensive Report\n");
		for (DominanceResult result : lowDominanceResults) {
			str.append(buildDominanceResultString(result));
		}
		writeToFile(outputDirectoryPath, lowDominanceReportFileName, String.valueOf(str));
	}
	
	private String buildDominanceResultString(DominanceResult dominanceResult) {
		String queryResultToString = "";
		
		if (dominanceResult.hasTwoCoordinates()) {
			queryResultToString = "\nQuery Results:\n" + dominanceResult.queryResultToString();
		}
		
		return horizontalLine + 
				"\n" + dominanceResult.titleToString() +
				"\nMetadata:\n" +
				dominanceResult.metadataToString() +
				"\nDetailed Results:\n" +
				dominanceResult.identificationResultsToString(isExtensiveReport) +
				"\nIdentified Highlights:\n" +
				dominanceResult.highlightsToString(isExtensiveReport) +
				queryResultToString;
	}
	
	private void produceOutliersReport(DatasetProfile datasetProfile,
			String outputDirectoryPath) throws IOException {
		PatternsProfile patternsProfile = datasetProfile.getPatternsProfile();
		List<OutlierResult> outlierResults = datasetProfile.getPatternsProfile().getOutlierResults();

		StringBuilder str = new StringBuilder(String.format(
				patternsProfile.getOutlierType() + " Outlier Pattern Results\n\n"
						+ "Total outliers found: %s\n",
				outlierResults.size()));

		for (Column column : datasetProfile.getColumns()) {
			int outliersInColumn = patternsProfile.countOutliersInColumn(column.getName());
			str.append(String.format(horizontalLine + "- Outliers in %s column\n"
					+ "Outliers found: %s\n", column.getName(), outliersInColumn));
			if (outliersInColumn > 0) {
				str.append(String.format("%-24s%-24s%-24s\n",
						"Outlier value", patternsProfile.getOutlierType(),
						"Position in the column"));
			}
			for (OutlierResult result : outlierResults) {
				if (!Objects.equals(result.getColumnName(), column.getName())) continue;
				str.append(result.toString());
			}
		}
		writeToFile(outputDirectoryPath, outliersReportFileName, String.valueOf(str));
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
