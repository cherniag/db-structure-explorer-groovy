package mobi.nowtechnologies.server.persistence.domain.referral;

import mobi.nowtechnologies.server.persistence.domain.Duration;
import mobi.nowtechnologies.server.shared.enums.DurationUnit;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

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
        userReferralsSnapshot.updateMatchesData(lessCount);
        assertFalse(userReferralsSnapshot.isMatched());
        assertEquals(lessCount, userReferralsSnapshot.getCurrentReferrals());

        userReferralsSnapshot.updateMatchesData(positiveRequiredCount);
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
        userReferralsSnapshot.updateMatchesData(biggerCount);
        assertEquals(biggerCount, userReferralsSnapshot.getCurrentReferrals());

        Date matchedDateBefore = userReferralsSnapshot.getMatchedDate();

        userReferralsSnapshot.updateMatchesData(againBiggerCount);
        assertEquals(againBiggerCount, userReferralsSnapshot.getCurrentReferrals());
        Date matchedDateAfter = userReferralsSnapshot.getMatchedDate();

        assertEquals(matchedDateBefore, matchedDateAfter);
    }
}