package gr.uoi.cs.pythia;

import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.sparkproject.guava.io.Files;
import org.sparkproject.guava.io.Resources;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TestsUtilities {

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

    public static String getDatasetPath(String datasetName) {
        return new File(Resources.getResource("datasets/" + datasetName).getFile())
                .getAbsolutePath();
    }

    public static String getExpectedFileAsString(String fileName) throws IOException {
        URL url = Resources.getResource("expected/" + fileName);
        return Resources.toString(url, StandardCharsets.UTF_8)
                .replace("\r", "");
    }

    public static String getTextFromFile(File file) throws IOException {
        return Files.toString(file, StandardCharsets.UTF_8)
                .replace("\r", "");
    }
}
