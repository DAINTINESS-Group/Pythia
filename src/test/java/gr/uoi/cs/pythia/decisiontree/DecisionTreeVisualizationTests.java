package gr.uoi.cs.pythia.decisiontree;

import gr.uoi.cs.pythia.decisiontree.model.DecisionTree;
import gr.uoi.cs.pythia.decisiontree.visualization.DecisionTreeVisualizerFactory;
import gr.uoi.cs.pythia.decisiontree.visualization.IDecisionTreeVisualizer;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class DecisionTreeVisualizationTests {

    @Test
    public void testGraphvizVisualizer() throws IOException {
        DecisionTree decisionTree = AllDecisionTreeTests.dtResource.getDecisionTree(new ArrayList<>());
        IDecisionTreeVisualizer dtVisualizer = new DecisionTreeVisualizerFactory(decisionTree)
                .getGraphvizVisualizer();

        String directory = getDecisionTreeTestFilePath();
        String fileName = "graphvizTest";
        dtVisualizer.createPng(directory, "graphvizTest");

        File decisionTreeImage = new File(directory + File.separator + fileName + ".png");
        assertTrue("The graphvizTest.png file does not exist.", decisionTreeImage.isFile());
        assertTrue("The graphvizTest.png file could not be deleted." ,decisionTreeImage.delete());
    }

    private String getDecisionTreeTestFilePath() {
        String relativePath = "\\src\\test\\java\\gr\\uoi\\cs\\pythia\\decisiontree"
                .replace("\\", File.separator);
        return System.getProperty("user.dir") + relativePath;
    }
}
