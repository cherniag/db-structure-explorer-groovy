package mobi.nowtechnologies.server.persistence.domain.referral;

import mobi.nowtechnologies.common.util.DateTimeUtils;
import mobi.nowtechnologies.server.persistence.domain.Duration;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.util.Date;

import com.google.common.base.Preconditions;

import org.springframework.util.Assert;

/**
 * Created by zam on 12/17/2014.
 */
@Entity
@Table(name = "user_referrals_snapshot")
public class UserReferralsSnapshot {

    @Id
    @Column(name = "user_id")
    private int userId;

    @Column(name = "required_referrals")
    private int requiredReferrals;

    @Column(name = "current_referrals")
    private int currentReferrals;

    @Embedded
    @AttributeOverrides({@AttributeOverride(name = "amount", column = @Column(name = "referrals_duration")), @AttributeOverride(name = "unit", column = @Column(name = "referrals_duration_type"))})
    private Duration referralsDuration;

    @Column(name = "matched_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date matchedDate;

    protected UserReferralsSnapshot() {
    }

    public UserReferralsSnapshot(int userId, int required, Duration duration) {
        this.userId = userId;
        this.requiredReferrals = required;
        this.referralsDuration = duration;
    }

    public void updateMatchesData(int currentReferrals, Date serverTime) {
        if (currentReferrals > 0) {
            this.currentReferrals = currentReferrals;

            boolean notMarkedAsMatchedYet = matchedDate == null;
            boolean matchesReferralsCount = currentReferrals >= requiredReferrals;

            if (notMarkedAsMatchedYet && matchesReferralsCount) {
                matchedDate = serverTime;
            }
        }
    }

    public int getRequiredReferrals() {
        return requiredReferrals;
    }

    public Duration getReferralsDuration() {
        return referralsDuration;
    }

    public int getUserId() {
        return userId;
    }

    public int getCurrentReferrals() {
        return currentReferrals;
    }

    public boolean hasNoDuration() {
        return referralsDuration == null || !referralsDuration.containsPeriod();
    }

    public boolean isMatched() {
        return matchedDate != null;
    }

    public boolean isActual(Date time) {
        Assert.notNull(time);

        return isMatched() && (hasNoDuration() || getReferralsExpiresDate().after(time));
    }

    public Date getMatchedDate() {
        if (matchedDate == null) {
            return null;
        } else {
            return new Date(matchedDate.getTime());
        }
    }

    @Override
    public String toString() {
        return "UserReferralsSnapshot{" +
               "userId=" + userId +
               ", matchedDate=" + matchedDate +
               ", referralsDuration=" + referralsDuration +
               ", requiredReferrals=" + requiredReferrals +
               '}';
    }

    public Date getReferralsExpiresDate() {
        Preconditions.checkState(isMatched(), "Referrals was not matched. Check is it before");
        Preconditions.checkState(!hasNoDuration(), "Referrals has no duration. Check is it before");

        return DateTimeUtils.moveDate(getMatchedDate(), DateTimeUtils.GMT_TIME_ZONE_ID, referralsDuration.getAmount(), referralsDuration.getUnit());
    }

    public boolean includes(Date start, Date end) {
        Assert.notNull(start);
        Preconditions.checkState(isMatched(), "Check if it is already matched");

        if (getMatchedDate().getTime() <= start.getTime()) {
            if (end == null) {
                return hasNoDuration();
            } else {
                return hasNoDuration() || getReferralsExpiresDate().getTime() >= end.getTime();
            }
        }

        return false;
    }
}
