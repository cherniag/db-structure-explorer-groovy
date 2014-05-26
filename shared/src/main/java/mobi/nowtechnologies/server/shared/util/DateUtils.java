package mobi.nowtechnologies.server.shared.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

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
}
