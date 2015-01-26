package mobi.nowtechnologies.server.persistence.domain.referral;

import mobi.nowtechnologies.common.util.DateTimeUtils;
import mobi.nowtechnologies.server.persistence.domain.Duration;

import javax.persistence.*;
import java.util.Date;

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
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "referrals_duration")),
            @AttributeOverride(name = "unit", column = @Column(name = "referrals_duration_type"))
    })
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

    public void updateMatchesData(int currentReferrals) {
        if (currentReferrals > 0) {
            this.currentReferrals = currentReferrals;

            boolean notMarkedAsMatchedYet = matchedDate == null;
            boolean matchesReferralsCount = currentReferrals >= requiredReferrals;

            if (notMarkedAsMatchedYet && matchesReferralsCount) {
                matchedDate = new Date();
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

    public Date getCalculatedExpireDate() {
        if(isMatched()) {
            if (!isUnlimitedReferralsDuration()) {
                return DateTimeUtils.moveDate(
                        matchedDate, DateTimeUtils.GMT_TIME_ZONE_ID,
                        referralsDuration.getAmount(), referralsDuration.getUnit());
            }
        }

        return null;
    }

    public int getCurrentReferrals() {
        return currentReferrals;
    }

    public boolean isUnlimitedReferralsDuration() {
        return isMatched() && (referralsDuration == null || !referralsDuration.containsPeriod());
    }

    public boolean isMatched() {
        return matchedDate != null;
    }

    Date getMatchedDate() {
        return matchedDate;
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
}
