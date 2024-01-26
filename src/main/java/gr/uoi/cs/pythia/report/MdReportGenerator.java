package gr.uoi.cs.pythia.report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.apache.spark.sql.types.DataTypes;

import gr.uoi.cs.pythia.model.Column;
import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.PatternsProfile;
import gr.uoi.cs.pythia.model.RegressionProfile;
import gr.uoi.cs.pythia.model.dominance.DominanceResult;
import gr.uoi.cs.pythia.model.outlier.OutlierResult;
import gr.uoi.cs.pythia.model.regression.RegressionType;
import gr.uoi.cs.pythia.report.md.components.MdCorrelations;
import gr.uoi.cs.pythia.report.md.components.MdDecisionTrees;
import gr.uoi.cs.pythia.report.md.components.MdDescriptiveStatistics;
import gr.uoi.cs.pythia.report.md.components.MdHeader;
import gr.uoi.cs.pythia.report.md.components.MdHistograms;

public class MdReportGenerator implements IReportGenerator {

	private static final String horizontalLine =  "\n\n-----------------------------------------------" +
			"-----------------------------------------------------\n\n";
	private static final String preTagOpen = "<pre>\n";
	private static final String preTagClose = "</pre>\n";

	private static final String statisticalReportFileName = "statistical_report.md";
	private static final String highDominanceReportFileName = "high_dominance_report.md";
	private static final String lowDominanceReportFileName = "low_dominance_report.md";
	private static final String outliersReportFileName = "outliers_report.md";
	private static final String regressionReportFileName = "regression_report.md";

	private static final boolean isExtensiveReport = false;

	@Override
	public void produceReport(DatasetProfile datasetProfile, String outputDirectoryPath)
			throws IOException {
		produceStatisticalProfileReport(datasetProfile, outputDirectoryPath);
		producePatternsProfileReports(datasetProfile, outputDirectoryPath);
		produceRegressionProfileReport(outputDirectoryPath);
	}
	
	//TODO produce report based on regression profile.
	private void produceRegressionProfileReport(String outputDirectoryPath) throws IOException {
	    String content = "# " + this.getTitle() + "\n\n";

	    content += "## Dependent Variable\n";
	    content += "- " + RegressionProfile.getDependentVariableName() + "\n\n";

	    content += "## Independent Variables\n";
	    content += "- " + String.join(", ", RegressionProfile.getIndependentVariablesNames()) + "\n\n";

	    content += "## Results\n\n";
	    content += "### Information about Independent Variables\n";
	    content += this.getTable();
	    
	    content += "\n\n### General Information\n";

	    content += "- **Intercept:** " + RegressionProfile.getIntercept() + "\n";
	    content += "- **Error (MSE):** " + RegressionProfile.getError() + "\n";
	    content += "- **Regression Type:** " + this.getTitle() + "\n";
	    content += "- **Formula:** " + this.getFormula() + "\n";

	    writeToFile(outputDirectoryPath, regressionReportFileName, content);
	}



	private void produceStatisticalProfileReport(DatasetProfile datasetProfile,
			String outputDirectoryPath) throws IOException {
		writeToFile(outputDirectoryPath, statisticalReportFileName, getReportString(datasetProfile));
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
		PatternsProfile patternsProfile = datasetProfile.getPatternsProfile();

		List<DominanceResult> highDominanceResults = patternsProfile.getHighDominanceResults();
		produceHighDominanceReport(highDominanceResults, outputDirectoryPath);

		List<DominanceResult> lowDominanceResults = patternsProfile.getLowDominanceResults();
		produceLowDominanceReport(lowDominanceResults, outputDirectoryPath);

		produceOutliersReport(datasetProfile, outputDirectoryPath);
	}

	private void produceHighDominanceReport(List<DominanceResult> highDominanceResults,
			String outputDirectoryPath) throws IOException {
		StringBuilder str = new StringBuilder("# High Dominance Pattern Concise Report\n");
		for (DominanceResult result : highDominanceResults) {
			str.append(buildDominanceResultString(result));
		}
		writeToFile(outputDirectoryPath, highDominanceReportFileName, String.valueOf(str));
	}

	private void produceLowDominanceReport(List<DominanceResult> lowDominanceResults,
		 	String outputDirectoryPath) throws IOException {
		StringBuilder str = new StringBuilder("# Low Dominance Pattern Concise Report\n");
		for (DominanceResult result : lowDominanceResults) {
			str.append(buildDominanceResultString(result));
		}
		writeToFile(outputDirectoryPath, lowDominanceReportFileName, String.valueOf(str));
	}

	private String buildDominanceResultString(DominanceResult dominanceResult) {
		if (dominanceResult.hasNoDominance()) return "";
		return horizontalLine + 
				"\n## " + dominanceResult.titleToString() +
				"\n### Metadata:\n" +
				preTagOpen + dominanceResult.metadataToString() + preTagClose +
				"\n### Detailed Results:\n" +
				preTagOpen + dominanceResult.identificationResultsToString(isExtensiveReport) 
				+ preTagClose +
				"\n### Identified Dominance Features:\n" +
				preTagOpen + dominanceResult.dominanceToString(isExtensiveReport) + preTagClose;
	}

	private void produceOutliersReport(DatasetProfile datasetProfile,
																		 String outputDirectoryPath) throws IOException {
		PatternsProfile patternsProfile = datasetProfile.getPatternsProfile();
		List<OutlierResult> outlierResults = datasetProfile.getPatternsProfile().getOutlierResults();

		StringBuilder str = new StringBuilder(String.format(
						"# " + patternsProfile.getOutlierType() + " Outlier Pattern Results\n\n" +
										"Total outliers found: %s\n", outlierResults.size()));

		for (Column column : datasetProfile.getColumns()) {
			if(isNumericColumn(column)) {
				int outliersInColumn = patternsProfile.countOutliersInColumn(column.getName());
				str.append(String.format(horizontalLine +
												"## Outliers in %s column\n" +
												"Outliers found: %s\n",
								column.getName(),
								outliersInColumn));
				if (outliersInColumn > 0) {
					str.append(String.format("%s%-24s%-24s%-24s\n",
									preTagOpen,
									"Outlier value", patternsProfile.getOutlierType(), "Position in the column"));
				}
				for (OutlierResult result : outlierResults) {
					if (!Objects.equals(result.getColumnName(), column.getName())) continue;
					str.append(result.toString());
				}
				if (outliersInColumn > 0) str.append(preTagClose);
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
	
	private boolean isNumericColumn(Column column) {
		return (column.getDatatype() == DataTypes.DoubleType.toString() ||
				column.getDatatype() == DataTypes.IntegerType.toString());
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

	    if (RegressionProfile.getType() == RegressionType.LINEAR ||
	            RegressionProfile.getType() == RegressionType.MULTIPLE_LINEAR ||
	            RegressionProfile.getType() == RegressionType.AUTOMATED) {
	        for (int i = 0; i < RegressionProfile.getIndependentVariablesNames().size(); i++) {
	            independentPart += " + " + RegressionProfile.getSlopes().get(i) +
	                    "\\*" + RegressionProfile.getIndependentVariablesNames().get(i);
	        }
	    } else if (RegressionProfile.getType() == RegressionType.POLYNOMIAL) {
	        for (int i = 0; i < RegressionProfile.getSlopes().size(); i++) {
	            String variable = RegressionProfile.getIndependentVariablesNames().get(0);
	            String power = String.valueOf(i + 1);
	            independentPart += " + " + RegressionProfile.getSlopes().get(i) +
	                    "\\*" + variable + "<sup>" + power + "</sup>";
	        }
	    }

	    return RegressionProfile.getDependentVariableName() + " = " +
	            RegressionProfile.getIntercept() + independentPart;
	}
	
	private String getTable() {
		String content = "";
		if(RegressionProfile.getType()!= RegressionType.POLYNOMIAL) {
			content += "| Column | Slope | Correlation | p-value |\n";
		    content += "|--------|-------|-------------|---------|\n";
		    
		    // Populate the table with data
		    for (int i = 0; i < RegressionProfile.getIndependentVariablesNames().size(); i++) {
		        content += "| " + RegressionProfile.getIndependentVariablesNames().get(i) + " | ";
		        content += RegressionProfile.getSlopes().get(i) + " | ";
		        content += RegressionProfile.getCorrelations().get(i) + " | ";
		        content += RegressionProfile.getpValues().get(i) + " |\n";
		    }
		}
		else {
			content += "| Column | Correlation | p-value |\n";
		    content += "|--------|-------------|---------|\n";
		    
		    // Populate the table with data
		    for (int i = 0; i < RegressionProfile.getIndependentVariablesNames().size(); i++) {
		        content += "| " + RegressionProfile.getIndependentVariablesNames().get(i) + " | ";
		        content += RegressionProfile.getCorrelations().get(i) + " | ";
		        content += RegressionProfile.getpValues().get(i) + " |\n";
		    }
		}
		return content;
	}

	
}
