package gr.uoi.cs.pythia.decisiontree.visualization;

import gr.uoi.cs.pythia.decisiontree.model.DecisionTree;

import java.io.IOException;

public interface IDecisionTreeVisualizer {
    /**
     * Creates a new PNG image of Decision Tree as a top-down graph,
     * with the specified file name and at the specified directory.
     * @param directory the directory where the image will be created
     * @param fileName the name of the image file
     * @throws IOException the IOException
     */
    void exportDecisionTreeToPNG(DecisionTree decisionTree, String directory, String fileName) throws IOException;
}
