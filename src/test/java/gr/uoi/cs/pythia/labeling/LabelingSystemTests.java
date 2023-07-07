package gr.uoi.cs.pythia.labeling;

import static org.apache.spark.sql.functions.expr;
import static org.junit.Assert.*;

import gr.uoi.cs.pythia.config.SparkConfig;
import gr.uoi.cs.pythia.labeling.LabelingSystemConstants;
import gr.uoi.cs.pythia.labeling.Rule;
import gr.uoi.cs.pythia.labeling.RuleSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.junit.Before;
import org.junit.Test;

public class LabelingSystemTests {

  private List<Rule> rules;
  private List<Row> data;

  @Before
  public void init() {
    rules = new ArrayList<>();
    rules.add(new Rule("age", LabelingSystemConstants.LEQ, 17, "kid"));
    rules.add(new Rule("age", LabelingSystemConstants.LEQ, 30, "adult"));
    rules.add(new Rule("age", LabelingSystemConstants.LEQ, 50, "mid"));
    rules.add(new Rule("age", LabelingSystemConstants.LEQ, 70, "old"));
    rules.add(new Rule("age", LabelingSystemConstants.LEQ, 90, "very old"));
    rules.add(new Rule("age", LabelingSystemConstants.GT, 90, "lucky one"));

    data =
        Arrays.asList(
            RowFactory.create("Michael", 80, 10, 100),
            RowFactory.create("Alex", 25, 40, 120),
            RowFactory.create("Panos", 30, 60, 35),
            RowFactory.create("Nikos", 40, 32, 67),
            RowFactory.create("Maria", 35, 90, 78),
            RowFactory.create("Jacob", 15, 56, 130));
  }

  @Test
  public void testRuleSetCreation() {
    RuleSet ruleSet = new RuleSet("age_labeled", rules);
    String expected =
        "CASE WHEN age <= '17' THEN 'kid' "
            + "WHEN age <= '30' THEN 'adult' "
            + "WHEN age <= '50' THEN 'mid' "
            + "WHEN age <= '70' THEN 'old' "
            + "WHEN age <= '90' THEN 'very old' "
            + "WHEN age > '90' THEN 'lucky one' "
            + "END";

    assertEquals(expected, ruleSet.generateSparkSqlExpression());
  }

  @Test
  public void testComputeLabeledColumn() {

    SparkConfig sparkConfig = new SparkConfig();
    SparkSession sparkSession =
        SparkSession.builder()
            .appName(sparkConfig.getAppName())
            .master(sparkConfig.getMaster())
            .config("spark.sql.warehouse.dir", sparkConfig.getSparkWarehouse())
            .getOrCreate();

    StructType schema =
        new StructType(
            new StructField[] {
              new StructField("name", DataTypes.StringType, false, Metadata.empty()),
              new StructField("age", DataTypes.IntegerType, false, Metadata.empty()),
              new StructField("funny", DataTypes.IntegerType, false, Metadata.empty()),
              new StructField("IQ", DataTypes.IntegerType, false, Metadata.empty()),
            });

    Dataset<Row> dataset = sparkSession.createDataFrame(data, schema);

    RuleSet ruleSet = new RuleSet("age_labeled", rules);
    String expression = ruleSet.generateSparkSqlExpression();
    dataset = dataset.withColumn(ruleSet.getNewColumnName(), expr(expression));
    List<Object> actual = dataset.select("age_labeled").toJavaRDD().map(row -> row.get(0)).collect();

    List<Object> expected =
        new ArrayList<>(Arrays.asList("very old", "adult", "adult", "mid", "mid", "kid"));
    assertEquals(expected, actual);
    sparkSession.stop();
  }
}
