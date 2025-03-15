package gr.uoi.cs.pythia.datatypeIdentifier;

import org.apache.spark.sql.Row;

import java.util.regex.Pattern;

public  class FloatTypeStrategy extends DataDetectorStrategy {

    private static final String FLOAT_REGEX = "^[-+]?(\\d{1,6}\\.\\d{1,7}|\\d{1,6}[eE][-+]?\\d{1,2}|\\d{1,6}\\.\\d{1,7}[eE][-+]?\\d{1,2})$";
    private static final Pattern FLOAT_PATTERN = Pattern.compile(FLOAT_REGEX);

    public FloatTypeStrategy() {
        super();
    }

    @Override
    public void findDataType(Row valueInColum) {

            String valueInRowString = valueInColum.get(0).toString().trim();
            if (FLOAT_PATTERN.matcher(valueInRowString).matches()) {
                score += 1;

            }
        }
}
