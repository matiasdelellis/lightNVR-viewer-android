package ar.com.delellis.lightnvrviewer.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Util {
    public static String getLocalTime(String dateTime) {
        Date utcDateTime = null;
        String dueDateAsNormal ="";
        SimpleDateFormat timeServerFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss 'UTC'");
        timeServerFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            utcDateTime = timeServerFormatter.parse(dateTime);
            SimpleDateFormat localFormatter = new SimpleDateFormat("MM/dd/yyyy - hh:mm:ss a");

            localFormatter.setTimeZone(TimeZone.getDefault());
            dueDateAsNormal = localFormatter.format(utcDateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dueDateAsNormal;
    }
}