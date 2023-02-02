package gr.uoi.cs.pythia.decisiontree.model.node;

public class DecisionTreeNode {
    private final DecisionTreeNodeParams nodeParams;
    private final int id;
    private final boolean isLeaf;
    private final Predict predict;
    private final Split split;
    private final InformationGainStats stats;

    public DecisionTreeNode(DecisionTreeNodeParams nodeParams) {
        this.nodeParams = nodeParams;
        // data
        this.id = nodeParams.getNode().id();
        this.isLeaf = nodeParams.getNode().isLeaf();
        this.predict = new Predict(nodeParams);
        this.split = new Split(nodeParams);
        this.stats = new InformationGainStats(nodeParams);
    }

    public int getId() {
        return id;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public Predict getPredict() {
        return predict;
    }

    public Split getSplit() {
        return split;
    }

    public InformationGainStats getStats() {
        return stats;
    }

    public DecisionTreeNode getLeftNode() {
        return new DecisionTreeNode(
                new DecisionTreeNodeParams(nodeParams,
                        nodeParams.getNode().leftNode().get()));
    }

    public DecisionTreeNode getRightNode() {
        return new DecisionTreeNode(
                new DecisionTreeNodeParams(nodeParams,
                        nodeParams.getNode().rightNode().get()));
    }

    public String getBasicInfo() {
        if (isLeaf)
            return predict.getPrediction();
        StringBuilder sb = new StringBuilder();
        sb.append(split.getFeature());
        if (split.getFeatureType() == FeatureType.CONTINUOUS) {
            sb.append(" <= ");
            sb.append(split.getThreshold());
        } else {
            sb.append(" in ");
            sb.append("(");
            sb.append(String.join(", ", split.getCategories()));
            sb.append(")");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Node = [" +
                "isLeaf = " + isLeaf +
                ", " + predict +
                ", "  + split +
                ", "  + stats + "]";
    }
}
