package gr.uoi.cs.pythia;

import gr.uoi.cs.pythia.engine.IDatasetProfiler;
import gr.uoi.cs.pythia.engine.IDatasetProfilerFactory;
import gr.uoi.cs.pythia.engine.labeling.LabelingSystemConstants;
import gr.uoi.cs.pythia.engine.labeling.Rule;
import gr.uoi.cs.pythia.engine.labeling.RuleSet;
import gr.uoi.cs.pythia.report.ReportGeneratorConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Main {
  public static void main(String[] args) {
    IDatasetProfiler datasetProfiler = IDatasetProfilerFactory.createDatasetProfiler();

    LinkedHashMap<String, String> schema = new LinkedHashMap<>();
    schema.put("id", "StringType");
    schema.put("user_name", "StringType");
    schema.put("user_location", "StringType");
    schema.put("user_description", "StringType");
    schema.put("user_created", "TimestampType");
    schema.put("user_followers", "IntegerType");
    schema.put("user_friends", "IntegerType");
    schema.put("user_favourites", "IntegerType");
    schema.put("user_verified", "BooleanType");
    schema.put("date", "TimestampType");
    schema.put("text", "StringType");
    schema.put("hashtags", "StringType");
    schema.put("source", "StringType");
    schema.put("retweets", "IntegerType");
    schema.put("favorites", "IntegerType");
    schema.put("is_retweet", "BooleanType");
    datasetProfiler.registerDataset(
        "peoples", String.format("data%stweets.csv", File.separator), schema);

    List<Rule> rules = new ArrayList<>();
    rules.add(new Rule("user_followers", LabelingSystemConstants.LEQ, 500, "low"));
    rules.add(new Rule("user_followers", LabelingSystemConstants.LEQ, 10000, "rel_low"));
    rules.add(new Rule("user_followers", LabelingSystemConstants.LEQ, 100000, "medium"));
    rules.add(new Rule("user_followers", LabelingSystemConstants.LEQ, 500000, "high"));
    rules.add(new Rule("user_followers", LabelingSystemConstants.GEQ, 500000, "super_high"));

    RuleSet ruleSet = new RuleSet("user_followers_labeled", rules);

    //        LinkedHashMap<String, String> schema = new LinkedHashMap<>();
    //        schema.put("name", "StringType");
    //        schema.put("age", "IntegerType");
    //        schema.put("funny", "IntegerType");
    //        schema.put("iQ", "IntegerType");
    //
    //        DatasetProfile datasetProfile = datasetProfiler
    //                .registerDataset("peoples", "people.json", schema);
    //
    //        List<Rule> rules = new ArrayList<>();
    //        rules.add(new Rule("iQ", LabelingSystemConstants.LEQ, 50, "low"));
    //        rules.add(new Rule("iQ", LabelingSystemConstants.LEQ, 70, "average"));
    //        rules.add(new Rule("iQ", LabelingSystemConstants.LEQ, 90, "smart"));
    //        rules.add(new Rule("iQ", LabelingSystemConstants.GEQ, 90, "smartx2"));
    //        RuleSet ruleSet = new RuleSet("iQ_labeled", rules);

    datasetProfiler.computeLabeledColumn(ruleSet);
    datasetProfiler.computeProfileOfDataset();
    datasetProfiler.generateReport(ReportGeneratorConstants.JSON_REPORT, "test.json");
    datasetProfiler.generateReport(ReportGeneratorConstants.TXT_REPORT, "test.txt");
  }
}
