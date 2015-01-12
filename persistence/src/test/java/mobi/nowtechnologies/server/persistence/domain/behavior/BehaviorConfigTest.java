package mobi.nowtechnologies.server.persistence.domain.behavior;

import com.google.common.collect.Sets;
import mobi.nowtechnologies.server.persistence.domain.Duration;
import mobi.nowtechnologies.server.persistence.domain.UserStatusType;
import mobi.nowtechnologies.server.shared.enums.DurationUnit;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BehaviorConfigTest {
    BehaviorConfig config = new BehaviorConfig();

    @Test
    public void testUpdateReferralsInfo() throws Exception {
        final int requiredAmount = 1;
        final int period = 2;
        final DurationUnit unit = DurationUnit.HOURS;

        config.updateReferralsInfo(requiredAmount, Duration.forPeriod(period, unit));
        assertEquals(requiredAmount, config.getRequiredReferrals());
        assertEquals(period, config.getReferralsDuration().getAmount());
        assertEquals(unit, config.getReferralsDuration().getUnit());

        config.updateReferralsInfo(Integer.MIN_VALUE, Duration.forPeriod(period, unit));
        assertEquals(BehaviorConfig.IGNORE, config.getRequiredReferrals());
        assertEquals(period, config.getReferralsDuration().getAmount());
        assertEquals(unit, config.getReferralsDuration().getUnit());
    }

    @Test
    public void testGetChartBehavior() throws Exception {
        // given
        config.chartBehaviors = Sets.newHashSet(
                createChartBehavior(ChartBehaviorType.NORMAL),
                createChartBehavior(ChartBehaviorType.PREVIEW),
                createChartBehavior(ChartBehaviorType.SHUFFLED)
        );
        // when
        ChartBehavior chartBehavior = config.getChartBehavior(ChartBehaviorType.NORMAL);
        // then
        assertEquals(ChartBehaviorType.NORMAL, chartBehavior.getType());
    }

    @Test
    public void testGetContentUserStatusBehavior() throws Exception {
        // given
        config.contentUserStatusBehaviors = Sets.newHashSet(
                createContentUserStatusBehavior(UserStatusType.FREE_TRIAL),
                createContentUserStatusBehavior(UserStatusType.LIMITED),
                createContentUserStatusBehavior(UserStatusType.SUBSCRIBED)
        );
        // when
        ContentUserStatusBehavior contentUserStatusBehavior = config.getContentUserStatusBehavior(UserStatusType.SUBSCRIBED);
        // then
        assertEquals(UserStatusType.SUBSCRIBED, contentUserStatusBehavior.getUserStatusType());
    }

    private ContentUserStatusBehavior createContentUserStatusBehavior(UserStatusType userStatusType) {
        ContentUserStatusBehavior cusb = mock(ContentUserStatusBehavior.class);
        when(cusb.getUserStatusType()).thenReturn(userStatusType);
        return cusb;
    }

    private ChartBehavior createChartBehavior(ChartBehaviorType type) {
        ChartBehavior cb = mock(ChartBehavior.class);
        when(cb.getType()).thenReturn(type);
        return cb;
    }
}