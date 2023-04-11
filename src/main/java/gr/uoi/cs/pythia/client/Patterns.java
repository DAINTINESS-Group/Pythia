package gr.uoi.cs.pythia.client;

import java.io.File;
import java.io.IOException;

import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import gr.uoi.cs.pythia.engine.IDatasetProfiler;
import gr.uoi.cs.pythia.engine.IDatasetProfilerFactory;
import gr.uoi.cs.pythia.patterns.dominance.DominanceAnalysisParameters;
import gr.uoi.cs.pythia.patterns.dominance.DominanceColumnSelectionMode;

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
        datasetProfiler.identifyHighlightPatterns(
        		new DominanceAnalysisParameters(
        				DominanceColumnSelectionMode.USER_SPECIFIED_ONLY, 
                		new String[] {"price"}, 
                		new String[] {"model", "year"},
                		"results")
        		);
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
