package gr.uoi.cs.pythia.regression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.ml.regression.LinearRegression;
import org.apache.spark.ml.regression.LinearRegressionModel;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.linalg.DenseVector;


import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.RegressionProfile;
import gr.uoi.cs.pythia.model.regression.RegressionType;

public class MultipleLinearRegressionPerformer extends GeneralRegression {
	
	private String dependentVariable;
	private List<String> independentVariables;
	private double intercept;
	private List<Double> slopes;
	
	
	public MultipleLinearRegressionPerformer(String dependentVariable, List<String> independentVariables, DatasetProfile datasetProfile) {
		super(datasetProfile);
		this.dependentVariable = dependentVariable;
		this.independentVariables = independentVariables;
		slopes = new ArrayList<>();
	}
	
	@Override
	public RegressionType getRegressionType() {
		return RegressionType.MULTIPLE_LINEAR;
	}
	
	@Override
	public RegressionProfile performRegression(Dataset<Row> dataset) {
	    // Get the values of the relevant columns
	    List<Double> dependentVariableValues = getColumnValues(dataset, dependentVariable);
	    List<List<Double>> independentVariablesValues = new ArrayList<>();
	    for (String var : independentVariables)
	        independentVariablesValues.add(getColumnValues(dataset, var));

	    //prepare a dataframe to train to the linear regression model
	    Dataset<Row> data = createDataFrame(dependentVariableValues, independentVariablesValues);
	    String[] featureCols = independentVariables.toArray(new String[0]);
	    VectorAssembler assembler = new VectorAssembler()
	            .setInputCols(featureCols)
	            .setOutputCol("features");
	    Dataset<Row> assembledData = assembler.transform(data);

	    //this is the part of the train of the model
	    LinearRegression lr = new LinearRegression()
	            .setLabelCol(dependentVariable)
	            .setFeaturesCol("features");

	    //get the model from the training
	    LinearRegressionModel model = lr.fit(assembledData);

	    //get intercept
	    intercept = model.intercept();
	    
	    //we get slopes as coefficients from the trained model
	    DenseVector coefficients = (DenseVector) model.coefficients();

	    //convert coefficients to doubles
	    for (int i = 0; i < coefficients.size(); i++) {
	        slopes.add(coefficients.apply(i));
	    }
	    
	    List<Double> correlations = getCorrelations(dependentVariable, independentVariables);
		List<Double> pValues = calculatePValues(correlations, dependentVariableValues.size());
		Double error = model.summary().meanSquaredError();

	    // Save output to RegressionProfile
		RegressionProfile result = this.setupRegressionProfile(independentVariables, independentVariablesValues,
				dependentVariable, dependentVariableValues, RegressionType.MULTIPLE_LINEAR,
				slopes, intercept, correlations, pValues, error);

	    datasetProfile.addRegressionProfile(result);
	    return result;
	}
	
	//helper method to create a DataFrame from the given data
	private Dataset<Row> createDataFrame(List<Double> dependentVariableValues, List<List<Double>> independentVariablesValues) {
	    List<Row> rows = new ArrayList<>();
	    boolean skipPoint;
	    for (int i = 0; i < dependentVariableValues.size(); i++) {
	    	skipPoint = false;
	        List<Object> values = new ArrayList<>();
	        values.add(dependentVariableValues.get(i));
	        for (List<Double> var : independentVariablesValues) {
	        	if(Double.isNaN(var.get(i)))	skipPoint=true;
	        }
	        if(Double.isNaN(dependentVariableValues.get(i)) || skipPoint)	continue;
	        for (List<Double> var : independentVariablesValues) {
	        	values.add(var.get(i));
	        }
	        
	        rows.add(RowFactory.create(values.toArray()));
	    }
	    
	    List<StructField> fields = new ArrayList<>();
	    fields.add(DataTypes.createStructField(dependentVariable, DataTypes.DoubleType, false));
	    for (String var : independentVariables) {
	        fields.add(DataTypes.createStructField(var, DataTypes.DoubleType, false));
	    }

	    StructType schema = DataTypes.createStructType(fields);
	    return SparkSession.builder().getOrCreate().createDataFrame(rows, schema);
	}

}
