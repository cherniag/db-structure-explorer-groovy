package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.shared.Utils;
import org.junit.Assert;
import org.junit.Test;

import static mobi.nowtechnologies.server.shared.Utils.WEEK_SECONDS;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Titov Mykhaylo (titov) on 11.04.2014.
 */
public class PromotionTest {

    @Test
    public void shouldReturn1WhenFreeWeeksIs1() throws Exception {
        //given
        Promotion promotion = new Promotion().withFreeWeeks((byte)1);
        int freeTrialStartedTimestampSeconds = Integer.MAX_VALUE;

        //when
        int freeWeeks = promotion.getFreeWeeks(freeTrialStartedTimestampSeconds);

        //then
        assertThat(freeWeeks, is(1));
    }

    @Test
    public void shouldReturn6WhenPromoEndDateIs7WeeksAndPromoFreeWeeksIs0AndFreeTrialStartedTimestampSecondsIsWeekSeconds() throws Exception {
        //given
        Promotion promotion = new Promotion().withFreeWeeks((byte) 0).withEndDate(7* WEEK_SECONDS);
        int freeTrialStartedTimestampSeconds = WEEK_SECONDS;

        //when
        int freeWeeks = promotion.getFreeWeeks(freeTrialStartedTimestampSeconds);

        //then
        assertThat(freeWeeks, is(6));
    }

    @Test
    public void shouldReturn4WeeksFreeWeeksEndDateWhenPromoEndDateIs7WeeksAndAndPromoFreeWeeksIs5AndFreeTrialStartedTimestampSecondsIsWeekSeconds() throws Exception {
        //given
        Promotion promotion = new Promotion().withFreeWeeks((byte) 5).withEndDate(7* WEEK_SECONDS);
        int freeTrialStartedTimestampSeconds = WEEK_SECONDS;

        //when
        int freeWeeksEndDate = promotion.getFreeWeeksEndDate(freeTrialStartedTimestampSeconds);

        //then
        assertThat(freeWeeksEndDate, is(freeTrialStartedTimestampSeconds+5*WEEK_SECONDS));
    }

    @Test
    public void shouldReturn7WeeksFreeWeeksEndDateWhenPromoEndDateIs7WeeksAndPromoFreeWeeksIs0AndFreeTrialStartedTimestampSecondsIsWeekSeconds() throws Exception {
        //given
        Promotion promotion = new Promotion().withFreeWeeks((byte) 0).withEndDate(7* WEEK_SECONDS);
        int freeTrialStartedTimestampSeconds = WEEK_SECONDS;

        //when
        int freeWeeksEndDate = promotion.getFreeWeeksEndDate(freeTrialStartedTimestampSeconds);

        //then
        assertThat(freeWeeksEndDate, is(7*WEEK_SECONDS));
    }
}
