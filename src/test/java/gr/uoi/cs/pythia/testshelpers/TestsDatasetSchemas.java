package gr.uoi.cs.pythia.testshelpers;

import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

public class TestsDatasetSchemas {

    public static StructType getCarseatsCsvSchema() {
        return new StructType(
                new StructField[] {
                        new StructField("Sales", DataTypes.DoubleType, false, Metadata.empty()),
                        new StructField("CompPrice", DataTypes.IntegerType, false, Metadata.empty()),
                        new StructField("Income", DataTypes.IntegerType, false, Metadata.empty()),
                        new StructField("Advertising", DataTypes.IntegerType, false, Metadata.empty()),
                        new StructField("Population", DataTypes.IntegerType, false, Metadata.empty()),
                        new StructField("Price", DataTypes.IntegerType, false, Metadata.empty()),
                        new StructField("ShelveLoc", DataTypes.StringType, false, Metadata.empty()),
                        new StructField("Age", DataTypes.IntegerType, false, Metadata.empty()),
                        new StructField("Education", DataTypes.IntegerType, false, Metadata.empty()),
                        new StructField("Urban", DataTypes.StringType, false, Metadata.empty()),
                        new StructField("US", DataTypes.StringType, false, Metadata.empty())
                });
    }

    public static StructType getPeopleJsonSchema() {
        return new StructType(
                new StructField[] {
                        new StructField("name", DataTypes.StringType, false, Metadata.empty()),
                        new StructField("age", DataTypes.IntegerType, false, Metadata.empty()),
                        new StructField("money", DataTypes.IntegerType, false, Metadata.empty()),
                });
    }

    public static StructType getTweetsCsvSchema() {
        return new StructType(
                new StructField[] {
                        new StructField("id", DataTypes.StringType, true, Metadata.empty()),
                        new StructField("user_name", DataTypes.StringType, true, Metadata.empty()),
                        new StructField("user_location", DataTypes.StringType, true, Metadata.empty()),
                        new StructField("user_description", DataTypes.StringType, true, Metadata.empty()),
                        new StructField("user_created", DataTypes.TimestampType, true, Metadata.empty()),
                        new StructField("user_followers", DataTypes.IntegerType, true, Metadata.empty()),
                        new StructField("user_friends", DataTypes.IntegerType, true, Metadata.empty()),
                        new StructField("user_favourites", DataTypes.IntegerType, true, Metadata.empty()),
                        new StructField("user_verified", DataTypes.BooleanType, true, Metadata.empty()),
                        new StructField("date", DataTypes.TimestampType, true, Metadata.empty()),
                        new StructField("text", DataTypes.StringType, true, Metadata.empty()),
                        new StructField("hashtags", DataTypes.StringType, true, Metadata.empty()),
                        new StructField("source", DataTypes.StringType, true, Metadata.empty()),
                        new StructField("retweets", DataTypes.IntegerType, true, Metadata.empty()),
                        new StructField("favorites", DataTypes.IntegerType, true, Metadata.empty()),
                        new StructField("is_retweet", DataTypes.BooleanType, true, Metadata.empty())
                });
    }

    public static StructType getBreastCsvSchema() {
        return new StructType(
                new StructField[] {
                        new StructField("Clump_Thickness", DataTypes.IntegerType, true, Metadata.empty()),
                        new StructField("Cell_Size_Uniformity", DataTypes.IntegerType, true, Metadata.empty()),
                        new StructField("Cell_Shape_Uniformity", DataTypes.IntegerType, true, Metadata.empty()),
                        new StructField("Marginal_Adhesion", DataTypes.IntegerType, true, Metadata.empty()),
                        new StructField("Single_Epi_Cell_Size", DataTypes.IntegerType, true, Metadata.empty()),
                        new StructField("Bare_Nuclei", DataTypes.IntegerType, true, Metadata.empty()),
                        new StructField("Bland_Chromatin", DataTypes.IntegerType, true, Metadata.empty()),
                        new StructField("Normal_Nucleoli", DataTypes.IntegerType, true, Metadata.empty()),
                        new StructField("Mitoses", DataTypes.IntegerType, true, Metadata.empty()),
                        new StructField("class", DataTypes.StringType, true, Metadata.empty()),
                });
    }
    
	public static StructType getInternetUsageCsvSchema() {
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
	
	public static StructType getGooglePlaystoreAppsCsvSchema() {
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
