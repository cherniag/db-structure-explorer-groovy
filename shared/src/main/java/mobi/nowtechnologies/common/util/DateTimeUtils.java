package mobi.nowtechnologies.common.util;

import mobi.nowtechnologies.server.shared.enums.DurationUnit;

import java.util.Calendar;
import java.util.Date;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Minutes;
import org.joda.time.Seconds;
import org.joda.time.Weeks;

/**
 * This class is wrapper around
 * <pre>{@code
 *  <dependency>
 *      <groupId>joda-time</groupId>
 *      <artifactId>joda-time</artifactId>
 *      <version>X.X</version>
 *  </dependency>
 * }</pre>
 * library which <strong>MUST NOT BE USED</strong> directly outside of this class  for easier upgrades!
 */
public final class DateTimeUtils {

    public static final String UTC_TIME_ZONE_ID = "UTC";
    public static final String GMT_TIME_ZONE_ID = "GMT";

    public static final long SECOND_MILLISECONDS = 1000L;
    public static final int DAY_SECONDS = 86400;
    public static final long DAY_MILLISECONDS = DAY_SECONDS * SECOND_MILLISECONDS;
    public static final int WEEK_DAYS = 7;
    public static final int WEEK_SECONDS = WEEK_DAYS * DAY_SECONDS;

    private DateTimeUtils() {
    }

    public static int truncatedToSeconds(Date date) {
        return (int) (date.getTime() / SECOND_MILLISECONDS);
    }

    public static int getEpochSeconds() {
        return (int) (System.currentTimeMillis() / SECOND_MILLISECONDS);
    }

    public static int getEpochDays() {
        return toEpochDays(System.currentTimeMillis());
    }

    public static int toEpochDays(long millis) {
        return (int) (millis / DAY_MILLISECONDS);
    }

    public static long getEpochMillis() {
        return System.currentTimeMillis();
    }

    public static Date getDateFromInt(int intDate) {
        return new Date(SECOND_MILLISECONDS * intDate);
    }

    public static long secondsToMillis(long seconds) {
        return SECONDS.toMillis(seconds);
    }

    public static long millisToSeconds(long millis) {
        return MILLISECONDS.toSeconds(millis);
    }

    public static int millisToIntSeconds(long millis) {
        return (int) millisToSeconds(millis);
    }

    public static boolean datesNotEquals(Date oldTime, Date newTime) {
        return newTime.getTime() != oldTime.getTime();
    }

    public static Date newDate(int dd, int mm, int yyyy) {
        return new DateTime(yyyy, mm, dd, 0, 0, 0, 0).toDate();
    }

    public static Date getDateInUTC(Date inputDate) {
        DateTime dateTime = toJodaDateTime(inputDate, UTC_TIME_ZONE_ID);
        if (dateTime == null) {
            return null;
        }
        return dateTime.toDate();
    }

    private static DateTime toJodaDateTime(Date date, String timeZoneId) throws IllegalArgumentException {
        if (date != null) {
            return new DateTime(date.getTime(), DateTimeZone.forID(timeZoneId));
        }
        return null;
    }

    public static Date getDateWithoutMilliseconds(Date inputDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(inputDate);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public static Long getTimeWithoutMilliseconds(Long inputTime) {
        return getDateWithoutMilliseconds(new Date(inputTime)).getTime();
    }

    public static Date moveDate(Date date, String timeZoneId, int amount, DurationUnit unit) {
        DateTime dateTime = toJodaDateTime(date, timeZoneId);
        switch (unit) {
            case SECONDS:
                return dateTime.plusSeconds(amount).toDate();
            case MINUTES:
                return dateTime.plusMinutes(amount).toDate();
            case HOURS:
                return dateTime.plusHours(amount).toDate();
            case DAYS:
                return dateTime.plusDays(amount).toDate();
            case WEEKS:
                return dateTime.plusWeeks(amount).toDate();
            case MONTHS:
                return dateTime.plusMonths(amount).toDate();
            case YEARS:
                return dateTime.plusYears(amount).toDate();
            default:
                throw new UnsupportedOperationException();
        }
    }

    public static int toHours(int amount, DurationUnit unit) {
        switch (unit) {
            case SECONDS:
                return Seconds.seconds(amount).toStandardHours().getHours();
            case MINUTES:
                return Minutes.minutes(amount).toStandardHours().getHours();
            case HOURS:
                return amount;
            case DAYS:
                return Days.days(amount).toStandardHours().getHours();
            case WEEKS:
                return Weeks.weeks(amount).toStandardHours().getHours();
            default:
                throw new UnsupportedOperationException();
        }
    }
}
