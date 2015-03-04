package mobi.nowtechnologies.server.persistence.domain.payment;

import static mobi.nowtechnologies.server.shared.enums.DurationUnit.DAYS;
import static mobi.nowtechnologies.server.shared.enums.DurationUnit.MONTHS;
import static mobi.nowtechnologies.server.shared.enums.DurationUnit.WEEKS;

import org.junit.*;
import static org.junit.Assert.*;

import static org.hamcrest.core.Is.is;

public class PeriodMessageKeyBuilderTest {

    @Test
    public void shouldConvert1DayPeriodToMessageCode() {
        //given
        Period period = new Period().withDuration(1).withDurationUnit(DAYS);
        PeriodMessageKeyBuilder builder = getPeriodMessageKeyBuilder(period);

        //when
        String messageCode = builder.getMessageKey(period);

        //then
        assertThat(messageCode, is("per.day"));
    }

    @Test
    public void shouldConvert2DaysPeriodToMessageCode() {
        //given
        Period period = new Period().withDuration(2).withDurationUnit(DAYS);
        PeriodMessageKeyBuilder builder = getPeriodMessageKeyBuilder(period);

        //when
        String messageCode = builder.getMessageKey(period);

        //then
        assertThat(messageCode, is("for.n.days"));
    }

    @Test
    public void shouldConvert1WeekPeriodToMessageCode() {
        //given
        Period period = new Period().withDuration(1).withDurationUnit(WEEKS);
        PeriodMessageKeyBuilder builder = getPeriodMessageKeyBuilder(period);

        //when
        String messageCode = builder.getMessageKey(period);

        //then
        assertThat(messageCode, is("per.week"));
    }

    @Test
    public void shouldConvert2WeeksPeriodToMessageCode() {
        //given
        Period period = new Period().withDuration(2).withDurationUnit(WEEKS);
        PeriodMessageKeyBuilder builder = getPeriodMessageKeyBuilder(period);

        //when
        String messageCode = builder.getMessageKey(period);

        //then
        assertThat(messageCode, is("for.n.weeks"));
    }

    @Test
    public void shouldConvert1MonthPeriodToMessageCode() {
        //given
        Period period = new Period().withDuration(1).withDurationUnit(MONTHS);
        PeriodMessageKeyBuilder builder = getPeriodMessageKeyBuilder(period);

        //when
        String messageCode = builder.getMessageKey(period);

        //then
        assertThat(messageCode, is("per.month"));
    }

    @Test
    public void shouldConvert2MonthsPeriodToMessageCode() {
        //given
        Period period = new Period().withDuration(2).withDurationUnit(MONTHS);
        PeriodMessageKeyBuilder builder = getPeriodMessageKeyBuilder(period);

        //when
        String messageCode = builder.getMessageKey(period);

        //then
        assertThat(messageCode, is("for.n.months"));
    }

    private PeriodMessageKeyBuilder getPeriodMessageKeyBuilder(Period period) {
        return PeriodMessageKeyBuilder.of(period.getDurationUnit());
    }
}