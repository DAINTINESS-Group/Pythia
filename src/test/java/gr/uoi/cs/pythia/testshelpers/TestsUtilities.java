package gr.uoi.cs.pythia.testshelpers;

import org.sparkproject.guava.io.Files;
import org.sparkproject.guava.io.Resources;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TestsUtilities {

    public static String getDatasetPath(String datasetName) {
        return new File(Resources.getResource("datasets/" + datasetName).getFile())
                .getAbsolutePath();
    }

    public static String getExpectedDatasetReport(String filepath) throws IOException {
        URL url = Resources.getResource("expectedDatasetReports/" + filepath);
        return Resources.toString(url, StandardCharsets.UTF_8)
                .replace("\r", "");
    }

    public static String getTextFromFile(File file) throws IOException {
        return Files.toString(file, StandardCharsets.UTF_8)
                .replace("\r", "");
    }
}
