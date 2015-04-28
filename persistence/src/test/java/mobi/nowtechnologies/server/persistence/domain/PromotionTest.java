package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.persistence.domain.payment.Period;
import mobi.nowtechnologies.server.shared.enums.DurationUnit;
import static mobi.nowtechnologies.server.shared.Utils.WEEK_SECONDS;

import org.junit.*;
import static org.junit.Assert.*;

import static org.hamcrest.Matchers.is;

/**
 * @author Titov Mykhaylo (titov) on 11.04.2014.
 */
public class PromotionTest {


    @Test
    public void shouldReturn4WeeksFreeWeeksEndDateWhenPromoEndDateIs7WeeksAndAndPromoFreeWeeksIs5AndFreeTrialStartedTimestampSecondsIsWeekSeconds() throws Exception {
        //given
        Promotion promotion = new Promotion();
        promotion.setPeriod(new Period(DurationUnit.WEEKS, 5));
        promotion.setEndDate(7 * WEEK_SECONDS);
        int freeTrialStartedTimestampSeconds = WEEK_SECONDS;

        //when
        int freeWeeksEndDate = promotion.getEndSeconds(freeTrialStartedTimestampSeconds);

        //then
        assertThat(freeWeeksEndDate, is(freeTrialStartedTimestampSeconds + 5 * WEEK_SECONDS));
    }

    @Test
    public void shouldReturn7WeeksFreeWeeksEndDateWhenPromoEndDateIs7WeeksAndPromoFreeWeeksIs0AndFreeTrialStartedTimestampSecondsIsWeekSeconds() throws Exception {
        //given
        Promotion promotion = new Promotion();
        promotion.setPeriod(new Period(DurationUnit.WEEKS, 0));
        promotion.setEndDate(7 * WEEK_SECONDS);
        int freeTrialStartedTimestampSeconds = WEEK_SECONDS;

        //when
        int freeWeeksEndDate = promotion.getEndSeconds(freeTrialStartedTimestampSeconds);

        //then
        assertThat(freeWeeksEndDate, is(7 * WEEK_SECONDS));
    }

    @Test
    public void shouldReturn7WeeksFreeWeeksEndDateWhenPromoEndDateIs7WeeksAndPeriodIsNullAndFreeTrialStartedTimestampSecondsIsWeekSeconds() throws Exception {
        //given
        Promotion promotion = new Promotion();
        promotion.setPeriod(null);
        promotion.setEndDate(7 * WEEK_SECONDS);
        int freeTrialStartedTimestampSeconds = WEEK_SECONDS;

        //when
        int freeWeeksEndDate = promotion.getEndSeconds(freeTrialStartedTimestampSeconds);

        //then
        assertThat(freeWeeksEndDate, is(7 * WEEK_SECONDS));
    }
}
