package gr.uoi.cs.pythia.regression;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import gr.uoi.cs.pythia.model.DatasetProfile;
import gr.uoi.cs.pythia.model.RegressionProfile;
import gr.uoi.cs.pythia.model.regression.RegressionType;

public interface IRegressionPerformer {
	//add comments 
	
	RegressionType getRegressionType();
	
	RegressionProfile performRegression(Dataset<Row> dataset); //discuss return type

}
