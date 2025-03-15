package gr.uoi.cs.pythia.datatypeIdentifier;


import org.apache.spark.sql.Row;

import java.util.regex.Pattern;

public class LongTypeStrategy extends DataDetectorStrategy {

    private static final Pattern LONG_REGEX = Pattern.compile(
            "^(0|" +                       // 0
                    "[+-]?([1-9]\\d{0,17}|" +       // Numbers: 1-18 digits
                    "922337203685477580[0-7]|" +     // Max Long number: 9223372036854775807
                    "-9223372036854775808))$"        // Min Long number: -9223372036854775808
    );

    public LongTypeStrategy() {
        super();
    }

    @Override
    public void findDataType(Row valueInColum) {
            String valueInRowString = valueInColum.get(0).toString().trim();
            if (LONG_REGEX.matcher(valueInRowString).matches()) {
                score += 1;
            }
        }

    }

