package mobi.nowtechnologies.server.service.behavior;

import mobi.nowtechnologies.server.persistence.domain.Duration;
import mobi.nowtechnologies.server.persistence.domain.UserStatusType;
import mobi.nowtechnologies.server.persistence.domain.behavior.ChartBehaviorType;
import mobi.nowtechnologies.server.persistence.domain.referral.UserReferralsSnapshot;
import mobi.nowtechnologies.server.shared.enums.DurationUnit;

import java.util.Date;
import java.util.TreeSet;

import com.google.common.collect.Iterables;
import org.apache.commons.lang.time.DateUtils;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Rm - date when Referral Matched Re - date when Referral Expires FT - free trial expires Lim- limited S  - subscribed
 */
public class ChartBehaviorReferralsServiceTest {

    static ChartBehaviorType ADMIN_CHART_TYPE_1 = ChartBehaviorType.PREVIEW;
    static ChartBehaviorType ADMIN_CHART_TYPE_2 = ChartBehaviorType.PREVIEW;

    ChartBehaviorReferralsService chartBehaviorReferralsService = new ChartBehaviorReferralsService();

    @Before
    public void setUp() throws Exception {
        chartBehaviorReferralsService.chartBehaviorReferralsRulesService = new ChartBehaviorReferralsRulesService();
    }

    /**
     * FT -----*--------[]-------|---*---------------------------> Rm       NOW          Re
     */
    @Test
    public void testReferralWithPeriodIncludesFreeTrialExpiresDateAndInLimitedThereIsALock() throws Exception {
        // given
        final int requiredCount = 10;
        final int biggerCount = requiredCount + 1;
        final Duration twoWeeksPeriod = Duration.forPeriod(2, DurationUnit.WEEKS);
        final Date serverTime = new Date();
        final Date inThePast = DateUtils.addDays(serverTime, -1);
        final Date inTheFuture = DateUtils.addDays(serverTime, +1);


        // when
        UserReferralsSnapshot userReferralsSnapshot = new UserReferralsSnapshot(1, requiredCount, twoWeeksPeriod);
        userReferralsSnapshot.updateMatchesData(biggerCount, inThePast);

        TreeSet<ChartBehaviorInfo> infos = freeTrialCase(serverTime, inTheFuture, ADMIN_CHART_TYPE_1, ADMIN_CHART_TYPE_2, true, true);
        chartBehaviorReferralsService.apply(infos, userReferralsSnapshot, serverTime);

        // then
        assertEquals(3, infos.size());

        // check chart types
        assertEquals(ChartBehaviorType.NORMAL, Iterables.get(infos, 0).chartBehaviorType);
        assertEquals(ChartBehaviorType.SHUFFLED, Iterables.get(infos, 1).chartBehaviorType);
        assertEquals(ADMIN_CHART_TYPE_2, Iterables.get(infos, 2).chartBehaviorType);

        // check dates
        assertEquals(serverTime, Iterables.get(infos, 0).validFrom);
        assertEquals(inTheFuture, Iterables.get(infos, 1).validFrom);
        assertEquals(userReferralsSnapshot.getReferralsExpiresDate(), Iterables.get(infos, 2).validFrom);

        // check locks
        assertNull(Iterables.get(infos, 0).lockedAction);
        assertNull(Iterables.get(infos, 1).lockedAction);
        assertNotNull(Iterables.get(infos, 2).lockedAction);
    }

    /**
     * FT -----*--------[]-------|---*---------------------------> Rm       NOW          Re
     */
    @Test
    public void testReferralWithPeriodIncludesFreeTrialExpiresDateAndInLimitedThereIsALock1() throws Exception {
        // given
        final int requiredCount = 10;
        final int biggerCount = requiredCount + 1;
        final Duration twoWeeksPeriod = Duration.forPeriod(2, DurationUnit.WEEKS);
        final Date serverTime = new Date();
        final Date inThePast = DateUtils.addDays(serverTime, -1);
        final Date inTheFuture = DateUtils.addDays(serverTime, +1);


        // when
        UserReferralsSnapshot userReferralsSnapshot = new UserReferralsSnapshot(1, requiredCount, twoWeeksPeriod);
        userReferralsSnapshot.updateMatchesData(biggerCount, inThePast);

        TreeSet<ChartBehaviorInfo> infos = freeTrialCase(serverTime, inTheFuture, ADMIN_CHART_TYPE_1, ADMIN_CHART_TYPE_2, true, false);
        chartBehaviorReferralsService.apply(infos, userReferralsSnapshot, serverTime);

        // then
        assertEquals(3, infos.size());

        // check chart types
        assertEquals(ChartBehaviorType.NORMAL, Iterables.get(infos, 0).chartBehaviorType);
        assertEquals(ChartBehaviorType.SHUFFLED, Iterables.get(infos, 1).chartBehaviorType);
        assertEquals(ADMIN_CHART_TYPE_2, Iterables.get(infos, 2).chartBehaviorType);

        // check dates
        assertEquals(serverTime, Iterables.get(infos, 0).validFrom);
        assertEquals(inTheFuture, Iterables.get(infos, 1).validFrom);
        assertEquals(userReferralsSnapshot.getReferralsExpiresDate(), Iterables.get(infos, 2).validFrom);

        // check locks
        assertNull(Iterables.get(infos, 0).lockedAction);
        assertNull(Iterables.get(infos, 1).lockedAction);
        assertNull(Iterables.get(infos, 2).lockedAction);
    }


    /**
     * FT -----*--------[]-------|-------------------------------> Rm       NOW
     */
    @Test
    public void testReferralWithNoPeriodIncludesFreeTrialExpiresDate() throws Exception {
        // given
        final int requiredCount = 10;
        final int biggerCount = requiredCount + 1;
        final Duration noPeriod = Duration.noPeriod();
        final Date serverTime = new Date();
        final Date inThePast = DateUtils.addDays(serverTime, -1);
        final Date inTheFuture = DateUtils.addDays(serverTime, +1);


        // when
        UserReferralsSnapshot userReferralsSnapshot = new UserReferralsSnapshot(1, requiredCount, noPeriod);
        userReferralsSnapshot.updateMatchesData(biggerCount, inThePast);

        TreeSet<ChartBehaviorInfo> infos = freeTrialCase(serverTime, inTheFuture, ADMIN_CHART_TYPE_1, ADMIN_CHART_TYPE_2, true, true);
        chartBehaviorReferralsService.apply(infos, userReferralsSnapshot, serverTime);

        // then
        assertEquals(2, infos.size());

        // check chart types
        assertEquals(ChartBehaviorType.NORMAL, Iterables.get(infos, 0).chartBehaviorType);
        assertEquals(ChartBehaviorType.SHUFFLED, Iterables.get(infos, 1).chartBehaviorType);

        // check dates
        assertEquals(serverTime, Iterables.get(infos, 0).validFrom);
        assertEquals(inTheFuture, Iterables.get(infos, 1).validFrom);

        // check locks
        assertNull(Iterables.get(infos, 0).lockedAction);
        assertNull(Iterables.get(infos, 1).lockedAction);
    }

    /**
     * FT -----*---[]---*-----|-------------------------------> Rm  NOW  Re
     */
    @Test
    public void testReferralWithPeriodDoesNotIncludeFreeTrialExpiresDate() throws Exception {
        // given
        final int requiredCount = 10;
        final int biggerCount = requiredCount + 1;
        final Duration twoDaysPeriod = Duration.forPeriod(2, DurationUnit.DAYS);
        final Date serverTime = new Date();
        final Date inThePast = DateUtils.addDays(serverTime, -1);
        final Date inTheFarFuture = DateUtils.addDays(serverTime, +10);

        // when
        UserReferralsSnapshot userReferralsSnapshot = new UserReferralsSnapshot(1, requiredCount, twoDaysPeriod);
        userReferralsSnapshot.updateMatchesData(biggerCount, inThePast);

        TreeSet<ChartBehaviorInfo> infos = freeTrialCase(serverTime, inTheFarFuture, ADMIN_CHART_TYPE_1, ADMIN_CHART_TYPE_2, true, true);
        chartBehaviorReferralsService.apply(infos, userReferralsSnapshot, serverTime);

        // then
        assertEquals(3, infos.size());

        // check chart types
        assertEquals(ChartBehaviorType.NORMAL, Iterables.get(infos, 0).chartBehaviorType);
        assertEquals(ADMIN_CHART_TYPE_1, Iterables.get(infos, 1).chartBehaviorType);
        assertEquals(ADMIN_CHART_TYPE_2, Iterables.get(infos, 2).chartBehaviorType);

        // check dates
        assertEquals(serverTime, Iterables.get(infos, 0).validFrom);
        assertEquals(userReferralsSnapshot.getReferralsExpiresDate(), Iterables.get(infos, 1).validFrom);
        assertEquals(inTheFarFuture, Iterables.get(infos, 2).validFrom);

        // check locks
        assertNull(Iterables.get(infos, 0).lockedAction);
        assertNotNull(Iterables.get(infos, 1).lockedAction);
        assertNotNull(Iterables.get(infos, 2).lockedAction);
    }

    /**
     * FT -----*--------[]-------|-------------------------------> Rm       NOW
     */
    @Test
    public void testReferralWithNoPeriodIncludesFreeTrialExpiresDateDifferentLocks() throws Exception {
        // given
        final int requiredCount = 10;
        final int biggerCount = requiredCount + 1;
        final Duration noPeriod = Duration.noPeriod();
        final Date serverTime = new Date();
        final Date inThePast = DateUtils.addDays(serverTime, -1);
        final Date inTheFuture = DateUtils.addDays(serverTime, +1);

        // when
        UserReferralsSnapshot userReferralsSnapshot = new UserReferralsSnapshot(1, requiredCount, noPeriod);
        userReferralsSnapshot.updateMatchesData(biggerCount, inThePast);

        TreeSet<ChartBehaviorInfo> infos = freeTrialCase(serverTime, inTheFuture, ADMIN_CHART_TYPE_1, ADMIN_CHART_TYPE_2, false, true);
        chartBehaviorReferralsService.apply(infos, userReferralsSnapshot, serverTime);

        // then
        assertEquals(2, infos.size());

        // check chart types
        assertEquals(ADMIN_CHART_TYPE_1, Iterables.get(infos, 0).chartBehaviorType);
        assertEquals(ChartBehaviorType.SHUFFLED, Iterables.get(infos, 1).chartBehaviorType);

        // check dates
        assertEquals(serverTime, Iterables.get(infos, 0).validFrom);
        assertEquals(inTheFuture, Iterables.get(infos, 1).validFrom);

        // check locks
        assertNull(Iterables.get(infos, 0).lockedAction);
        assertNull(Iterables.get(infos, 1).lockedAction);
    }

    /**
     * Lim ---|--*--------[]--------------------------------------> Rm       NOW
     */
    @Test
    public void testReferralWithNoPeriodIncludesLimited() throws Exception {
        // given
        final int requiredCount = 10;
        final int biggerCount = requiredCount + 1;
        final Duration noPeriod = Duration.noPeriod();
        final Date serverTime = new Date();
        final Date inThePast = DateUtils.addDays(serverTime, -1);

        // when
        UserReferralsSnapshot userReferralsSnapshot = new UserReferralsSnapshot(1, requiredCount, noPeriod);
        userReferralsSnapshot.updateMatchesData(biggerCount, inThePast);

        TreeSet<ChartBehaviorInfo> infos = limitedCase(serverTime, ADMIN_CHART_TYPE_1, true);
        chartBehaviorReferralsService.apply(infos, userReferralsSnapshot, serverTime);

        // then
        assertEquals(1, infos.size());

        // check chart types
        assertEquals(ChartBehaviorType.SHUFFLED, Iterables.get(infos, 0).chartBehaviorType);

        // check dates
        assertEquals(serverTime, Iterables.get(infos, 0).validFrom);

        // check locks
        assertNull(Iterables.get(infos, 0).lockedAction);
    }

    /**
     * Lim ---|--*--------[]-----*--------------------------------> Rm       NOW    Re
     */
    @Test
    public void testReferralWithPeriodIncludesLimited() throws Exception {
        // given
        final int requiredCount = 10;
        final int biggerCount = requiredCount + 1;
        final Duration twoWeeksPeriod = Duration.forPeriod(2, DurationUnit.WEEKS);
        final Date serverTime = new Date();
        final Date inThePast = DateUtils.addDays(serverTime, -1);

        // when
        UserReferralsSnapshot userReferralsSnapshot = new UserReferralsSnapshot(1, requiredCount, twoWeeksPeriod);
        userReferralsSnapshot.updateMatchesData(biggerCount, inThePast);

        TreeSet<ChartBehaviorInfo> infos = limitedCase(serverTime, ADMIN_CHART_TYPE_1, true);
        chartBehaviorReferralsService.apply(infos, userReferralsSnapshot, serverTime);

        // then
        assertEquals(2, infos.size());

        // check chart types
        assertEquals(ChartBehaviorType.SHUFFLED, Iterables.get(infos, 0).chartBehaviorType);
        assertEquals(ADMIN_CHART_TYPE_1, Iterables.get(infos, 1).chartBehaviorType);

        // check dates
        assertEquals(serverTime, Iterables.get(infos, 0).validFrom);
        assertEquals(userReferralsSnapshot.getReferralsExpiresDate(), Iterables.get(infos, 1).validFrom);

        // check locks
        assertNull(Iterables.get(infos, 0).lockedAction);
        assertNotNull(Iterables.get(infos, 1).lockedAction);
    }

    /**
     * |        S        |           Lim ---|---*-------[]----|--*--------------------------------> Rm     NOW       Re
     */
    @Test
    public void testReferralWithPeriodIncludesLimitedAfterSubscribed() throws Exception {
        // given
        final int requiredCount = 10;
        final int biggerCount = requiredCount + 1;
        final Duration twoWeeksPeriod = Duration.forPeriod(2, DurationUnit.WEEKS);
        final Date serverTime = new Date();
        final Date inThePast = DateUtils.addDays(serverTime, -1);
        final Date inTheFuture = DateUtils.addDays(serverTime, +1);

        // when
        UserReferralsSnapshot userReferralsSnapshot = new UserReferralsSnapshot(1, requiredCount, twoWeeksPeriod);
        userReferralsSnapshot.updateMatchesData(biggerCount, inThePast);

        TreeSet<ChartBehaviorInfo> infos = subscribedCase(serverTime, inTheFuture, ADMIN_CHART_TYPE_1, ADMIN_CHART_TYPE_2, true);
        chartBehaviorReferralsService.apply(infos, userReferralsSnapshot, serverTime);

        // then
        assertEquals(3, infos.size());

        // check chart types
        assertEquals(ADMIN_CHART_TYPE_1, Iterables.get(infos, 0).chartBehaviorType);
        assertEquals(ChartBehaviorType.SHUFFLED, Iterables.get(infos, 1).chartBehaviorType);
        assertEquals(ADMIN_CHART_TYPE_2, Iterables.get(infos, 2).chartBehaviorType);

        // check dates
        assertEquals(serverTime, Iterables.get(infos, 0).validFrom);
        assertEquals(inTheFuture, Iterables.get(infos, 1).validFrom);
        assertEquals(userReferralsSnapshot.getReferralsExpiresDate(), Iterables.get(infos, 2).validFrom);

        // check locks
        assertNull(Iterables.get(infos, 0).lockedAction);
        assertNull(Iterables.get(infos, 1).lockedAction);
        assertNotNull(Iterables.get(infos, 2).lockedAction);
    }

    private TreeSet<ChartBehaviorInfo> subscribedCase(Date subscribedTime, Date limitedTime, ChartBehaviorType subscribedType, ChartBehaviorType limitedType, boolean lockedLimited) {
        TreeSet<ChartBehaviorInfo> infos = new TreeSet<>();
        infos.add(createChartBehaviorInfo(subscribedTime, UserStatusType.SUBSCRIBED, subscribedType, false));
        infos.add(createChartBehaviorInfo(limitedTime, UserStatusType.LIMITED, limitedType, lockedLimited));
        return infos;
    }

    private TreeSet<ChartBehaviorInfo> limitedCase(Date validFrom, ChartBehaviorType type, boolean locked) {
        TreeSet<ChartBehaviorInfo> infos = new TreeSet<>();
        infos.add(createChartBehaviorInfo(validFrom, UserStatusType.LIMITED, type, locked));
        return infos;
    }

    private TreeSet<ChartBehaviorInfo> freeTrialCase(Date validFrom1, Date validFrom2, ChartBehaviorType freeTrial, ChartBehaviorType limited, boolean lock1, boolean lock2) {
        TreeSet<ChartBehaviorInfo> infos = new TreeSet<>();
        infos.add(createChartBehaviorInfo(validFrom1, UserStatusType.FREE_TRIAL, freeTrial, lock1));
        infos.add(createChartBehaviorInfo(validFrom2, UserStatusType.LIMITED, limited, lock2));
        return infos;
    }

    private ChartBehaviorInfo createChartBehaviorInfo(Date validFrom, UserStatusType status, ChartBehaviorType type, boolean locked) {
        ChartBehaviorInfo info = new ChartBehaviorInfo();
        info.userStatusType = status;
        info.validFrom = validFrom;
        info.chartBehaviorType = type;
        info.lockedAction = (locked) ?
                            "lock action" :
                            null;
        info.canBeUnlocked = locked;
        return info;
    }

}