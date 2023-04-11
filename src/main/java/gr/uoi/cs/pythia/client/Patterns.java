package gr.uoi.cs.pythia.client;

import java.io.File;
import java.io.IOException;

import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import gr.uoi.cs.pythia.engine.DatasetProfilerParameters;
import gr.uoi.cs.pythia.engine.IDatasetProfiler;
import gr.uoi.cs.pythia.engine.IDatasetProfilerFactory;
import gr.uoi.cs.pythia.patterns.dominance.DominanceColumnSelectionMode;

// TODO this class can most likely be deleted
// dataset profiler parameters allow us to select which parts of the dataset profiling 
// will be executed and therefore the normal main method could be used for patterns development.
public class Patterns {

	// This is a dummy main method
	// Its purpose is to assist with the development of highlight pattern identification
	public static void main(String[] args) throws AnalysisException, IOException {
		IDatasetProfiler datasetProfiler = new IDatasetProfilerFactory().createDatasetProfiler();
		
		StructType schema = getCarsCsvSchema();
        String alias = "cars";
		String path = String.format(
				"src%stest%sresources%sdatasets%scars_100.csv", 
				File.separator, File.separator, File.separator, File.separator);
		
		datasetProfiler.registerDataset(alias, path, schema);
		datasetProfiler.declareDominanceParameters(
				DominanceColumnSelectionMode.USER_SPECIFIED_ONLY,
				new String[] { "price" }, 
				new String[] { "model", "year" }
				);
		
		boolean shouldRunDescriptiveStats = true;
		boolean shouldRunHistograms = true;
		boolean shouldRunAllPairsCorrelations = false;
		boolean shouldRunDecisionTrees = false;
		boolean shouldRunHighlightPatterns = true;

		datasetProfiler.computeProfileOfDataset(
				new DatasetProfilerParameters(
						"results", 
						shouldRunDescriptiveStats,
						shouldRunHistograms, 
						shouldRunAllPairsCorrelations,
						shouldRunDecisionTrees, 
						shouldRunHighlightPatterns));
	}
	
	public static StructType getCarsCsvSchema() {
		return new StructType(
                new StructField[]{
                        new StructField("manufacturer", DataTypes.StringType, true, Metadata.empty()),
                        new StructField("model", DataTypes.StringType, true, Metadata.empty()),
                        new StructField("year", DataTypes.StringType, true, Metadata.empty()),
                        new StructField("price", DataTypes.DoubleType, true, Metadata.empty()),
                        new StructField("transmission", DataTypes.StringType, true, Metadata.empty()),
                        new StructField("mileage", DataTypes.DoubleType, true, Metadata.empty()),
                        new StructField("fuelType", DataTypes.StringType, true, Metadata.empty()),
                        new StructField("tax", DataTypes.IntegerType, true, Metadata.empty()),
                        new StructField("mpg", DataTypes.DoubleType, true, Metadata.empty()),
                        new StructField("engineSize", DataTypes.DoubleType, true, Metadata.empty()),
                });
	}

}
