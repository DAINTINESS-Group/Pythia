package gr.uoi.cs.pythia.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gr.uoi.cs.pythia.model.DatasetProfile;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.log4j.Logger;

public class JsonUtil {

  private static final Logger logger = Logger.getLogger(JsonUtil.class);

  public static void loadFromJson(DatasetProfile datasetProfile, String path) {
    try {
      new Gson().fromJson(new FileReader(path), DatasetProfile.class);
      logger.info(
          String.format("Loaded %s dataset profile from %s", datasetProfile.getAlias(), path));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public static void saveToJson(DatasetProfile datasetProfile, String path) {
    try (FileWriter writer = new FileWriter(path)) {
      new GsonBuilder()
          .serializeSpecialFloatingPointValues()
          .setPrettyPrinting()
          .create()
          .toJson(datasetProfile, writer);
      logger.info(String.format("Saved %s dataset profile to %s", datasetProfile.getAlias(), path));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
