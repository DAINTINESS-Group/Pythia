package gr.uoi.cs.pythia.datatypeIdentifier;

import org.apache.spark.sql.Row;

import java.util.regex.Pattern;

public class IntegerTypeStrategy extends DataDetectorStrategy {


private static final String INTEGER_REGEX = "^[-+]?(\\d{1,9}|214748364[0-7]|-2147483648)$";
    Pattern INTEGER_pattern= Pattern.compile(INTEGER_REGEX);

    public IntegerTypeStrategy() {
        super();
    }

    @Override
    public void findDataType(Row valueInColum) {

            String valueInRowString = valueInColum.get(0).toString().trim();
            if (INTEGER_pattern.matcher(valueInRowString).matches()) {
                score += 1;
            }
        }
    }
