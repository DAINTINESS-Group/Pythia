package gr.uoi.cs.pythia.datatypeIdentifier;

import org.apache.spark.sql.Row;

import java.util.regex.Pattern;

public class BooleanTypeStrategy extends DataDetectorStrategy{
    private static final Pattern BOOLEAN_REGEX = Pattern.compile(
            "^(true|false|1|0|yes|no|TRUE|FALSE|YES|NO|on|off|enabled|disabled|ok|not\\sok)$",
            Pattern.CASE_INSENSITIVE
    );

    public BooleanTypeStrategy(){
        super();
    }

    @Override
    public void findDataType(Row valueInColum){

            String valueInRowString = valueInColum.get(0).toString().trim();
            if(BOOLEAN_REGEX.matcher(valueInRowString).matches()){
                if(valueInRowString.equalsIgnoreCase("true") || valueInRowString.equalsIgnoreCase("1") ||
                        valueInRowString.equalsIgnoreCase("yes") || valueInRowString.equalsIgnoreCase("on") ||
                        valueInRowString.equalsIgnoreCase("enabled") || valueInRowString.equalsIgnoreCase("ok")){
                    score += 1;
                } else if(valueInRowString.equalsIgnoreCase("false") || valueInRowString.equalsIgnoreCase("0") ||
                        valueInRowString.equalsIgnoreCase("no") || valueInRowString.equalsIgnoreCase("off") ||
                        valueInRowString.equalsIgnoreCase("disabled") || valueInRowString.equalsIgnoreCase("not ok")){
                    score += 1;
                }
            }
        }
}

