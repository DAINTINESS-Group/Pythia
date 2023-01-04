package gr.uoi.cs.pythia.decisiontree.visualization;

import gr.uoi.cs.pythia.decisiontree.model.DecisionTree;
import gr.uoi.cs.pythia.decisiontree.model.node.DecisionTreeNode;
import guru.nidi.graphviz.attribute.*;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.*;

import java.io.File;
import java.io.IOException;

import static guru.nidi.graphviz.attribute.Attributes.attr;
import static guru.nidi.graphviz.model.Factory.*;


public class DecisionTreeGraphvizVisualizer implements IDecisionTreeVisualizer {

    private final Color rootNodeColor = Color.RED4;
    private final Color internalNodeColor = Color.LIGHTSKYBLUE;
    private final Color leafNodeColor = Color.GREEN;
    private final Graph graph;

    public DecisionTreeGraphvizVisualizer(DecisionTree dt) {
        this.graph = getGraph(dt.getRootNode());
    }

    private Graph getGraph(DecisionTreeNode dtNode) {
        return graph("decisionTree").directed()
                .nodeAttr().with(Font.name("Arial"))
                .linkAttr().with("class", "link-class")
                .with(
                        getRootNode(dtNode)
                                .link(to(getNodes(dtNode.getLeftNode())).with(attr("label", "Yes")))
                                .link(to(getNodes(dtNode.getRightNode())).with(attr("label", "No")))
                );
    }

    private Node getRootNode(DecisionTreeNode dtNode) {
        Node graphNode = createNode(dtNode);
        if (dtNode.isLeaf())
            return graphNode.with(leafNodeColor);
        return graphNode.with(rootNodeColor);
    }

    private Node getNodes(DecisionTreeNode dtNode) {
        Node graphNode = createNode(dtNode);
        if (dtNode.isLeaf())
            return graphNode.with(leafNodeColor);

        graphNode = graphNode.with(internalNodeColor);
        Node leftNode = getNodes(dtNode.getLeftNode());
        Node rightNode = getNodes(dtNode.getRightNode());

        graphNode = graphNode.link(to(leftNode).with(attr("label", "Yes")));
        graphNode = graphNode.link(to(rightNode).with(attr("label", "No")));
        return graphNode;
    }

    private Node createNode(DecisionTreeNode dtNode) {
        return node(Integer.toString(dtNode.getId()))
                .with(Style.FILLED, Label.of(dtNode.toVisualizationString()));
    }

    @Override
    public void exportDecisionTreeToPNG(String directory, String fileName) throws IOException {
        Graphviz.fromGraph(graph).height(1000)
                .render(Format.PNG)
                .toFile(new File(directory + File.separator + fileName + ".png"));
    }
}
