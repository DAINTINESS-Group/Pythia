package gr.uoi.cs.pythia.testshelpers;

import org.apache.commons.io.FileUtils;
import org.sparkproject.guava.io.Files;
import org.sparkproject.guava.io.Resources;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

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

    public static void setupResultsDir(String directory) throws IOException {
        String resultsFilePath = "src/test/resources/results";
        File results = new File(resultsFilePath);
        if (!results.exists()) {
            java.nio.file.Files.createDirectories(Paths.get(resultsFilePath));
        }
        String directoryFilePath = results.getAbsolutePath() + File.separator + directory;
        File directoryFile = new File(directoryFilePath);
        if (!directoryFile.exists()) {
            java.nio.file.Files.createDirectories(Paths.get(directoryFilePath));
        }
        FileUtils.cleanDirectory(new File(getResultsDir(directory)));
    }

    public static String getResultsDir(String directory) {
        return ("src/test/resources/results/" + directory).replace("/", File.separator);
    }
}
