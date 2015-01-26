package mobi.nowtechnologies.server.transport.context.dto;

import mobi.nowtechnologies.common.util.DateTimeUtils;
import mobi.nowtechnologies.server.persistence.domain.behavior.ChartBehaviorType;
import mobi.nowtechnologies.server.shared.enums.DurationUnit;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class ReferralsLimitedConditionsTest {

    @Test
    public void testSRV497() throws Exception {
        Date now = new Date();
        ChartBehaviorDto freeTrialNormal = new ChartBehaviorDto(now, ChartBehaviorType.NORMAL);
        Date inAMonth = DateTimeUtils.moveDate(now, DateTimeUtils.UTC_TIME_ZONE_ID, 4, DurationUnit.WEEKS);
        ChartBehaviorDto limitedPreview = new ChartBehaviorDto(inAMonth, ChartBehaviorType.PREVIEW);

        List<ChartBehaviorDto> content = new ArrayList<ChartBehaviorDto>(1);
        content.add(freeTrialNormal);
        content.add(limitedPreview);

        Date expDate = DateTimeUtils.moveDate(now, DateTimeUtils.UTC_TIME_ZONE_ID, 4, DurationUnit.HOURS);
        content = new ReferralsLimitedConditions(expDate).unlockChartBehaviors(content);

        assertEquals(2, content.size());
        assertEquals(ChartBehaviorType.NORMAL, content.get(0).getChartBehaviorType());
        assertFalse(content.get(0).shouldUnlock());
        assertSame(now, content.get(0).getValidFrom());

        assertEquals(ChartBehaviorType.PREVIEW, content.get(1).getChartBehaviorType());
        assertFalse(content.get(1).shouldUnlock());
        assertSame(inAMonth, content.get(1).getValidFrom());

        content.clear();

        now = new Date();
        ChartBehaviorDto freeTrialPreviewLocked = new ChartBehaviorDto(now, ChartBehaviorType.PREVIEW);
        freeTrialPreviewLocked.setLockedAction("");
        inAMonth = DateTimeUtils.moveDate(now, DateTimeUtils.UTC_TIME_ZONE_ID, 4, DurationUnit.WEEKS);
        limitedPreview = new ChartBehaviorDto(inAMonth, ChartBehaviorType.PREVIEW);

        content = new ArrayList<ChartBehaviorDto>(1);
        content.add(freeTrialPreviewLocked);
        content.add(limitedPreview);

        expDate = DateTimeUtils.moveDate(now, DateTimeUtils.UTC_TIME_ZONE_ID, 4, DurationUnit.HOURS);
        content = new ReferralsLimitedConditions(expDate).unlockChartBehaviors(content);

        assertEquals(3, content.size());
        assertEquals(ChartBehaviorType.NORMAL, content.get(0).getChartBehaviorType());
        assertFalse(content.get(0).shouldUnlock());
        assertSame(now, content.get(0).getValidFrom());

        assertEquals(ChartBehaviorType.PREVIEW, content.get(1).getChartBehaviorType());
        assertTrue(content.get(1).shouldUnlock());
        assertSame(expDate, content.get(1).getValidFrom());

        assertEquals(ChartBehaviorType.PREVIEW, content.get(2).getChartBehaviorType());
        assertFalse(content.get(2).shouldUnlock());
        assertSame(inAMonth, content.get(2).getValidFrom());
    }

    @Test
    public void testSRV499() throws Exception {
        Date now = new Date();
        ChartBehaviorDto limitedPreviewLocked = new ChartBehaviorDto(now, ChartBehaviorType.PREVIEW);
        limitedPreviewLocked.setLockedAction("");

        List<ChartBehaviorDto> content = new ArrayList<ChartBehaviorDto>(1);
        content.add(limitedPreviewLocked);

        Date expDate = DateTimeUtils.moveDate(now, DateTimeUtils.UTC_TIME_ZONE_ID, 8, DurationUnit.WEEKS);
        content = new ReferralsLimitedConditions(expDate).unlockChartBehaviors(content);

        assertEquals(2, content.size());
        assertEquals(ChartBehaviorType.NORMAL, content.get(0).getChartBehaviorType());
        assertFalse(content.get(0).shouldUnlock());
        assertSame(now, content.get(0).getValidFrom());

        assertEquals(ChartBehaviorType.PREVIEW, content.get(1).getChartBehaviorType());
        assertTrue(content.get(1).shouldUnlock());
        assertSame(expDate, content.get(1).getValidFrom());
    }

    @Test
    public void testSRV504() throws Exception {
        Date now = new Date();
        ChartBehaviorDto freeTrialPreviewLocked = new ChartBehaviorDto(now, ChartBehaviorType.PREVIEW);
        freeTrialPreviewLocked.setLockedAction("");

        Date inAMonth = DateTimeUtils.moveDate(now, DateTimeUtils.UTC_TIME_ZONE_ID, 4, DurationUnit.WEEKS);
        ChartBehaviorDto limitedShuffled = new ChartBehaviorDto(inAMonth, ChartBehaviorType.SHUFFLED);

        List<ChartBehaviorDto> content = new ArrayList<ChartBehaviorDto>(1);
        content.add(freeTrialPreviewLocked);
        content.add(limitedShuffled);

        Date expDate = DateTimeUtils.moveDate(now, DateTimeUtils.UTC_TIME_ZONE_ID, 8, DurationUnit.WEEKS);
        content = new ReferralsLimitedConditions(expDate).unlockChartBehaviors(content);

        assertEquals(2, content.size());
        assertEquals(ChartBehaviorType.NORMAL, content.get(0).getChartBehaviorType());
        assertFalse(content.get(0).shouldUnlock());
        assertSame(now, content.get(0).getValidFrom());

        assertEquals(ChartBehaviorType.SHUFFLED, content.get(1).getChartBehaviorType());
        assertFalse(content.get(1).shouldUnlock());
        assertSame(expDate, content.get(1).getValidFrom());
    }

    @Test
    public void testSRV504_1() throws Exception {
        Date now = new Date();
        ChartBehaviorDto freeTrialNormal = new ChartBehaviorDto(now, ChartBehaviorType.NORMAL);

        Date inAMonth = DateTimeUtils.moveDate(now, DateTimeUtils.UTC_TIME_ZONE_ID, 4, DurationUnit.WEEKS);
        ChartBehaviorDto limitedShuffledLocked = new ChartBehaviorDto(inAMonth, ChartBehaviorType.SHUFFLED);
        limitedShuffledLocked.setLockedAction("");

        List<ChartBehaviorDto> content = new ArrayList<ChartBehaviorDto>(1);
        content.add(freeTrialNormal);
        content.add(limitedShuffledLocked);

        Date expDate = DateTimeUtils.moveDate(now, DateTimeUtils.UTC_TIME_ZONE_ID, 8, DurationUnit.WEEKS);
        content = new ReferralsLimitedConditions(expDate).unlockChartBehaviors(content);

        assertEquals(3, content.size());
        assertEquals(ChartBehaviorType.NORMAL, content.get(0).getChartBehaviorType());
        assertFalse(content.get(0).shouldUnlock());
        assertSame(now, content.get(0).getValidFrom());

        assertEquals(ChartBehaviorType.SHUFFLED, content.get(1).getChartBehaviorType());
        assertFalse(content.get(1).shouldUnlock());
        assertSame(inAMonth, content.get(1).getValidFrom());

        assertEquals(ChartBehaviorType.SHUFFLED, content.get(2).getChartBehaviorType());
        assertTrue(content.get(2).shouldUnlock());
        assertSame(expDate, content.get(2).getValidFrom());
    }

    @Test
    public void testSRV508() throws Exception {
        Date now = new Date();
        ChartBehaviorDto freeTrialNormal = new ChartBehaviorDto(now, ChartBehaviorType.NORMAL);

        Date inAMonth = DateTimeUtils.moveDate(now, DateTimeUtils.UTC_TIME_ZONE_ID, 4, DurationUnit.WEEKS);
        ChartBehaviorDto limitedShuffled = new ChartBehaviorDto(inAMonth, ChartBehaviorType.SHUFFLED);

        List<ChartBehaviorDto> content = new ArrayList<ChartBehaviorDto>(1);
        content.add(freeTrialNormal);
        content.add(limitedShuffled);

        Date expDate = DateTimeUtils.moveDate(now, DateTimeUtils.UTC_TIME_ZONE_ID, 8, DurationUnit.WEEKS);
        content = new ReferralsLimitedConditions(expDate).unlockChartBehaviors(content);

        assertEquals(2, content.size());
        assertEquals(ChartBehaviorType.NORMAL, content.get(0).getChartBehaviorType());
        assertFalse(content.get(0).shouldUnlock());
        assertSame(now, content.get(0).getValidFrom());

        assertEquals(ChartBehaviorType.SHUFFLED, content.get(1).getChartBehaviorType());
        assertFalse(content.get(1).shouldUnlock());
        assertSame(inAMonth, content.get(1).getValidFrom());

    }
}