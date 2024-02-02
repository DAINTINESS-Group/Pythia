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
		produceRegressionProfileReport(datasetProfile, outputDirectoryPath);
	}
	
	//TODO produce report based on regression profile.
	private void produceRegressionProfileReport(DatasetProfile datasetProfile, String outputDirectoryPath) throws IOException {
		String content = "";
		for(int i=0; i<datasetProfile.getRegressionProfiles().size(); i++) {
			RegressionProfile currentProfile = datasetProfile.getRegressionProfiles().get(i);
			int currentRegressionId = i+1;
			content += "# " + currentRegressionId + ". " + this.getTitle(currentProfile) + "\n\n";

		    content += "## Dependent Variable\n";
		    content += "- " + currentProfile.getDependentVariable().getName() + "\n\n";
		    content += "## Independent Variables\n";
		    if (currentProfile.getIndependentVariables().size() > 0) {
			    content += "- " + currentProfile.getIndependentVariables().get(0).getName();

			    for (int j = 1; j < currentProfile.getIndependentVariables().size(); j++) {
			        content += ", " + currentProfile.getIndependentVariables().get(j).getName();
			    }
			}
		    content += "\n\n";

		    content += "## Results\n\n";
		    content += "### Information about Independent Variables\n";
		    content += this.getTable(currentProfile);
		    
		    content += "\n\n### General Information\n";

		    content += "- **Intercept:** " + currentProfile.getIntercept() + "\n";
		    content += "- **Error (MSE):** " + currentProfile.getError() + "\n";
		    content += "- **Regression Type:** " + this.getTitle(currentProfile) + "\n";
		    content += "- **Formula:** " + this.getFormula(currentProfile) + "\n";

		    content += "\n<br><br><br>\n\n";
		}writeToFile(outputDirectoryPath, regressionReportFileName, content);
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
	
	private String getTitle(RegressionProfile profile) {
		if(profile.getType() == RegressionType.AUTOMATED)	return "Automated Regression";
		else if(profile.getType() == RegressionType.LINEAR)	return "Linear Regression";
		else if(profile.getType() == RegressionType.MULTIPLE_LINEAR)	return "Multiple Linear Regression";
		else if(profile.getType() == RegressionType.POLYNOMIAL)	return "Polynomial Regression";
		else	return null;
	}
	
	private String getFormula(RegressionProfile profile) {
	    String independentPart = "";

	    if (profile.getType() == RegressionType.LINEAR ||
	    		profile.getType() == RegressionType.MULTIPLE_LINEAR ||
	    				profile.getType() == RegressionType.AUTOMATED) {
	        for (int i = 0; i < profile.getIndependentVariables().size(); i++) {
	            independentPart += " + " + profile.getSlopes().get(i) +
	                    "\\*" + profile.getIndependentVariables().get(i).getName();
	        }
	    } else if (profile.getType() == RegressionType.POLYNOMIAL) {
	        for (int i = 0; i < profile.getSlopes().size(); i++) {
	            String variable = profile.getIndependentVariables().get(0).getName();
	            String power = String.valueOf(i + 1);
	            independentPart += " + " + profile.getSlopes().get(i) +
	                    "\\*" + variable + "<sup>" + power + "</sup>";
	        }
	    }

	    return profile.getDependentVariable().getName() + " = " +
	    profile.getIntercept() + independentPart;
	}
	
	private String getTable(RegressionProfile profile) {
		String content = "";
		if(profile.getType()!= RegressionType.POLYNOMIAL) {
			content += "| Column | Slope | Correlation | p-value |\n";
		    content += "|--------|-------|-------------|---------|\n";
		    
		    // Populate the table with data
		    for (int i = 0; i < profile.getIndependentVariables().size(); i++) {
		        content += "| " + profile.getIndependentVariables().get(i).getName() + " | ";
		        content += profile.getSlopes().get(i) + " | ";
		        content += profile.getCorrelations().get(i) + " | ";
		        content += profile.getpValues().get(i) + " |\n";
		    }
		}
		else {
			content += "| Column | Correlation | p-value |\n";
		    content += "|--------|-------------|---------|\n";
		    
		    // Populate the table with data
		    for (int i = 0; i < profile.getIndependentVariables().size(); i++) {
		        content += "| " + profile.getIndependentVariables().get(i).getName() + " | ";
		        content += profile.getCorrelations().get(i) + " | ";
		        content += profile.getpValues().get(i) + " |\n";
		    }
		}
		return content;
	}

	
}
