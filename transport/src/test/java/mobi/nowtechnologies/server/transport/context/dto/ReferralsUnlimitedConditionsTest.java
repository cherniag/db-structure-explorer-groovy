package mobi.nowtechnologies.server.transport.context.dto;

import mobi.nowtechnologies.common.util.DateTimeUtils;
import mobi.nowtechnologies.server.persistence.domain.behavior.ChartBehaviorType;
import mobi.nowtechnologies.server.shared.enums.DurationUnit;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class ReferralsUnlimitedConditionsTest {

    @Test
    public void testSRV502() throws Exception {
        Date now = new Date();
        ChartBehaviorDto freeTrialEvent = new ChartBehaviorDto(now, ChartBehaviorType.PREVIEW);
        freeTrialEvent.setLockedAction("");

        Date inAMonth = DateTimeUtils.moveDate(now, DateTimeUtils.UTC_TIME_ZONE_ID, 4, DurationUnit.WEEKS);
        ChartBehaviorDto inAMonthEvent = new ChartBehaviorDto(inAMonth, ChartBehaviorType.PREVIEW);
        inAMonthEvent.setLockedAction("");

        List<ChartBehaviorDto> content = new ArrayList<ChartBehaviorDto>(1);
        content.add(freeTrialEvent);
        content.add(inAMonthEvent);

        content = new ReferralsUnlimitedConditions().unlockChartBehaviors(content);
        assertEquals(1, content.size());
        assertEquals(ChartBehaviorType.NORMAL, content.get(0).getChartBehaviorType());
        assertFalse(content.get(0).shouldUnlock());
        assertSame(now, content.get(0).getValidFrom());

        // never locked content

        content.clear();

        now = new Date();
        freeTrialEvent = new ChartBehaviorDto(now, ChartBehaviorType.PREVIEW);

        inAMonth = DateTimeUtils.moveDate(now, DateTimeUtils.UTC_TIME_ZONE_ID, 4, DurationUnit.WEEKS);
        inAMonthEvent = new ChartBehaviorDto(inAMonth, ChartBehaviorType.SHUFFLED);

        content = new ArrayList<ChartBehaviorDto>(1);
        content.add(freeTrialEvent);
        content.add(inAMonthEvent);

        content = new ReferralsUnlimitedConditions().unlockChartBehaviors(content);
        assertEquals(2, content.size());

        assertEquals(ChartBehaviorType.PREVIEW, content.get(0).getChartBehaviorType());
        assertFalse(content.get(0).shouldUnlock());
        assertSame(now, content.get(0).getValidFrom());

        assertEquals(ChartBehaviorType.SHUFFLED, content.get(1).getChartBehaviorType());
        assertFalse(content.get(1).shouldUnlock());
        assertSame(inAMonth, content.get(1).getValidFrom());
    }

    @Test
    public void testSRV503() throws Exception {
        Date now = new Date();
        ChartBehaviorDto event = new ChartBehaviorDto(now, ChartBehaviorType.PREVIEW);

        List<ChartBehaviorDto> content = new ArrayList<ChartBehaviorDto>(1);
        content.add(event);

        content = new ReferralsUnlimitedConditions().unlockChartBehaviors(content);
        assertEquals(1, content.size());
        assertEquals(ChartBehaviorType.PREVIEW, content.get(0).getChartBehaviorType());
        assertFalse(content.get(0).shouldUnlock());
        assertSame(now, content.get(0).getValidFrom());

        content.clear();

        event = new ChartBehaviorDto(now, ChartBehaviorType.PREVIEW);
        event.setLockedAction("");

        content = new ArrayList<ChartBehaviorDto>(1);
        content.add(event);

        content = new ReferralsUnlimitedConditions().unlockChartBehaviors(content);
        assertEquals(1, content.size());
        assertEquals(ChartBehaviorType.NORMAL, content.get(0).getChartBehaviorType());
        assertFalse(content.get(0).shouldUnlock());
        assertSame(now, content.get(0).getValidFrom());
    }

}