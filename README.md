<p align="center"> 
    <img height=170 src="https://cdn.discordapp.com/attachments/326432556037832704/936299117766926406/logo3.png"/> 
</p>

## <div align="center">Pythia</div>

#### <div align="center">Java library that produces an automated statistical profile of an input dataset.</div>

A standard dataset is just a text file, with lines, where each line is a record, the fields of which are separated by a
separator (eg. tabs, comma, pipe, etc). After registering a data set, the system produces a 100% automatic statistical
profile of the dataset and generates a report of the findings.

### <div align="center">Setup</div>

---

#### Intellij IDEA Installation Requirements

- Install [**Intellij IDEA**](https://www.jetbrains.com/idea/download/#section=windows) (Community edition is
  free)
- Import the project as a Maven project, and it runs out of the box

#### Eclipse Installation Requirements

- Install [**Eclipse**](https://www.eclipse.org/downloads/)
- Import the project as a Maven project.

_Note_: This project uses [**lombok**](https://projectlombok.org/) to generate boilerplate code at compile time

- Follow the instructions at [**Lombok's official website**](https://projectlombok.org/setup/eclipse)
  in the `Via eclipse plugin installer` section to install it in Eclipse

#### Maven

The project uses a Maven wrapper so there is no need to install it to your system as long as you have the JAVA_HOME
environmental variable pointing to your [**Java 8**](https://www.oracle.com/java/technologies/downloads/) installation
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

<table>
<thead>
	<tr>
		<th>name</th>
		<th>age</th>
		<th>money</th>
	</tr>
</thead>
<tbody>
	<tr>
		<td>Michael</td>
		<td>25</td>
		<td>20</td>
	</tr>
	<tr>
		<td>Andy</td>
		<td>30</td>
		<td>1000</td>
	</tr>
    <tr>
		<td>Justin</td>
		<td>65</td>
		<td>10000</td>
	</tr>
</tbody>
</table>


Sample Main class with API usage for the file above

```java
import gr.uoi.cs.pythia.engine.IDatasetProfiler;
import gr.uoi.cs.pythia.engine.IDatasetProfilerFactory;
import gr.uoi.cs.pythia.labeling.LabelingSystemConstants;
import gr.uoi.cs.pythia.labeling.Rule;
import gr.uoi.cs.pythia.labeling.RuleSet;
import gr.uoi.cs.pythia.report.ReportGeneratorConstants;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws AnalysisException {
        // Initialize a DatasetProfiler
        IDatasetProfiler datasetProfiler = IDatasetProfilerFactory.createDatasetProfiler();

        // Specify input file schema
        StructType schema =
                new StructType(
                        new StructField[]{
                                new StructField("name", DataTypes.StringType, true, Metadata.empty()),
                                new StructField("age", DataTypes.IntegerType, true, Metadata.empty()),
                                new StructField("money", DataTypes.IntegerType, true, Metadata.empty()),
                        });

        // Register the input file
        datasetProfiler.registerDataset("people", "people.csv", schema);

        // Specify labeling rules for a column
        List<Rule> rules = new ArrayList<>();
        rules.add(new Rule("money", LabelingSystemConstants.LEQ, 20, "poor"));
        rules.add(new Rule("money", LabelingSystemConstants.LEQ, 1000, "mid"));
        rules.add(new Rule("money", LabelingSystemConstants.GT, 1000, "rich"));

        // Create Ruleset and specify the new column name
        RuleSet ruleSet = new RuleSet("money_labeled", rules);

        // Compute the new labeled column
        datasetProfiler.computeLabeledColumn(ruleSet);

        // Compute the profile of the Dataset (this will take a while for big datasets)
        datasetProfiler.computeProfileOfDataset();

        // Generate a report (txt report in this case)
        datasetProfiler.generateReport(ReportGeneratorConstants.TXT_REPORT, "report.txt");
    }
}
```
