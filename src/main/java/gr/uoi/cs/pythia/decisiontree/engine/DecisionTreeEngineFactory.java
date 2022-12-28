package gr.uoi.cs.pythia.decisiontree.engine;

import gr.uoi.cs.pythia.decisiontree.input.DecisionTreeParams;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class DecisionTreeEngineFactory {
    private final DecisionTreeParams decisionTreeParams;
    private final Dataset<Row> dataset;

    public DecisionTreeEngineFactory(DecisionTreeParams decisionTreeParams, Dataset<Row> dataset) {
        this.decisionTreeParams = decisionTreeParams;
        this.dataset = dataset;
    }

    public IDecisionTreeEngine getDefaultEngine() {
        return new DecisionTreeEngine(decisionTreeParams, dataset);
    }
}
