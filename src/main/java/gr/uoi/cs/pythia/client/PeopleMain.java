package gr.uoi.cs.pythia.client;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import gr.uoi.cs.pythia.engine.DatasetProfilerParameters;
import gr.uoi.cs.pythia.engine.IDatasetProfiler;
import gr.uoi.cs.pythia.engine.IDatasetProfilerFactory;
import gr.uoi.cs.pythia.labeling.LabelingSystemConstants;
import gr.uoi.cs.pythia.labeling.Rule;
import gr.uoi.cs.pythia.labeling.RuleSet;
import gr.uoi.cs.pythia.patterns.dominance.DominanceColumnSelectionMode;
import gr.uoi.cs.pythia.report.ReportGeneratorConstants;
import gr.uoi.cs.pythia.util.HighlightParameters;
import gr.uoi.cs.pythia.util.HighlightParameters.HighlightExtractionMode;

// This class contains a main method specifically set up for the 'people' dataset.
// Used as a sample main class that showcases usage of Pythia in simple steps.
public class PeopleMain {
	public static void main(String[] args) throws AnalysisException, IOException {
		
        // 1. Initialize a DatasetProfiler object (this is the main engine interface of Pythia).
        IDatasetProfiler datasetProfiler = new IDatasetProfilerFactory().createDatasetProfiler();

        // 2. Specify the schema, an alias and the path of the input dataset.
        StructType schema =
                new StructType(
                        new StructField[]{
                                new StructField("name", DataTypes.StringType, true, Metadata.empty()),
                                new StructField("age", DataTypes.IntegerType, true, Metadata.empty()),
                                new StructField("money", DataTypes.IntegerType, true, Metadata.empty()),
                        });
        String alias = "people";
        String path = String.format(
                "src%stest%sresources%sdatasets%speople.json",
                File.separator, File.separator, File.separator, File.separator);
        
        // 3. Register the input dataset specified in step 2 into Pythia.
        datasetProfiler.registerDataset(alias, path, schema);

        // 4. Specify labeling rules for a column and a name for the new labeled column.
        List<Rule> rules =
                new ArrayList<Rule>(
                        Arrays.asList(
                                new Rule("money", LabelingSystemConstants.LEQ, 20, "poor"),
                                new Rule("money", LabelingSystemConstants.LEQ, 1000, "mid"),
                                new Rule("money", LabelingSystemConstants.GT, 1000, "rich")));
        String newColumnName = "money_labeled";
        
        // 5. Create a RuleSet object and compute the new labeled column
        // (steps 4 & 5 can be repeated multiple times).
        RuleSet ruleSet = new RuleSet(newColumnName, rules);
        datasetProfiler.computeLabeledColumn(ruleSet);
        
        // 6. Specify the DominanceColumnSelectionMode and (optionally) a list of 
        // measurement & coordinate columns used in dominance pattern identification.
        DominanceColumnSelectionMode mode = DominanceColumnSelectionMode.USER_SPECIFIED_ONLY;
        String[] measurementColumns = new String[] { "money", "age" };
        String[] coordinateColumns =  new String[] { "name" };
        
        // 7. Declare the specified dominance parameters into Pythia
        // (steps 6 & 7 are optional, however, they are a prerequisite for highlight patterns identification).
    	datasetProfiler.declareDominanceParameters(mode, measurementColumns, coordinateColumns);



        /**
         * Missing
         */
        //Danger!!
        datasetProfiler.declareOutlierParameters(null,0.3); // We have null pointer threashold !!


        // 8. Specify the auxiliary data output directory and the desired parts of the analysis procedure
    	// that should get executed for the computation of the dataset profile.
    	String auxiliaryDataOutputDirectory = "results";
    	boolean shouldRunDescriptiveStats = true;
    	boolean shouldRunHistograms = true;
    	boolean shouldRunAllPairsCorrelations = true;
    	boolean shouldRunDecisionTrees = false;
    	boolean shouldRunDominancePatterns = true;
    	boolean shouldRunOutlierDetection = false;
    	boolean shouldRunRegression = false;
    	boolean shouldRunClustering = false;
	    HighlightParameters highlightParameters = new HighlightParameters(HighlightExtractionMode.NONE, Double.MAX_VALUE);

        // 9. Create a DatasetProfilerParameters object with the parameters specified in step 8
        // and compute the profile of the dataset (this will take a while for big datasets).
        DatasetProfilerParameters parameters =  new DatasetProfilerParameters(
        		auxiliaryDataOutputDirectory,
                shouldRunDescriptiveStats,
                shouldRunHistograms,
                shouldRunAllPairsCorrelations,
                shouldRunDecisionTrees,
                shouldRunDominancePatterns,
                shouldRunOutlierDetection,
                shouldRunRegression,
                shouldRunClustering,
                highlightParameters);
        datasetProfiler.computeProfileOfDataset(parameters);

        // 10. (Optionally) specify an output directory path for the generated reports
        // (unspecified output directory path means that the reports will be generated under the 
        // auxiliary data output directory specified in step 8).
        String outputDirectoryPath = "";
        
        // 11. Generate a report in plain text and markdown format.
        datasetProfiler.generateReport(ReportGeneratorConstants.TXT_REPORT, outputDirectoryPath);
        datasetProfiler.generateReport(ReportGeneratorConstants.MD_REPORT, outputDirectoryPath);
    }
}

