package mobi.nowtechnologies.server.shared.enums;

import static mobi.nowtechnologies.server.shared.enums.DurationUnit.DAYS;
import static mobi.nowtechnologies.server.shared.enums.DurationUnit.MONTHS;
import static mobi.nowtechnologies.server.shared.enums.DurationUnit.WEEKS;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class DurationUnitTest {

    @Test
    public void shouldSayThatDaysUnitsAreTheSame() {
        //given
        DurationUnit days = DAYS;

        //when
        int result = days.compareTo(DAYS);

        //then
        assertThat(result, is(0));
    }

    @Test
    public void shouldSayThatDaysUnitIsLessThanWeeksUnit() {
        //given
        DurationUnit days = DAYS;

        //when
        int result = days.compareTo(WEEKS);

        //then
        assertThat(result, lessThan(0));
    }

    @Test
    public void shouldSayThatDaysUnitIsLessThanMonthsUnit() {
        //given
        DurationUnit days = DAYS;

        //when
        int result = days.compareTo(MONTHS);

        //then
        assertThat(result, lessThan(0));
    }

    @Test
    public void shouldSayThatWeeksUnitIsMoreThanDaysUnit() {
        //given
        DurationUnit weeks = WEEKS;

        //when
        int result = weeks.compareTo(DAYS);

        //then
        assertThat(result, greaterThan(0));
    }

    @Test
    public void shouldSayThatWeeksUnitsAreTheSame() {
        //given
        DurationUnit weeks = WEEKS;

        //when
        int result = weeks.compareTo(WEEKS);

        //then
        assertThat(result, is(0));
    }

    @Test
    public void shouldSayThatWeeksUnitIsLessThanMonthsUnit() {
        //given
        DurationUnit weeks = WEEKS;

        //when
        int result = weeks.compareTo(MONTHS);

        //then
        assertThat(result, lessThan(0));
    }


    @Test
    public void shouldSayThatMonthsUnitIsMoreThanDaysUnit() {
        //given
        DurationUnit months = MONTHS;

        //when
        int result = months.compareTo(DAYS);

        //then
        assertThat(result, greaterThan(0));
    }

    @Test
    public void shouldSayThatMonthsUnitIsMoreThanWeeksUnit() {
        //given
        DurationUnit months = MONTHS;

        //when
        int result = months.compareTo(WEEKS);

        //then
        assertThat(result, greaterThan(0));
    }

    @Test
    public void shouldSayThatMonthsUnitsAreTheSame() {
        //given
        DurationUnit months = MONTHS;

        //when
        int result = months.compareTo(MONTHS);

        //then
        assertThat(result, is(0));
    }
}