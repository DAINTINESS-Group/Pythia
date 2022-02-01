package gr.uoi.cs.pythia;

import gr.uoi.cs.pythia.engine.IDatasetProfiler;
import gr.uoi.cs.pythia.engine.IDatasetProfilerFactory;
import gr.uoi.cs.pythia.labeling.LabelingSystemConstants;
import gr.uoi.cs.pythia.labeling.Rule;
import gr.uoi.cs.pythia.labeling.RuleSet;
import gr.uoi.cs.pythia.report.ReportGeneratorConstants;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
  public static void main(String[] args) {
    IDatasetProfiler datasetProfiler = IDatasetProfilerFactory.createDatasetProfiler();

    StructType schema =
        new StructType(
            new StructField[] {
              new StructField("id", DataTypes.StringType, false, Metadata.empty()),
              new StructField("user_name", DataTypes.StringType, false, Metadata.empty()),
              new StructField("user_location", DataTypes.StringType, false, Metadata.empty()),
              new StructField("user_description", DataTypes.StringType, false, Metadata.empty()),
              new StructField("user_created", DataTypes.TimestampType, false, Metadata.empty()),
              new StructField("user_followers", DataTypes.IntegerType, false, Metadata.empty()),
              new StructField("user_friends", DataTypes.IntegerType, false, Metadata.empty()),
              new StructField("user_favourites", DataTypes.IntegerType, false, Metadata.empty()),
              new StructField("user_verified", DataTypes.BooleanType, false, Metadata.empty()),
              new StructField("date", DataTypes.TimestampType, false, Metadata.empty()),
              new StructField("text", DataTypes.StringType, false, Metadata.empty()),
              new StructField("hashtags", DataTypes.StringType, false, Metadata.empty()),
              new StructField("source", DataTypes.StringType, false, Metadata.empty()),
              new StructField("retweets", DataTypes.IntegerType, false, Metadata.empty()),
              new StructField("favorites", DataTypes.IntegerType, false, Metadata.empty()),
              new StructField("is_retweet", DataTypes.BooleanType, false, Metadata.empty()),
            });
    try {
      datasetProfiler.registerDataset(
          "peoples", String.format("data%stweets.csv", File.separator), schema);
    } catch (Exception e) {
      System.out.println("Input file not found.");
      return;
    }

    List<Rule> rules = new ArrayList<>();
    rules.add(new Rule("user_followers", LabelingSystemConstants.LEQ, 500, "low"));
    rules.add(new Rule("user_followers", LabelingSystemConstants.LEQ, 10000, "rel_low"));
    rules.add(new Rule("user_followers", LabelingSystemConstants.LEQ, 100000, "medium"));
    rules.add(new Rule("user_followers", LabelingSystemConstants.LEQ, 500000, "high"));
    rules.add(new Rule("user_followers", LabelingSystemConstants.GEQ, 500000, "super_high"));

    RuleSet ruleSet = new RuleSet("user_followers_labeled", rules);
    datasetProfiler.computeLabeledColumn(ruleSet);
    datasetProfiler.computeProfileOfDataset();
    datasetProfiler.generateReport(ReportGeneratorConstants.TXT_REPORT, "test.txt");
  }
}
