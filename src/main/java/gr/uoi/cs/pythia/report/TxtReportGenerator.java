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
		produceRegressionProfileReport(outputDirectoryPath);
	}

	private void produceRegressionProfileReport(String outputDirectoryPath) throws IOException{
		String content = this.getTitle() + "\n\n";
		content += "Dependent Variable: " + RegressionProfile.getDependentVariableName() + "\n";
		if(RegressionProfile.getIndependentVariablesNames().size()>0)
			content += "Independent Variables: " + RegressionProfile.getIndependentVariablesNames().get(0);
		for(int i=1; i<RegressionProfile.getIndependentVariablesNames().size(); i++)
			content += ", " + RegressionProfile.getIndependentVariablesNames().get(i);
		content += "\n\n\n-> Results\n\n" + "-Information about Independent Variables\n";
		content += this.getTable();
		content += "\n\n\n-General Information\n";
		content += "Intercept: " + RegressionProfile.getIntercept() + "\n";
		content += "Error (MSE): " + RegressionProfile.getError() + "\n";	
		content += "Regression Type: " + this.getTitle()  + "\n";	
		content += "Formula: " + this.getFormula();
		writeToFile(outputDirectoryPath, regressionReportFileName, content);
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
	
	private String getTitle() {
		if(RegressionProfile.getType() == RegressionType.AUTOMATED)	return "Automated Regression";
		else if(RegressionProfile.getType() == RegressionType.LINEAR)	return "Linear Regression";
		else if(RegressionProfile.getType() == RegressionType.MULTIPLE_LINEAR)	return "Multiple Linear Regression";
		else if(RegressionProfile.getType() == RegressionType.POLYNOMIAL)	return "Polynomial Regression";
		else	return null;
	}
	
	private String getFormula() {
		String independentPart = "";
		if(RegressionProfile.getType() == RegressionType.LINEAR ||
				RegressionProfile.getType() == RegressionType.MULTIPLE_LINEAR || RegressionProfile.getType() == RegressionType.AUTOMATED) {
			for(int i=0; i<RegressionProfile.getIndependentVariablesNames().size();i++) {
				independentPart += " + " + RegressionProfile.getSlopes().get(i) + "*" + RegressionProfile.getIndependentVariablesNames().get(i);
			}
		}
		else if(RegressionProfile.getType() == RegressionType.POLYNOMIAL) {
			for(int i=0; i<RegressionProfile.getSlopes().size();i++) {
				independentPart += " + " + RegressionProfile.getSlopes().get(i) +
						"*" + RegressionProfile.getIndependentVariablesNames().get(0) + "^(" + (i+1) + ")";
			}
		}
		return RegressionProfile.getDependentVariableName() + " = " + RegressionProfile.getIntercept() + independentPart;
	}
	
	private String getTable() {
		String content = "";
		if(RegressionProfile.getType()!= RegressionType.POLYNOMIAL) {
			content += "Column	|	Slope	|	Correlation	|	p-value (Null hypothesis for p-value of each column X -> Dependent variable and X are not correlated)\n";
			content += "-------------------------------------------------\n";
			for(int i=0; i<RegressionProfile.getIndependentVariablesNames().size(); i++) {
				content += RegressionProfile.getIndependentVariablesNames().get(i) + "	|	";
				content += RegressionProfile.getSlopes().get(i) + "	|	";
				content += RegressionProfile.getCorrelations().get(i) + "	|	";
				content += RegressionProfile.getpValues().get(i) + "\n";
			}
		}
		else {
			content += "Column	|	Correlation	|	p-value (Null hypothesis for p-value of each column X -> Dependent variable and X are not correlated)\n";
			content += "-------------------------------------------------\n";
			for(int i=0; i<RegressionProfile.getIndependentVariablesNames().size(); i++) {
				content += RegressionProfile.getIndependentVariablesNames().get(i) + "	|	";
				content += RegressionProfile.getCorrelations().get(i) + "	|	";
				content += RegressionProfile.getpValues().get(i) + "\n";
			}
		}
		return content;
	}
}
