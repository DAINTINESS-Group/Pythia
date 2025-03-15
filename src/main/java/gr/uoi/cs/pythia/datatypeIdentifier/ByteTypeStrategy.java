package gr.uoi.cs.pythia.datatypeIdentifier;

import org.apache.spark.sql.Row;

import java.util.regex.Pattern;

class ByteTypeStrategy extends DataDetectorStrategy{

    private static final String SIGN_PART = "(//+|-)?";
    private static final String ZERO_PART = "0";
    // Numbers from 1 to 127
    private static final String SMALL_NUMBERS_PART = "[1-9]\\d?|1[0-1]\\d|12[0-7]";
    // Minimum value (-128)
    private static final String MIN_VALUE_PART = "-128";
    // Combining all cases
    private static final Pattern BYTE_REGEX = Pattern.compile(
            "^"+SIGN_PART+"("+ZERO_PART+"|"+SMALL_NUMBERS_PART+"|"+MIN_VALUE_PART+")$"
    );

    public ByteTypeStrategy(){
        super();
    }

    @Override
    public void findDataType(Row valueInColum){
            String valueInRowString = valueInColum.get(0).toString().trim();
            if(BYTE_REGEX.matcher(valueInRowString).matches()){
                score += 1;
            }
        }
}

