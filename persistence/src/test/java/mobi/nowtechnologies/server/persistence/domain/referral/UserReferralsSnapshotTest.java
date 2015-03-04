package mobi.nowtechnologies.server.persistence.domain.referral;

import mobi.nowtechnologies.server.persistence.domain.Duration;
import mobi.nowtechnologies.server.shared.enums.DurationUnit;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import org.junit.*;
import static org.junit.Assert.*;

public class UserReferralsSnapshotTest {

    @Test
    public void testConstructorNoPeriod() throws Exception {
        // given
        final int positiveRequiredCount = 1;
        final Duration noPeriod = Duration.noPeriod();
        final int userId = 1;

        // when
        UserReferralsSnapshot userReferralsSnapshot = new UserReferralsSnapshot(userId, positiveRequiredCount, noPeriod);

        // then
        assertSame(noPeriod, userReferralsSnapshot.getReferralsDuration());
        assertEquals(positiveRequiredCount, userReferralsSnapshot.getRequiredReferrals());
        assertEquals(userId, userReferralsSnapshot.getUserId());
    }

    @Test
    public void testConstructorWithPeriod() throws Exception {
        // given
        final int positiveRequiredCount = 1;
        final Duration withPeriod = Duration.forPeriod(11, DurationUnit.DAYS);
        final int userId = 4;

        // when
        UserReferralsSnapshot userReferralsSnapshot = new UserReferralsSnapshot(userId, positiveRequiredCount, withPeriod);

        // then
        assertSame(withPeriod, userReferralsSnapshot.getReferralsDuration());
        assertEquals(positiveRequiredCount, userReferralsSnapshot.getRequiredReferrals());
        assertEquals(userId, userReferralsSnapshot.getUserId());
    }

    @Test
    public void testUpdateMatchesDataWithDiffCounts() throws Exception {
        // given
        final int positiveRequiredCount = 10;
        final int lessCount = positiveRequiredCount - 1;
        final int userId = 1;

        // when-then
        UserReferralsSnapshot userReferralsSnapshot = new UserReferralsSnapshot(userId, positiveRequiredCount, Duration.noPeriod());
        userReferralsSnapshot.updateMatchesData(lessCount, new Date());
        assertFalse(userReferralsSnapshot.isMatched());
        assertEquals(lessCount, userReferralsSnapshot.getCurrentReferrals());

        userReferralsSnapshot.updateMatchesData(positiveRequiredCount, new Date());
        assertTrue(userReferralsSnapshot.isMatched());
        assertEquals(positiveRequiredCount, userReferralsSnapshot.getCurrentReferrals());
    }

    @Test
    public void testUpdateMatchesDataWithDiffCountManyTimes() throws Exception {
        // given
        final int positiveRequiredCount = 10;
        final int biggerCount = positiveRequiredCount + 1;
        final int againBiggerCount = biggerCount + 2;
        final int userId = 1;

        // when
        UserReferralsSnapshot userReferralsSnapshot = new UserReferralsSnapshot(userId, positiveRequiredCount, Duration.noPeriod());
        userReferralsSnapshot.updateMatchesData(biggerCount, new Date());
        assertEquals(biggerCount, userReferralsSnapshot.getCurrentReferrals());

        Date matchedDateBefore = userReferralsSnapshot.getMatchedDate();

        userReferralsSnapshot.updateMatchesData(againBiggerCount, new Date());
        assertEquals(againBiggerCount, userReferralsSnapshot.getCurrentReferrals());
        Date matchedDateAfter = userReferralsSnapshot.getMatchedDate();

        assertEquals(matchedDateBefore, matchedDateAfter);
    }

    @Test
    public void testIncludesWhenWithDuration() throws Exception {
        // given
        final int userId = 1;
        final int positiveRequiredCount = 10;
        final int biggerCount = positiveRequiredCount + 1;
        final Duration oneDayPeriod = Duration.forPeriod(1, DurationUnit.DAYS);

        // when
        UserReferralsSnapshot userReferralsSnapshot = new UserReferralsSnapshot(userId, positiveRequiredCount, oneDayPeriod);
        userReferralsSnapshot.updateMatchesData(biggerCount, new Date());

        final Date now = new Date();
        final Date plusHalfDay = DateUtils.addHours(now, 12);
        final Date plusOneDay = DateUtils.addHours(now, 24);
        final Date plusOneDayAndOneMillisecond = DateUtils.addMilliseconds(plusOneDay, 1);
        final Date infinity = null;

        assertTrue(userReferralsSnapshot.includes(plusHalfDay, plusOneDay));
        assertFalse(userReferralsSnapshot.includes(plusHalfDay, plusOneDayAndOneMillisecond));
        assertFalse(userReferralsSnapshot.includes(plusHalfDay, infinity));
    }

    @Test
    public void testIncludesWhenWithNoDuration() throws Exception {
        // given
        final int userId = 1;
        final int positiveRequiredCount = 10;
        final int biggerCount = positiveRequiredCount + 1;
        final Duration noPeriod = Duration.noPeriod();

        // when
        UserReferralsSnapshot userReferralsSnapshot = new UserReferralsSnapshot(userId, positiveRequiredCount, noPeriod);
        userReferralsSnapshot.updateMatchesData(biggerCount, new Date());

        final Date now = new Date();
        final Date plusHalfDay = DateUtils.addHours(now, 12);
        final Date plusOneDay = DateUtils.addHours(now, 24);
        final Date plusOneDayAndOneMillisecond = DateUtils.addMilliseconds(plusOneDay, 1);
        final Date infinity = null;

        assertTrue(userReferralsSnapshot.includes(plusHalfDay, plusOneDay));
        assertTrue(userReferralsSnapshot.includes(plusHalfDay, plusOneDayAndOneMillisecond));
        assertTrue(userReferralsSnapshot.includes(plusHalfDay, infinity));
    }

    @Test
    public void testActualWithNoDuration() throws Exception {
        // given
        final int userId = 1;
        final int positiveRequiredCount = 10;
        final int biggerCount = positiveRequiredCount + 1;
        final Duration oneDayPeriod = Duration.noPeriod();

        // when
        UserReferralsSnapshot userReferralsSnapshot = new UserReferralsSnapshot(userId, positiveRequiredCount, oneDayPeriod);
        userReferralsSnapshot.updateMatchesData(biggerCount, new Date());

        final Date now = new Date();
        final Date plusHalfDay = DateUtils.addHours(now, 12);

        assertTrue(userReferralsSnapshot.isActual(plusHalfDay));
    }

    @Test
    public void testActualWithDuration() throws Exception {
        // given
        final int userId = 1;
        final int positiveRequiredCount = 10;
        final int biggerCount = positiveRequiredCount + 1;
        final Duration oneDayPeriod = Duration.forPeriod(1, DurationUnit.DAYS);

        // when
        UserReferralsSnapshot userReferralsSnapshot = new UserReferralsSnapshot(userId, positiveRequiredCount, oneDayPeriod);
        userReferralsSnapshot.updateMatchesData(biggerCount, new Date());

        final Date now = new Date();
        final Date plusHalfDay = DateUtils.addHours(now, 12);
        final Date plusOneDay = DateUtils.addHours(now, 24);
        final Date plusOneDayAndOneMillisecond = DateUtils.addMilliseconds(plusOneDay, 1);

        assertTrue(userReferralsSnapshot.isActual(plusHalfDay));
        assertFalse(userReferralsSnapshot.isActual(plusOneDayAndOneMillisecond));
    }
}