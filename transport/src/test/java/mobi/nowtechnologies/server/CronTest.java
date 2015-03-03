package mobi.nowtechnologies.server;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.CronExpression;

import org.junit.*;
import static org.junit.Assert.*;

import static org.hamcrest.CoreMatchers.is;

/**
 * @author Titov Mykhaylo (titov) 20.10.13 19:23
 */
public class CronTest {

    @Test
    public void shouldReturnTomorrow8AmNextDate() throws Exception {
        //given
        CronExpression cronExpression = new CronExpression("0 0/5 08-19 * * ?");

        //when
        Date nextValidDate = cronExpression.getNextValidTimeAfter(new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").parse("19:55:59 01/01/2013"));

        //then
        assertThat(nextValidDate, is(new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").parse("08:00:00 02/01/2013")));
    }

    @Test
    public void shouldReturnTomorrow8AmInNewYearNextDate() throws Exception {
        //given
        CronExpression cronExpression = new CronExpression("0 0/5 08-19 * * ?");

        //when
        Date nextValidDate = cronExpression.getNextValidTimeAfter(new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").parse("19:55:59 31/12/2013"));

        //then
        assertThat(nextValidDate, is(new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").parse("08:00:00 01/01/2014")));
    }

    @Test
    public void shouldReturn8AmNextDate() throws Exception {
        //given
        CronExpression cronExpression = new CronExpression("0 0/5 08-19 * * ?");

        //when
        Date nextValidDate = cronExpression.getNextValidTimeAfter(new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").parse("07:59:59 02/10/2013"));

        //then
        assertThat(nextValidDate, is(new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").parse("08:00:00 02/10/2013")));
    }

    @Test
    public void shouldReturn8h05AmNextDate() throws Exception {
        //given
        CronExpression cronExpression = new CronExpression("0 0/5 08-19 * * ?");

        //when
        Date nextValidDate = cronExpression.getNextValidTimeAfter(new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").parse("08:00:00 31/08/2013"));

        //then
        assertThat(nextValidDate, is(new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").parse("08:05:00 31/08/2013")));
    }

    @Test
    public void shouldReturn19h55AmNextDate() throws Exception {
        //given
        CronExpression cronExpression = new CronExpression("0 0/5 08-19 * * ?");

        //when
        Date nextValidDate = cronExpression.getNextValidTimeAfter(new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").parse("19:50:00 29/02/2012"));

        //then
        assertThat(nextValidDate, is(new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").parse("19:55:00 29/02/2012")));
    }
}
