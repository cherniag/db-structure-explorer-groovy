package mobi.nowtechnologies.server.user.rules;

import mobi.nowtechnologies.server.persistence.domain.Promotion;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import org.junit.*;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

//TODO: update test to test public isValid instead of isValidImpl (time issue)
public class PromotionBasedValidationDelegateTest {

    private PromotionBasedValidationDelegate testInstance;

    @Test
    public void shouldBeValid() {
        Promotion promotion = new Promotion();
        updateWithValidDate(promotion, 0);
        promotion.setIsActive(true);
        testInstance = new PromotionBasedValidationDelegate(null);
        MatcherAssert.assertThat(testInstance.isValidImpl(promotion), Matchers.is(true));
    }

    @Test
    public void shouldBeNotValidBecauseOfNotActive() {
        Promotion promotion = new Promotion();
        updateWithValidDate(promotion, 0);
        promotion.setIsActive(false);
        testInstance = new PromotionBasedValidationDelegate(null);
        MatcherAssert.assertThat(testInstance.isValidImpl(promotion), Matchers.is(false));
    }

    @Test
    public void shouldBeNotValidBecauseWeekAgoFinished() {
        Promotion promotion = new Promotion();
        updateWithValidDate(promotion, -7);
        promotion.setIsActive(true);
        testInstance = new PromotionBasedValidationDelegate(null);
        MatcherAssert.assertThat(testInstance.isValidImpl(promotion), Matchers.is(false));
    }

    @Test
    public void shouldBeNotValidBecauseWillBeActiveOnNextWeek() {
        Promotion promotion = new Promotion();
        updateWithValidDate(promotion, 7);
        promotion.setIsActive(true);
        testInstance = new PromotionBasedValidationDelegate(null);
        MatcherAssert.assertThat(testInstance.isValidImpl(promotion), Matchers.is(false));
    }

    private void updateWithValidDate(Promotion promotion, int dayOffset) {
        Date nowDate = new Date();
        Date endDate = DateUtils.addDays(nowDate, 1 + dayOffset);
        Date startDate = DateUtils.addDays(nowDate, -1 + dayOffset);
        promotion.setEndDate((int) (endDate.getTime() / 1000L));
        promotion.setStartDate((int) (startDate.getTime() / 1000L));
    }
}
