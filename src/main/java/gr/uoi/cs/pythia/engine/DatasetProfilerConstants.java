package gr.uoi.cs.pythia.engine;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;

public class DatasetProfilerConstants {

  // Descriptive statistics
  public static final String COUNT = "count";
  public static final String MEAN = "mean";
  public static final String STANDARD_DEVIATION = "stddev";
  public static final String MEDIAN = "50%"; // the median is the 50th Percentile
  public static final String MIN = "min";
  public static final String MAX = "max";

  // Supported Data Types collected from https://spark.apache.org/docs/latest/sql-ref-datatypes.html
  public static final Map<String, DataType> DATATYPES =
      Stream.of(
              new Object[][] {
                {String.valueOf(DataTypes.ByteType), DataTypes.ByteType},
                {String.valueOf(DataTypes.ShortType), DataTypes.ShortType},
                {String.valueOf(DataTypes.IntegerType), DataTypes.IntegerType},
                {String.valueOf(DataTypes.FloatType), DataTypes.FloatType},
                {String.valueOf(DataTypes.DoubleType), DataTypes.DoubleType},
                {String.valueOf(DataTypes.StringType), DataTypes.StringType},
                {String.valueOf(DataTypes.BinaryType), DataTypes.BinaryType},
                {String.valueOf(DataTypes.BooleanType), DataTypes.BooleanType},
                {String.valueOf(DataTypes.TimestampType), DataTypes.TimestampType},
                {String.valueOf(DataTypes.DateType), DataTypes.DateType},
              })
          .collect(Collectors.toMap(data -> (String) data[0], data -> (DataType) data[1]));
}
