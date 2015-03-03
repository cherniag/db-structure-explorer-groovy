package mobi.nowtechnologies.server.persistence.domain.behavior;

import mobi.nowtechnologies.server.persistence.domain.Duration;
import mobi.nowtechnologies.server.persistence.domain.UserStatusType;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.Cascade;

import org.springframework.util.Assert;

/**
 * Created by zam on 12/9/2014.
 */
@Entity
@Table(name = "behavior_config")
public class BehaviorConfig implements Serializable {

    public static final int IGNORE = -1;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "behaviorConfig")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    Set<ChartBehavior> chartBehaviors = new HashSet<ChartBehavior>();
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "behaviorConfig")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    Set<ContentUserStatusBehavior> contentUserStatusBehaviors = new HashSet<ContentUserStatusBehavior>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "community_id")
    private int communityId;
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private BehaviorConfigType type;
    @Column(name = "required_referrals")
    private int requiredReferrals = IGNORE;
    @Embedded
    @AttributeOverrides({@AttributeOverride(name = "amount", column = @Column(name = "referrals_duration")), @AttributeOverride(name = "unit", column = @Column(name = "referrals_duration_type"))})
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    private Duration referralsDuration = Duration.noPeriod();

    protected BehaviorConfig() {
    }

    public void updateReferralsInfo(int requiredAmount, Duration duration) {
        requiredReferrals = (requiredAmount < 0) ?
                            IGNORE :
                            requiredAmount;
        referralsDuration = duration;
    }

    public int getCommunityId() {
        return communityId;
    }

    public int getRequiredReferrals() {
        return requiredReferrals;
    }

    public Duration getReferralsDuration() {
        return referralsDuration;
    }

    public ChartBehavior getChartBehavior(ChartBehaviorType chartBehaviorType) {
        Assert.notNull(chartBehaviorType);

        for (ChartBehavior chartBehavior : chartBehaviors) {
            if (chartBehavior.getType() == chartBehaviorType) {
                return chartBehavior;
            }
        }

        throw new IllegalStateException("No values for " + chartBehaviorType + " in " + id);
    }

    public ContentUserStatusBehavior getContentUserStatusBehavior(UserStatusType userStatusType) {
        Assert.notNull(userStatusType);

        for (ContentUserStatusBehavior contentUserStatusBehavior : contentUserStatusBehaviors) {
            if (contentUserStatusBehavior.getUserStatusType() == userStatusType) {
                return contentUserStatusBehavior;
            }
        }

        throw new IllegalStateException("No values for " + userStatusType + " in " + id);
    }

    public BehaviorConfigType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "BehaviorConfig{" +
               "requiredReferrals=" + requiredReferrals +
               ", referralsDuration=" + referralsDuration +
               ", type=" + type +
               '}';
    }
}
