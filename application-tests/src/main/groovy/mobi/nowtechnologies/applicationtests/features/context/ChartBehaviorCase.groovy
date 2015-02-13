package mobi.nowtechnologies.applicationtests.features.context
import mobi.nowtechnologies.server.persistence.domain.Duration
import mobi.nowtechnologies.server.shared.enums.DurationUnit
import org.apache.commons.lang3.time.DateUtils

import java.util.concurrent.TimeUnit

class ChartBehaviorCase {
    String info

    int nowIndex = -1;
    int ftIndex = -1;
    int rmIndex = -1;
    int reIndex = -1;

    long ONE_DAY = TimeUnit.DAYS.toSeconds(1)

    Date getReferralMatchingDate(Date now) {
        DateUtils.addSeconds(now, -ONE_DAY as int)
    }

    Date getReferralExpiresDate(Date now) {
        if(reIndex < 0) {
            // not found in the info: infinity, has not date limitations, has no duration for this referral case
            return null;
        }

        if(reIndex < nowIndex) {
            // in the past
            return DateUtils.addSeconds(now, -ONE_DAY as int)
        } else {
            // in the future
            return DateUtils.addSeconds(now, +ONE_DAY as int)
        }
    }

    boolean hasReferralPeriod() {
        return reIndex > 0;
    }

    Duration getReferralPeriod(Date serverTime, Date freeTrialDate) {
        assert hasReferralPeriod(), 'Has no Referral expires date'

        if(ftIndex > 0) {
            // free trial case

            // matches date always goes first and thus is always in the past: -1 day form now
            Date matchesDate = DateUtils.addDays(serverTime, -2)

            if(reIndex < ftIndex) {
                // expires before free trial
                Date expiresDate = DateUtils.addDays(freeTrialDate, -2)

                return Duration.forPeriod(diffInDays(expiresDate, matchesDate), DurationUnit.DAYS)
            } else {
                // expires after free trial
                Date expiresDate = DateUtils.addDays(freeTrialDate, +2)

                return Duration.forPeriod(diffInDays(expiresDate, matchesDate), DurationUnit.DAYS)
            }

        } else {
            // limited case
            def positions = [nowIndex, ftIndex, rmIndex, reIndex];
            Collections.sort(positions)
            int periodsBetween = positions.indexOf(reIndex) - positions.indexOf(rmIndex)
            return Duration.forPeriod(periodsBetween, DurationUnit.DAYS)
        }

    }

    @Override
    public String toString() {
        return info;
    }

    public int diffInDays(Date hi, Date lo) {
        def hiSec = TimeUnit.MILLISECONDS.toSeconds(hi.getTime());
        def loSec = TimeUnit.MILLISECONDS.toSeconds(lo.getTime());
        def diff = hiSec - loSec
        return TimeUnit.SECONDS.toDays(diff)
    }
}
