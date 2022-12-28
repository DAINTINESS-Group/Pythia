package gr.uoi.cs.pythia.decisiontree.visualization;

import java.io.IOException;

public interface IDecisionTreeVisualizer {
    /**
     * Creates a new PNG image with the specified name,
     * at the specified directory.
     * @param directory the directory where the image will be created
     * @param fileName the name of the image file
     * @throws IOException
     */
    void createPng(String directory, String fileName) throws IOException;
}
