package gr.uoi.cs.pythia.datatypeIdentifier;

import org.apache.spark.sql.Row;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimestampTypeStrategy extends DataDetectorStrategy{

    public TimestampTypeStrategy(){
        super();
    }

    @Override
    public void findDataType(Row valueInColum){
            String valueInRowString = valueInColum.get(0).toString().trim();

            //(YYYY-MM-DD HH:MM:SS)
            if(valueInRowString.matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")){
                if(isValidTimestamp(valueInRowString, "yyyy-MM-dd HH:mm:ss")){
                    score += 1;
                }
            }
            // (YYYY-MM-DD HH:MM:SS.SSS)
            else if(valueInRowString.matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}$")){
                if(isValidTimestamp(valueInRowString, "yyyy-MM-dd HH:mm:ss.SSS")){
                    score += 1;
                }
            }
            // (YYYY-MM-DD HH:MM:SS±HH:MM)
            else if(valueInRowString.matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}[+-]\\d{2}:\\d{2}$")){
                if(isValidTimestampWithTimezone(valueInRowString)){
                    score += 1;
                }
            }
    }


    private boolean isValidTimestampWithTimezone(String timestamp){
        try {

            String regex = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}([+-]\\d{2}:\\d{2})$";
            if(timestamp.matches(regex)){
                String baseTimestamp = timestamp.substring(0, 19);
                String timezone = timestamp.substring(19); // Ζώνη ώρας
                if(!isValidTimezone(timezone)){
                    return false;
                }
                return isValidTimestamp(baseTimestamp, "yyyy-MM-dd HH:mm:ss");
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }


    private boolean isValidTimezone(String timezone){
        String[] parts = timezone.split(":");
        int hours = Integer.parseInt(parts[0].substring(1));
        return hours <= 14 && hours >= 0;
    }

    private boolean isValidTimestamp(String timestamp, String pattern){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            sdf.setLenient(false);
            Date parsedDate = sdf.parse(timestamp);
            return parsedDate!=null;
        } catch (Exception e) {
            return false;
        }
    }
}
