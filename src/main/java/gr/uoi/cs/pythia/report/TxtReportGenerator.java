package gr.uoi.cs.pythia.report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.PatternsProfile;
import gr.uoi.cs.pythia.model.RegressionProfile;
import gr.uoi.cs.pythia.model.dominance.DominanceResult;
import gr.uoi.cs.pythia.model.outlier.OutlierResult;
import gr.uoi.cs.pythia.model.regression.RegressionType;

public class TxtReportGenerator implements IReportGenerator {

	private static final String horizontalLine =  "\n\n===================================" +
			"==========================================\n\n";
	private static final String statisticalReportFileName = "statistical_report.txt";
	private static final String highDominanceReportFileName = "high_dominance_report.txt";
	private static final String lowDominanceReportFileName = "low_dominance_report.txt";
	private static final String outliersReportFileName = "outliers_report.txt";
	private static final String regressionReportFileName = "regression_report.txt";

	private static final boolean isExtensiveReport = true;
	
	public void produceReport(DatasetProfile datasetProfile, String outputDirectoryPath)
			throws IOException {
		produceStatisticalProfileReport(datasetProfile, outputDirectoryPath);
		producePatternsProfileReports(datasetProfile, outputDirectoryPath);
		produceRegressionProfileReport(datasetProfile, outputDirectoryPath);
	}

	private void produceRegressionProfileReport(DatasetProfile datasetProfile, String outputDirectoryPath) throws IOException{
		String content = "";
		for(int i=0; i<datasetProfile.getRegressionProfiles().size(); i++) {
			RegressionProfile currentProfile = datasetProfile.getRegressionProfiles().get(i);
			int currentRegressionId = i+1;
			content += currentRegressionId + ". " + this.getTitle(currentProfile) + "\n\n";
			content += "Dependent Variable: " + currentProfile.getDependentVariable().getName() + "\n";
			if (currentProfile.getIndependentVariables().size() > 0) {
			    content += "Independent Variables: " + currentProfile.getIndependentVariables().get(0).getName();

			    for (int j = 1; j < currentProfile.getIndependentVariables().size(); j++) {
			        content += ", " + currentProfile.getIndependentVariables().get(j).getName();
			    }
			}
			content += "\n\n\n-> Results\n\n" + "-Information about Independent Variables\n";
			content += this.getTable(currentProfile);
			content += "\n\n\n-General Information\n";
			content += "Intercept: " + currentProfile.getIntercept() + "\n";
			content += "Error (MSE): " + currentProfile.getError() + "\n";	
			content += "Regression Type: " + this.getTitle(currentProfile)  + "\n";	
			content += "Formula: " + this.getFormula(currentProfile);
			content += "\n\n\n\n\n";
		}writeToFile(outputDirectoryPath, regressionReportFileName, content);
		return;
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
				"\nIdentified Dominance Features:\n" +
				dominanceResult.dominanceToString(isExtensiveReport) +
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
	
	private String getTitle(RegressionProfile profile) {
		if(profile.getType() == RegressionType.AUTOMATED)	return "Automated Regression";
		else if(profile.getType() == RegressionType.LINEAR)	return "Linear Regression";
		else if(profile.getType() == RegressionType.MULTIPLE_LINEAR)	return "Multiple Linear Regression";
		else if(profile.getType() == RegressionType.POLYNOMIAL)	return "Polynomial Regression";
		else	return null;
	}
	
	private String getFormula(RegressionProfile profile) {
		String independentPart = "";
		if(profile.getType() == RegressionType.LINEAR ||
				profile.getType() == RegressionType.MULTIPLE_LINEAR || profile.getType() == RegressionType.AUTOMATED) {
			for(int i=0; i<profile.getIndependentVariables().size();i++) {
				independentPart += " + " + profile.getSlopes().get(i) + "*" + profile.getIndependentVariables().get(i).getName();
			}
		}
		else if(profile.getType() == RegressionType.POLYNOMIAL) {
			for(int i=0; i<profile.getSlopes().size();i++) {
				independentPart += " + " + profile.getSlopes().get(i) +
						"*" + profile.getIndependentVariables().get(0).getName() + "^(" + (i+1) + ")";
			}
		}
		return profile.getDependentVariable().getName() + " = " + profile.getIntercept() + independentPart;
	}
	
	private String getTable(RegressionProfile profile) {
		String content = "";
		if(profile.getType()!= RegressionType.POLYNOMIAL) {
			content += "Column	|	Slope	|	Correlation	|	p-value (Null hypothesis for p-value of each column X -> Dependent variable and X are not correlated)\n";
			content += "-------------------------------------------------\n";
			for(int i=0; i<profile.getIndependentVariables().size(); i++) {
				content += profile.getIndependentVariables().get(i).getName() + "	|	";
				content += profile.getSlopes().get(i) + "	|	";
				content += profile.getCorrelations().get(i) + "	|	";
				content += profile.getpValues().get(i) + "\n";
			}
		}
		else {
			content += "Column	|	Correlation	|	p-value (Null hypothesis for p-value of each column X -> Dependent variable and X are not correlated)\n";
			content += "-------------------------------------------------\n";
			for(int i=0; i<profile.getIndependentVariables().size(); i++) {
				content += profile.getIndependentVariables().get(i).getName() + "	|	";
				content += profile.getCorrelations().get(i) + "	|	";
				content += profile.getpValues().get(i) + "\n";
			}
		}
		return content;
	}
}
