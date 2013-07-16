package mobi.nowtechnologies.server.shared.util;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public final static java.text.DateFormat DD_MM_YYYY = new SimpleDateFormat("dd-MM-yyyy");

    public static Date newDate(int dd, int mm, int yyyy) {
        return new DateTime(yyyy, mm, dd, 0, 0, 0, 0).toDate();
    }
}
