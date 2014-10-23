package mobi.nowtechnologies.server.shared.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Oleg Artomov on 10/16/2014.
 */
public class HeaderUtils {
    private static final String[] DATE_FORMATS = new String[]{
            "EEE, dd MMM yyyy HH:mm:ss zzz",
            "EEE, dd-MMM-yy HH:mm:ss zzz",
            "EEE MMM dd HH:mm:ss yyyy"
    };
    private static TimeZone GMT = TimeZone.getTimeZone("GMT");

    public static Date convertStringValueToDate(String value){
        for (String dateFormat : DATE_FORMATS) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
            simpleDateFormat.setTimeZone(GMT);
            try {
                return simpleDateFormat.parse(value);
            } catch (ParseException e) {
                // ignore
            }
        }
        return null;
    }
}
