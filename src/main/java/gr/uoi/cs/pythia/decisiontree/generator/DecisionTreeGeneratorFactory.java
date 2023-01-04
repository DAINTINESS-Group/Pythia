package gr.uoi.cs.pythia.decisiontree.generator;

import gr.uoi.cs.pythia.decisiontree.input.DecisionTreeParams;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class DecisionTreeGeneratorFactory {
    private final DecisionTreeParams decisionTreeParams;
    private final Dataset<Row> dataset;

    public DecisionTreeGeneratorFactory(DecisionTreeParams decisionTreeParams, Dataset<Row> dataset) {
        this.decisionTreeParams = decisionTreeParams;
        this.dataset = dataset;
    }

    public IDecisionTreeGenerator getDefaultGenerator() {
        return new DecisionTreeGenerator(decisionTreeParams, dataset);
    }
}
