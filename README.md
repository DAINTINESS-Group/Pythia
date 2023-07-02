<p align="center"> 
    <img height=170 src="https://cdn.discordapp.com/attachments/326432556037832704/936299117766926406/logo3.png"/> 
</p>

## <div align="center">Pythia</div>

#### <div align="center">Java library that produces an automated statistical profile of an input dataset.</div>

A standard dataset is just a text file, with lines, where each line is a record, the fields of which are separated by a
separator (eg. tabs, comma, pipe, etc). After registering a dataset and declaring the desired data analysis methods that should get executed, the system produces a 100% automatic statistical profile of the dataset and generates reports of the findings.

### <div align="center">Setup</div>

---

#### Intellij IDEA Installation Requirements

- Install [**Intellij IDEA**](https://www.jetbrains.com/idea/download/#section=windows) (Community edition is free)
- Import the project as a Maven project, and it runs out of the box.

#### Eclipse Installation Requirements

- Install [**Eclipse**](https://www.eclipse.org/downloads/)
- Import the project as a Maven project.

#### Maven

The project uses a Maven wrapper so there is no need to install it to your system as long as you have the JAVA_HOME
environmental variable pointing to your [**Java 8**](https://www.oracle.com/java/technologies/downloads/archive/) installation
folder.

### <div align="center">🛠️ Build with Maven</div>

---

Navigate to the root folder of the repo and run,

~~~~
./mvnw clean install
~~~~

and wait for the procedure to finish

After that, there should be a folder called `target` that includes two jar files:

~~~~
Pythia-x.y.z-all-deps.jar and Pythia-x.y.z.jar
~~~~

The difference is that the all deps jar file is an uber jar so you can import Pythia to a project and run it out of the
box. (All dependecies are embedded into the all deps jar)

* Otherwise you will need to provide the Pythia dependencies to your pom.xml file.

To run with the driver Main method, navigate to the root folder of the repo:

~~~~
java -jar target/Pythia-x.y.z-all-deps.jar
~~~~

### <div align="center">🧪 Run tests</div>

---

Navigate to the root folder of the repo and run,

~~~~
./mvnw test
~~~~

### <div align="center">Code Formatter</div>

---

This project complies with Google's Java coding style and is formatted using the official [**Google java
formatter**](https://github.com/google/google-java-format). You can follow the installation guide in the official GitHub
repo to install it to your Editor.

_Note:  Consider installing it and run it so that the project follows a coding style_

In case you want to format all java files from the command line, run in the root folder of the project:

~~~~shell
java -jar google-java-format-x.y.z-all-deps.jar -i $(find . -type f -name "*.java")
~~~~

_Note: The formatter needs a [**Java 11**](https://www.oracle.com/java/technologies/downloads/#java11) installation to
run in the command line_

### <div align="center">Usage</div>

---
Suppose we want to generate a statistical profile of the following file:

|  name   | age | money |
|:-------:|:---:|:-----:|
| Michael | 25  |  20   |
|  John   | 21  |  15   |
|  Andy   | 30  | 1000  |
| Justin  | 65  | 10000 |

Below is a sample Main class that showcases API usage in simple steps for the above dataset:

```java
public class Main {
	public static void main(String[] args) throws AnalysisException, IOException {
		
        // 1. Initialize a DatasetProfiler object (this is the main engine interface of Pythia).
        IDatasetProfiler datasetProfiler = new IDatasetProfilerFactory().createDatasetProfiler();

        // 2. Specify the schema, an alias and the path of the input dataset.
        StructType schema =
                new StructType(
                        new StructField[]{
                                new StructField("name", DataTypes.StringType, true, Metadata.empty()),
                                new StructField("age", DataTypes.IntegerType, true, Metadata.empty()),
                                new StructField("money", DataTypes.IntegerType, true, Metadata.empty()),
                        });
        String alias = "people";
        String path = String.format(
                "src%stest%sresources%sdatasets%speople.json",
                File.separator, File.separator, File.separator, File.separator);
        
        // 3. Register the input dataset specified in step 2 into Pythia.
        datasetProfiler.registerDataset(alias, path, schema);

        // 4. Specify labeling rules for a column and a name for the new labeled column.
        List<Rule> rules =
                new ArrayList<Rule>(
                        Arrays.asList(
                                new Rule("money", LabelingSystemConstants.LEQ, 20, "poor"),
                                new Rule("money", LabelingSystemConstants.LEQ, 1000, "mid"),
                                new Rule("money", LabelingSystemConstants.GT, 1000, "rich")));
        String newColumnName = "money_labeled";
        
        // 5. Create a RuleSet object and compute the new labeled column
        // (steps 4 & 5 can be repeated multiple times).
        RuleSet ruleSet = new RuleSet(newColumnName, rules);
        datasetProfiler.computeLabeledColumn(ruleSet);
        
        // 6. Specify the DominanceColumnSelectionMode and (optionally) a list of 
        // measurement & coordinate columns used in dominance pattern identification.
        DominanceColumnSelectionMode mode = DominanceColumnSelectionMode.USER_SPECIFIED_ONLY;
        String[] measurementColumns = new String[] { "money", "age" };
        String[] coordinateColumns =  new String[] { "name" };
        
        // 7. Declare the specified dominance parameters into Pythia
        // (steps 6 & 7 are optional, however, they are a prerequisite for highlight patterns identification).
    	datasetProfiler.declareDominanceParameters(mode, measurementColumns, coordinateColumns);

    	// 8. Specify the auxiliary data output directory and the desired parts of the analysis procedure 
    	// that should get executed for the computation of the dataset profile.
    	String auxiliaryDataOutputDirectory = "results";
    	boolean shouldRunDescriptiveStats = true;
    	boolean shouldRunHistograms = true;
    	boolean shouldRunAllPairsCorrelations = true;
    	boolean shouldRunDecisionTrees = true;
    	boolean shouldRunHighlightPatterns = true;
        
        // 9. Create a DatasetProfilerParameters object with the parameters specified in step 8
        // and compute the profile of the dataset (this will take a while for big datasets).
        DatasetProfilerParameters parameters =  new DatasetProfilerParameters(
        		auxiliaryDataOutputDirectory,
                shouldRunDescriptiveStats,
                shouldRunHistograms,
                shouldRunAllPairsCorrelations,
                shouldRunDecisionTrees,
                shouldRunHighlightPatterns);
        datasetProfiler.computeProfileOfDataset(parameters);

        // 10. (Optionally) specify an output directory path for the generated reports
        // (unspecified output directory path means that the reports will be generated under the 
        // auxiliary data output directory specified in step 8).
        String outputDirectoryPath = "";
        
        // 11. Generate a report in plain text and markdown format.
        datasetProfiler.generateReport(ReportGeneratorConstants.TXT_REPORT, outputDirectoryPath);
        datasetProfiler.generateReport(ReportGeneratorConstants.MD_REPORT, outputDirectoryPath);
    }
}
```

### <div align="center"> Contributors </div>
- Alexiou Alexandros
- Charisis Alexandros ([Youtube](https://youtu.be/QhAO9OIl6Cg))
- Christodoulos Antoniou
