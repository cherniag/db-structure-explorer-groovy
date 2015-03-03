package mobi.nowtechnologies.server.persistence.domain.payment;

import static mobi.nowtechnologies.server.shared.Utils.millisToIntSeconds;
import static mobi.nowtechnologies.server.shared.enums.DurationUnit.MONTHS;

import org.joda.time.DateTime;

import org.junit.*;
import static org.junit.Assert.*;

import static org.hamcrest.core.Is.is;

// @author Titov Mykhaylo (titov) on 22.10.2014.
public class PeriodIT {

    Period period;

    @Test
    public void shouldConvert12MonthsPeriodToNextSubPaymentSecondsWhenNewNextSubPaymentDateDoesNotExistOntMonthOfLeapYear() {
        //given
        period = new Period().withDuration(12).withDurationUnit(MONTHS);
        int oldNextSubPaymentSeconds = millisToIntSeconds(new DateTime("2036-02-29T23:59:59.000-00:00").getMillis());

        //when
        int newNextSubPaymentSeconds = period.toNextSubPaymentSeconds(oldNextSubPaymentSeconds);

        //then
        int expectedNewNextSubPaymentSeconds = millisToIntSeconds(new DateTime("2037-03-01T23:59:59.000-00:00").getMillis());

        assertThat(newNextSubPaymentSeconds, is(expectedNewNextSubPaymentSeconds));
    }

    @Test
    public void shouldConvert1MonthPeriodToNextSubPaymentSecondsWhenNewNextSubPaymentDateDoesNotExistOntMonthOfLeapYear() {
        //given
        period = new Period().withDuration(1).withDurationUnit(MONTHS);
        int oldNextSubPaymentSeconds = millisToIntSeconds(new DateTime("2036-01-31T23:59:59.000-00:00").getMillis());

        //when
        int newNextSubPaymentSeconds = period.toNextSubPaymentSeconds(oldNextSubPaymentSeconds);

        //then
        int expectedNewNextSubPaymentSeconds = millisToIntSeconds(new DateTime("2036-03-01T23:59:59.000-00:00").getMillis());

        assertThat(newNextSubPaymentSeconds, is(expectedNewNextSubPaymentSeconds));
    }

    @Test
    public void shouldConvert1MonthPeriodToNextSubPaymentSecondsWhenNewNextSubPaymentDateExistsOntMonthOfLeapYear() throws Exception {
        //given
        period = new Period().withDuration(1).withDurationUnit(MONTHS);
        int oldNextSubPaymentSeconds = millisToIntSeconds(new DateTime("2036-01-29T23:59:59.000-00:00").getMillis());

        //when
        int newNextSubPaymentSeconds = period.toNextSubPaymentSeconds(oldNextSubPaymentSeconds);

        //then
        int expectedNewNextSubPaymentSeconds = millisToIntSeconds(new DateTime("2036-02-29T23:59:59.000-00:00").getMillis());

        assertThat(newNextSubPaymentSeconds, is(expectedNewNextSubPaymentSeconds));
    }

    @Test
    public void shouldConvert1MonthPeriodToNextSubPaymentSecondsWhenNewNextSubPaymentDateDoesNotExistOntMonthOfNotLeapYear() throws Exception {
        //given
        period = new Period().withDuration(1).withDurationUnit(MONTHS);
        int oldNextSubPaymentSeconds = millisToIntSeconds(new DateTime("2037-01-29T23:59:59.000-00:00").getMillis());

        //when
        int newNextSubPaymentSeconds = period.toNextSubPaymentSeconds(oldNextSubPaymentSeconds);

        //then
        int expectedNewNextSubPaymentSeconds = millisToIntSeconds(new DateTime("2037-03-01T23:59:59.000-00:00").getMillis());
        assertThat(newNextSubPaymentSeconds, is(expectedNewNextSubPaymentSeconds));
    }

    @Test
    public void shouldConvert1MonthPeriodToNextSubPaymentSecondsWhenNewNextSubPaymentDateExistsOntMonthOfNotLeapYear() throws Exception {
        //given
        period = new Period().withDuration(1).withDurationUnit(MONTHS);
        int oldNextSubPaymentSeconds = millisToIntSeconds(new DateTime("2037-01-01T23:59:59.000-00:00").getMillis());

        //when
        int newNextSubPaymentSeconds = period.toNextSubPaymentSeconds(oldNextSubPaymentSeconds);

        //then
        int expectedNewNextSubPaymentSeconds = millisToIntSeconds(new DateTime("2037-02-01T23:59:59.000-00:00").getMillis());
        assertThat(newNextSubPaymentSeconds, is(expectedNewNextSubPaymentSeconds));
    }
}
