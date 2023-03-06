package gr.uoi.cs.pythia;

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

import gr.uoi.cs.pythia.engine.IDatasetProfiler;
import gr.uoi.cs.pythia.engine.IDatasetProfilerFactory;
import gr.uoi.cs.pythia.labeling.LabelingSystemConstants;
import gr.uoi.cs.pythia.labeling.Rule;
import gr.uoi.cs.pythia.labeling.RuleSet;
import gr.uoi.cs.pythia.report.ReportGeneratorConstants;

public class Main {
  public static void main(String[] args) throws AnalysisException, IOException {
    IDatasetProfiler datasetProfiler = new IDatasetProfilerFactory().createDatasetProfiler();

    StructType schema =
        new StructType(
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
              new StructField("is_retweet", DataTypes.BooleanType, true, Metadata.empty()),
            });

    datasetProfiler.registerDataset(
        "tweets",
        String.format(
            "src%stest%sresources%sdatasets%stweets.csv", 
            File.separator, File.separator, File.separator, File.separator),
        	schema);

    List<Rule> rules =
        new ArrayList<>(
            Arrays.asList(
                new Rule("user_followers", LabelingSystemConstants.LEQ, 500, "low"),
                new Rule("user_followers", LabelingSystemConstants.LEQ, 10000, "rel_low"),
                new Rule("user_followers", LabelingSystemConstants.LEQ, 100000, "medium"),
                new Rule("user_followers", LabelingSystemConstants.LEQ, 500000, "high"),
                new Rule("user_followers", LabelingSystemConstants.GEQ, 500000, "super_high")));
    RuleSet ruleSet = new RuleSet("user_followers_labeled", rules);
    datasetProfiler.computeLabeledColumn(ruleSet);
    datasetProfiler.computeProfileOfDataset("results");
    datasetProfiler.generateReport(ReportGeneratorConstants.TXT_REPORT, "");
    datasetProfiler.generateReport(ReportGeneratorConstants.MD_REPORT, "");
  }
}
