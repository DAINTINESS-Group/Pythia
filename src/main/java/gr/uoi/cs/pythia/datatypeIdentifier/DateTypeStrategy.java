package gr.uoi.cs.pythia.datatypeIdentifier;

import org.apache.spark.sql.Row;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateTypeStrategy extends DataDetectorStrategy{

    private static final Map<String, Integer> monthDays = new HashMap<>();

    static{
        monthDays.put("January", 31);
        monthDays.put("February", 28);
        monthDays.put("March", 31);
        monthDays.put("April", 30);
        monthDays.put("May", 31);
        monthDays.put("June", 30);
        monthDays.put("July", 31);
        monthDays.put("August", 31);
        monthDays.put("September", 30);
        monthDays.put("October", 31);
        monthDays.put("November", 30);
        monthDays.put("December", 31);
    }

    public DateTypeStrategy(){
        super();
    }

    @Override
    public void findDataType(Row valueInColum){

            String valueInColumString = valueInColum.get(0).toString().trim();
            if(valueInColumString.matches("^\\d{4}-\\d{2}-\\d{2}$")){  // YYYY-MM-DD
                checkAndUpdateScore(valueInColumString, "yyyy-MM-dd");
            } else if(valueInColumString.matches("^\\d{2}-\\d{2}-\\d{4}$")){  // DD-MM-YYYY
                checkAndUpdateScore(valueInColumString, "dd-MM-yyyy");
            } else if(valueInColumString.matches("^\\d{2}/\\d{2}/\\d{4}$")){  // DD/MM/YYYY
                checkAndUpdateScore(valueInColumString, "dd/MM/yyyy");
            } else if(valueInColumString.matches("^\\d{2}/\\d{2}/\\d{2}$")){  // DD/MM/YY
                checkAndUpdateScore(valueInColumString, "dd/MM/yy");
            } else if(valueInColumString.matches("^\\d{2}-\\d{2}-\\d{2}$")){  // MM-DD-YY
                checkAndUpdateScore(valueInColumString, "MM-dd-yy");
            } else if(valueInColumString.matches("^\\d{4}/\\d{2}/\\d{2}$")){  // YYYY/MM/DD
                checkAndUpdateScore(valueInColumString, "yyyy/MM/dd");
            } else if(valueInColumString.matches("^(0[1-9]|[12][0-9]|3[01])\\s(January|February|March|April|May|June|July|August|September|October|November|December)\\s\\d{4}$")){  // DD Month YYYY
                if(isValidDateDDMonthYYYY(valueInColumString)){
                    score += 1;
                }
            } else if(valueInColumString.matches("^(January|February|March|April|May|June|July|August|September|October|November|December)\\s(0[1-9]|[12][0-9]|3[01]),\\s\\d{4}$")){  // Month DD, YYYY
                if(isValidDateMonthDDYYYY(valueInColumString)){
                    score += 1;
                }
            }
    }

    private void checkAndUpdateScore(String date, String pattern){
        if(isValidDate(date, pattern)){
            score += 1;
        }
    }

    private boolean isValidDateDDMonthYYYY(String date){
        String[] parts = date.split(" ");
        if(parts.length!=3) return false;
        String day = parts[0];
        String month = parts[1];
        return isLeapYear(parts, month, day);
    }

    private boolean isValidDateMonthDDYYYY(String date){
        String[] parts = date.split(" ");
        if(parts.length!=3) return false;
        String month = parts[0];
        String dayYear = parts[1].replace(",", "");
        return isLeapYear(parts, month, dayYear);
    }

    private boolean isLeapYear(String[] parts, String month, String dayYear){
        String year = parts[2];
        if(!monthDays.containsKey(month)) return false;
        int dayInt = Integer.parseInt(dayYear);
        int maxDays = monthDays.get(month);
        if(month.equals("February")){
            int yearInt = Integer.parseInt(year);
            maxDays = isLeapYear(yearInt)?29:28;
        }
        return dayInt >= 1 && dayInt <= maxDays && isValidYear(year);
    }

    private boolean isLeapYear(int year){
        return (year % 4==0 && (year % 100!=0 || year % 400==0));
    }

    private boolean isValidYear(String year){
        try {
            Integer.parseInt(year);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidDate(String date, String pattern){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            sdf.setLenient(false);
            Date parsedDate = sdf.parse(date);
            return parsedDate!=null;
        } catch (ParseException e) {
            return false;
        }
    }
}
