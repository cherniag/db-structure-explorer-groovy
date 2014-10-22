package mobi.nowtechnologies.server.persistence.domain.payment;

import static mobi.nowtechnologies.server.shared.Utils.WEEK_SECONDS;
import static mobi.nowtechnologies.server.shared.Utils.getEpochSeconds;
import static mobi.nowtechnologies.server.shared.Utils.getMonthlyNextSubPayment;
import static mobi.nowtechnologies.server.shared.enums.DurationUnit.DAYS;
import static mobi.nowtechnologies.server.shared.enums.DurationUnit.MONTHS;
import static mobi.nowtechnologies.server.shared.enums.DurationUnit.WEEKS;
import static org.joda.time.DateTimeFieldType.dayOfMonth;
import static org.joda.time.DateTimeZone.UTC;
import static org.joda.time.Period.days;
import static org.joda.time.Period.months;
import static org.junit.Assert.*;

import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.DurationUnit;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Calendar;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

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
        int newNextSubPaymentSeconds = period.toNextSubPaymentSeconds(oldNextSubPaymentSeconds);

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
        int newNextSubPaymentSeconds = period.toNextSubPaymentSeconds(oldNextSubPaymentSeconds);

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
        int newNextSubPaymentSeconds = period.toNextSubPaymentSeconds(oldNextSubPaymentSeconds);

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

    @Test
    public void shouldConvert1DayPeriodToMessageCode() {
        //given
        period = new Period().withDuration(1).withDurationUnit(DAYS);

        //when
        String messageCode = period.toMessageCode();

        //then
        assertThat(messageCode, is("per.day"));
    }

    @Test
    public void shouldConvert2DaysPeriodToMessageCode() {
        //given
        period = new Period().withDuration(2).withDurationUnit(DAYS);

        //when
        String messageCode = period.toMessageCode();

        //then
        assertThat(messageCode, is("for.n.days"));
    }

    @Test
    public void shouldConvert1WeekPeriodToMessageCode() {
        //given
        period = new Period().withDuration(1).withDurationUnit(WEEKS);

        //when
        String messageCode = period.toMessageCode();

        //then
        assertThat(messageCode, is("per.week"));
    }

    @Test
    public void shouldConvert2WeeksPeriodToMessageCode() {
        //given
        period = new Period().withDuration(2).withDurationUnit(WEEKS);

        //when
        String messageCode = period.toMessageCode();

        //then
        assertThat(messageCode, is("for.n.weeks"));
    }

    @Test
    public void shouldConvert1MonthPeriodToMessageCode() {
        //given
        period = new Period().withDuration(1).withDurationUnit(MONTHS);

        //when
        String messageCode = period.toMessageCode();

        //then
        assertThat(messageCode, is("per.month"));
    }

    @Test
    public void shouldConvert2MonthsPeriodToMessageCode() {
        //given
        period = new Period().withDuration(2).withDurationUnit(MONTHS);

        //when
        String messageCode = period.toMessageCode();

        //then
        assertThat(messageCode, is("for.n.months"));
    }
}