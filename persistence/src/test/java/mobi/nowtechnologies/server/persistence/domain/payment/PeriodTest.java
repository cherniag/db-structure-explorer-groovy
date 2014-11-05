package mobi.nowtechnologies.server.persistence.domain.payment;

import mobi.nowtechnologies.server.shared.Utils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static java.lang.Math.max;
import static mobi.nowtechnologies.server.shared.Utils.WEEK_SECONDS;
import static mobi.nowtechnologies.server.shared.Utils.getEpochSeconds;
import static mobi.nowtechnologies.server.shared.enums.DurationUnit.*;
import static org.hamcrest.core.Is.is;
import static org.joda.time.DateTimeFieldType.dayOfMonth;
import static org.joda.time.DateTimeZone.UTC;
import static org.joda.time.Period.days;
import static org.joda.time.Period.months;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Utils.class, Period.class, DateTime.class})
public class PeriodTest {

    Period period;

    @Test
    public void shouldConvert7DaysPeriodToNextSubPaymentSeconds() {
        //given
        period = new Period().withDuration(7).withDurationUnit(DAYS);
        int oldNextSubPaymentSeconds = 0;

        mockStatic(Utils.class);
        when(Utils.getEpochSeconds()).thenReturn(WEEK_SECONDS);

        //when
        int nextSeconds = max(getEpochSeconds(), oldNextSubPaymentSeconds);
        int newNextSubPaymentSeconds = period.toNextSubPaymentSeconds(nextSeconds);

        //then
        assertThat(newNextSubPaymentSeconds, is(2*WEEK_SECONDS));
    }

    @Test
    public void shouldConvert2WeeksPeriodToNextSubPaymentSeconds() {
        //given
        period = new Period().withDuration(1).withDurationUnit(WEEKS);
        int oldNextSubPaymentSeconds = 0;

        mockStatic(Utils.class);
        when(Utils.getEpochSeconds()).thenReturn(2*WEEK_SECONDS);

        //when
        int nextSeconds = max(getEpochSeconds(), oldNextSubPaymentSeconds);
        int newNextSubPaymentSeconds = period.toNextSubPaymentSeconds(nextSeconds);

        //then
        assertThat(newNextSubPaymentSeconds, is(3*WEEK_SECONDS));
    }

    @Test
    public void shouldConvert20MonthsPeriodToNextSubPaymentSecondsUsesCurrentTimeAsSubscriptionStartTime() throws Exception {
        //given
        int duration = 20;
        period = new Period().withDuration(duration).withDurationUnit(MONTHS);
        int oldNextSubPaymentSeconds = 0;

        int currentTimeSeconds = 2 * WEEK_SECONDS;
        mockStatic(Utils.class);
        when(Utils.getEpochSeconds()).thenReturn(currentTimeSeconds);
        long currentTimeMillis = currentTimeSeconds * 1000L;
        when(Utils.secondsToMillis(currentTimeSeconds)).thenReturn(currentTimeMillis);

        DateTime startDateTimeMock = mock(DateTime.class);
        whenNew(DateTime.class).withParameterTypes(long.class, DateTimeZone.class).withArguments(currentTimeMillis, UTC).thenReturn(startDateTimeMock);

        when(startDateTimeMock.get(dayOfMonth())).thenReturn(29);

        DateTime dateTimePlusDurationMonthsMock = mock(DateTime.class);
        when(startDateTimeMock.plus(months(duration))).thenReturn(dateTimePlusDurationMonthsMock);

        when(dateTimePlusDurationMonthsMock.get(dayOfMonth())).thenReturn(28);

        DateTime expectedDateTimeMock = mock(DateTime.class);
        when(dateTimePlusDurationMonthsMock.plus(days(1))).thenReturn(expectedDateTimeMock);
        int expectedNewNextSubPaymentSeconds = 1;
        long expectedNewNextSubPaymentMillis = expectedNewNextSubPaymentSeconds*1000L;
        when(expectedDateTimeMock.getMillis()).thenReturn(expectedNewNextSubPaymentMillis);

        when(Utils.millisToIntSeconds(expectedNewNextSubPaymentMillis)).thenReturn(expectedNewNextSubPaymentSeconds);

        //when
        int nextSeconds = max(getEpochSeconds(), oldNextSubPaymentSeconds);
        int newNextSubPaymentSeconds = period.toNextSubPaymentSeconds(nextSeconds);

        //then
        assertThat(newNextSubPaymentSeconds, is(expectedNewNextSubPaymentSeconds));
    }

    @Test
    public void shouldConvert20MonthsPeriodToNextSubPaymentSecondsUsesOldNextSubPaymentAsSubscriptionStartTime() throws Exception {
        //given
        int duration = 20;
        period = new Period().withDuration(duration).withDurationUnit(MONTHS);
        int oldNextSubPaymentSeconds = 2 * WEEK_SECONDS;

        int currentTimeSeconds = 0;
        mockStatic(Utils.class);
        when(Utils.getEpochSeconds()).thenReturn(currentTimeSeconds);
        long currentTimeMillis = currentTimeSeconds * 1000L;
        when(Utils.secondsToMillis(currentTimeSeconds)).thenReturn(currentTimeMillis);

        DateTime startDateTimeMock = mock(DateTime.class);
        whenNew(DateTime.class).withParameterTypes(long.class, DateTimeZone.class).withArguments(currentTimeMillis, UTC).thenReturn(startDateTimeMock);

        when(startDateTimeMock.get(dayOfMonth())).thenReturn(29);

        DateTime dateTimePlusDurationMonthsMock = mock(DateTime.class);
        when(startDateTimeMock.plus(months(duration))).thenReturn(dateTimePlusDurationMonthsMock);

        when(dateTimePlusDurationMonthsMock.get(dayOfMonth())).thenReturn(28);

        DateTime expectedDateTimeMock = mock(DateTime.class);
        when(dateTimePlusDurationMonthsMock.plus(days(1))).thenReturn(expectedDateTimeMock);
        int expectedNewNextSubPaymentSeconds = 1;
        long expectedNewNextSubPaymentMillis = expectedNewNextSubPaymentSeconds*1000L;
        when(expectedDateTimeMock.getMillis()).thenReturn(expectedNewNextSubPaymentMillis);

        when(Utils.millisToIntSeconds(expectedNewNextSubPaymentMillis)).thenReturn(expectedNewNextSubPaymentSeconds);

        //when
        int newNextSubPaymentSeconds = period.toNextSubPaymentSeconds(oldNextSubPaymentSeconds);

        //then
        assertThat(newNextSubPaymentSeconds, is(expectedNewNextSubPaymentSeconds));
    }


}