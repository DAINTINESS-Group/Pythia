package gr.uoi.cs.pythia.decisiontree;

import gr.uoi.cs.pythia.decisiontree.model.DecisionTree;
import gr.uoi.cs.pythia.decisiontree.visualization.DecisionTreeVisualizerFactory;
import gr.uoi.cs.pythia.decisiontree.visualization.IDecisionTreeVisualizer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class DecisionTreeVisualizationTests {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testGraphvizVisualizer() throws IOException {
        DecisionTree decisionTree = AllDecisionTreeTests.dtResource.getDecisionTree(new ArrayList<>());
        IDecisionTreeVisualizer dtVisualizer = new DecisionTreeVisualizerFactory(decisionTree)
                .getGraphvizVisualizer();

        File testFolder = tempFolder.newFolder("graphvizTests");
        String directory = testFolder.getAbsolutePath();
        String fileName = "graphvizTest";
        dtVisualizer.exportDecisionTreeToPNG(directory, fileName);
        File decisionTreeImage = new File(directory + File.separator + fileName + ".png");
        assertTrue("The graphvizTest.png file does not exist.", decisionTreeImage.isFile());
    }
}
