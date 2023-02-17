package gr.uoi.cs.pythia.decisiontree;

import gr.uoi.cs.pythia.decisiontree.visualization.DecisionTreeVisualizerFactory;
import gr.uoi.cs.pythia.decisiontree.visualization.DecisionTreeVisualizerType;
import gr.uoi.cs.pythia.model.decisiontree.DecisionTree;
import gr.uoi.cs.pythia.testshelpers.TestsUtilities;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class DecisionTreeVisualizationTests {

    @Test
    public void testGraphvizVisualizer() throws IOException {
        DecisionTree decisionTree = AllDecisionTreeTests.dtResource.getDecisionTree(new ArrayList<>());
        String directory = TestsUtilities.getResultsDir("decisiontree");
        String fileName = "graphvizTest";

        new DecisionTreeVisualizerFactory()
                .getVisualizer(DecisionTreeVisualizerType.GRAPH_VIZ)
                .exportDecisionTreeToPNG(decisionTree, directory, fileName);

        File decisionTreeImage = new File(directory + File.separator + fileName + ".png");
        assertTrue("The graphvizTest.png file does not exist.", decisionTreeImage.isFile());
    }
}
