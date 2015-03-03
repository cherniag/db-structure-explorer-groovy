package mobi.nowtechnologies.server.persistence.domain.referral;

import mobi.nowtechnologies.server.shared.enums.ProviderType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.springframework.util.Assert;

/**
 * Author: Gennadii Cherniaiev Date: 11/21/2014
 */
@Entity
@Table(name = "user_referrals",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "contact"}))
public class Referral {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "community_id")
    private int communityId;

    @Column(name = "contact", nullable = false)
    private String contact;

    @Column(name = "provider_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProviderType providerType;

    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReferralState state = ReferralState.PENDING;

    @Column(name = "create_timestamp")
    private long createTimestamp = new Date().getTime();

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCommunityId() {
        return communityId;
    }

    public void setCommunityId(int communityId) {
        this.communityId = communityId;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public ProviderType getProviderType() {
        return providerType;
    }

    public void setProviderType(ProviderType providerType) {
        this.providerType = providerType;
    }

    public ReferralState getState() {
        return state;
    }

    public void setState(ReferralState state) {
        Assert.notNull(state);
        Assert.isTrue(this.state.hasNext(state));

        this.state = state;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("id", id).append("userId", userId).append("communityId", communityId).append("contact", contact)
                                                                          .append("providerType", providerType).append("state", state).append("createTimestamp", createTimestamp).toString();
    }
}
