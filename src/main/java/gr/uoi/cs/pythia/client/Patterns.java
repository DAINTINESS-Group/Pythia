package gr.uoi.cs.pythia.client;

import java.io.File;
import java.io.IOException;

import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import gr.uoi.cs.pythia.config.AnalysisParameters;
import gr.uoi.cs.pythia.engine.IDatasetProfiler;
import gr.uoi.cs.pythia.engine.IDatasetProfilerFactory;
import gr.uoi.cs.pythia.patterns.algos.ColumnSelectionMode;

public class Patterns {

	// This is a dummy main method
	// Its purpose is to assist with the development of highlight pattern identification
	public static void main(String[] args) throws AnalysisException, IOException {
		IDatasetProfiler datasetProfiler = new IDatasetProfilerFactory().createDatasetProfiler();
		
		StructType schema = createCarsDatasetSchema();
        String alias = "cars";
		String path = String.format(
				"src%stest%sresources%sdatasets%scars_100.csv", 
				File.separator, File.separator, File.separator, File.separator);
		
		datasetProfiler.registerDataset(alias, path, schema);
        datasetProfiler.identifyHighlightPatterns(
        		new AnalysisParameters(
        				ColumnSelectionMode.USER_SPECIFIED_ONLY, 
                		new String[] {"price"}, 
                		new String[] {"model", "year"},
                		"results")
        		);
	}
	
	public static StructType createInternetUsageDatasetSchema() {
		return new StructType(
                new StructField[]{
                        new StructField("name", DataTypes.StringType, true, Metadata.empty()),
                        new StructField("start_time", DataTypes.TimestampType, true, Metadata.empty()),
                        new StructField("usage_time", DataTypes.StringType, true, Metadata.empty()),
                        new StructField("IP", DataTypes.StringType, true, Metadata.empty()),
                        new StructField("MAC", DataTypes.StringType, true, Metadata.empty()),
                        new StructField("upload", DataTypes.DoubleType, true, Metadata.empty()),
                        new StructField("download", DataTypes.DoubleType, true, Metadata.empty()),
                        new StructField("total_transfer", DataTypes.DoubleType, true, Metadata.empty()),
                        new StructField("session_break_reason", DataTypes.StringType, true, Metadata.empty()),
                });
	}
	
	public static StructType createGooglePlaystoreAppsDatasetSchema() {
		return new StructType(
                new StructField[]{
                        new StructField("App Name", DataTypes.StringType, true, Metadata.empty()),
                        new StructField("App Id", DataTypes.StringType, true, Metadata.empty()),
                        new StructField("Category", DataTypes.StringType, true, Metadata.empty()),
                        new StructField("Rating", DataTypes.DoubleType, true, Metadata.empty()),
                        new StructField("Rating Count", DataTypes.IntegerType, true, Metadata.empty()),
                        new StructField("Installs", DataTypes.StringType, true, Metadata.empty()),
                        new StructField("Minimum Installs", DataTypes.IntegerType, true, Metadata.empty()),
                        new StructField("Maximum Installs", DataTypes.IntegerType, true, Metadata.empty()),
                        new StructField("Free", DataTypes.BooleanType, true, Metadata.empty()),
                        new StructField("Price", DataTypes.DoubleType, true, Metadata.empty()),
                        new StructField("Currency", DataTypes.StringType, true, Metadata.empty()),
                        new StructField("Size", DataTypes.StringType, true, Metadata.empty()),
                        new StructField("Minimum Android", DataTypes.StringType, true, Metadata.empty()),
                        new StructField("Developer Id", DataTypes.StringType, true, Metadata.empty()),
                        new StructField("Developer Website", DataTypes.StringType, true, Metadata.empty()),
                        new StructField("Developer Email", DataTypes.StringType, true, Metadata.empty()),
                        new StructField("Released", DataTypes.DateType, true, Metadata.empty()),
                        new StructField("Last Updated", DataTypes.DateType, true, Metadata.empty()),
                        new StructField("Content Rating", DataTypes.StringType, true, Metadata.empty()),
                        new StructField("Privacy Policy", DataTypes.StringType, true, Metadata.empty()),
                        new StructField("Ad Supported", DataTypes.BooleanType, true, Metadata.empty()),
                        new StructField("In App Purchases", DataTypes.BooleanType, true, Metadata.empty()),
                        new StructField("Editors Choice", DataTypes.BooleanType, true, Metadata.empty()),
                        new StructField("Scraped Time", DataTypes.DateType, true, Metadata.empty()),
                });
	}
	
	public static StructType createCarsDatasetSchema() {
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
