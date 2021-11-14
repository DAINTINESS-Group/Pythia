package gr.uoi.cs.pythia.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import lombok.Data;

@Data
public class SparkConfig {
  private String master;
  private String appName;
  private String sparkWarehouse;

  public SparkConfig() {
    try (InputStream input =
        SparkConfig.class.getClassLoader().getResourceAsStream("spark.properties")) {
      if (input == null)
        throw new RuntimeException("Unable to find spark.properties file in classpath.");
      Properties properties = new Properties();
      properties.load(input);
      master = properties.getProperty("spark.master.uri");
      appName = properties.getProperty("spark.app.name");
      sparkWarehouse = System.getProperty("java.io.tmpdir");
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
