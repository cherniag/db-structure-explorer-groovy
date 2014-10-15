package mobi.nowtechnologies.server.shared.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static Date newDate(int dd, int mm, int yyyy) {
        return new DateTime(yyyy, mm, dd, 0, 0, 0, 0).toDate();
    }

    public static Date getDateInUTC(Date inputDate) {
        if (inputDate != null) {
            DateTimeZone timeZone = DateTimeZone.forID("UTC");
            return new DateTime(inputDate, timeZone).toDate();
        }
        return null;
    }

    public static Date getDateWithoutMilliseconds(Date inputDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(inputDate);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public static Date getStartOfDay(Date inputDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(inputDate);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }

}
