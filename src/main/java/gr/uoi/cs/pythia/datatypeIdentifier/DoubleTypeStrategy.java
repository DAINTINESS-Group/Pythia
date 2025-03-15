package gr.uoi.cs.pythia.datatypeIdentifier;

import org.apache.spark.sql.Row;

import java.util.regex.Pattern;

public class DoubleTypeStrategy extends DataDetectorStrategy {

    private static final String DOUBLE_REGEX = "^[-+]?(\\d{1,15}\\.\\d{1,16}|\\d{1,15}[eE][-+]?\\d{1,3}|\\d{1,15}\\.\\d{1,16}[eE][-+]?\\d{1,3})$";
    private static final Pattern DOUBLE_PATTERN = Pattern.compile(DOUBLE_REGEX);

    public DoubleTypeStrategy() {
        super();
    }

    @Override
    public void findDataType(Row valueInColum) {

            String valueInRowString = valueInColum.get(0).toString().trim();
            if (DOUBLE_PATTERN.matcher(valueInRowString).matches()) {
                score += 1;
            }
        }

}

