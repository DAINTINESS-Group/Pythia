package gr.uoi.cs.pythia.datatypeIdentifier;

import org.apache.spark.sql.Row;

import java.util.regex.Pattern;

public class ShortTypeStrategy extends DataDetectorStrategy{
    // Optional sign (+ or -)
    private static final String SIGN_PART = "([+\\-])?";
    // Special case for zero
    private static final String ZERO_PART = "0";
    // Numbers from 1 to 32,767
    private static final String SMALL_NUMBERS_PART = "[1-9]\\d{0,3}|[12]\\d{4}|3[0-1]\\d{3}|32[0-6]\\d{2}|327[0-5]\\d|3276[0-7]";
    // Minimum value (-32,768)
    private static final String MIN_VALUE_PART = "-32768";
    private static final Pattern SHORT_REGEX = Pattern.compile(
            "^"+SIGN_PART+"("+ZERO_PART+"|"+SMALL_NUMBERS_PART+"|"+MIN_VALUE_PART+")$"
    );

    public ShortTypeStrategy(){
        super();
    }

    @Override
    public void findDataType(Row valueInColum){

            String valueInRowString = valueInColum.get(0).toString().trim();
            if(SHORT_REGEX.matcher(valueInRowString).matches()){
                score += 1;
            }
        }
    }
