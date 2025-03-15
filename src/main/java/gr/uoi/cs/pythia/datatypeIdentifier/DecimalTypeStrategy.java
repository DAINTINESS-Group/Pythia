package gr.uoi.cs.pythia.datatypeIdentifier;

import org.apache.spark.sql.Row;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public  class DecimalTypeStrategy extends DataDetectorStrategy {

    private static final String DECIMAL_REGEX = "^[-+]?(\\d+\\.\\d*|\\.\\d+)$";
    private static final Pattern DECIMAL_PATTERN = Pattern.compile(DECIMAL_REGEX);

    public DecimalTypeStrategy() {
        super();
    }

    @Override
    public void findDataType(Row valueInColum) {

            String valueInRowString = valueInColum.get(0).toString().trim();
            if (DECIMAL_PATTERN.matcher(valueInRowString).matches()) {
                new BigDecimal(valueInRowString);
                score += 1;
            }
        }

}

