package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.persistence.domain.payment.Period;
import mobi.nowtechnologies.server.persistence.domain.payment.PromotionPaymentPolicy;
import mobi.nowtechnologies.server.shared.enums.DurationUnit;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "tb_promotions")
public class Promotion implements Serializable {
    public static final String ADD_FREE_WEEKS_PROMOTION = "PromoCode";
    public static final String ADD_SUBBALANCE_PROMOTION = "noPromoCode";
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer i;

    @Column(name = "description", columnDefinition = "char(100)")
    private String description;

    @Column(name = "type", columnDefinition = "char(20)")
    private String type;

    private int endDate;

    @Embedded
    private Period period = new Period(DurationUnit.WEEKS, 0);

    private boolean isActive;

    private int maxUsers;

    private int numUsers;

    private int startDate;

    private byte subWeeks;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userGroup", nullable = false)
    private UserGroup userGroup;

    private boolean showPromotion;

    @Column(length = 50, nullable = true)
    private String label;

    @OneToMany(mappedBy = "promotion", fetch = FetchType.LAZY)
    private List<PromotionPaymentPolicy> promotionPaymentPolicies;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<AbstractFilter> filters;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "promotion")
    private PromoCode promoCode;

    @Column(name = "is_white_listed", columnDefinition = "BIT default false")
    private boolean isWhiteListed;

    @Transient
    private boolean couldBeAppliedMultipleTimes;

    public Promotion() {
    }

    public Integer getI() {
        return this.i;
    }

    public void setI(Integer i) {
        this.i = i;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getEndDate() {
        return this.endDate;
    }

    public void setEndDate(int endDate) {
        this.endDate = endDate;
    }

    public Period getPeriod() {
        return period;
    }

    public boolean getIsActive() {
        return this.isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public int getMaxUsers() {
        return this.maxUsers;
    }

    public void setMaxUsers(int maxUsers) {
        this.maxUsers = maxUsers;
    }

    public int getNumUsers() {
        return this.numUsers;
    }

    public void setNumUsers(int numUsers) {
        this.numUsers = numUsers;
    }

    public int getStartDate() {
        return this.startDate;
    }

    public void setStartDate(int startDate) {
        this.startDate = startDate;
    }

    public byte getSubWeeks() {
        return this.subWeeks;
    }

    public void setSubWeeks(byte subWeeks) {
        this.subWeeks = subWeeks;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getShowPromotion() {
        return showPromotion;
    }

    public void setShowPromotion(boolean showPromotion) {
        this.showPromotion = showPromotion;
    }

    public List<PromotionPaymentPolicy> getPromotionPaymentPolicies() {
        return promotionPaymentPolicies;
    }

    public void setPromotionPaymentPolicies(List<PromotionPaymentPolicy> promotionPaymentPolicies) {
        this.promotionPaymentPolicies = promotionPaymentPolicies;
    }

    public List<AbstractFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<AbstractFilter> filters) {
        this.filters = filters;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getPromoCodeId() {
        if (isNotNull(promoCode)) {
            return promoCode.getId();
        }
        return null;
    }

    public PromoCode getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(PromoCode promoCode) {
        this.promoCode = promoCode;
    }

    public Integer getUserGroupId() {
        if (isNotNull(userGroup)) {
            return userGroup.getId();
        }
        return null;
    }

    public UserGroup getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }

    public boolean isWhiteListed() {
        return isWhiteListed;
    }

    public void setWhiteListed(boolean whiteListed) {
        isWhiteListed = whiteListed;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public Promotion withDescription(String description) {
        setDescription(description);
        return this;
    }

    public Promotion withType(String type) {
        setType(type);
        return this;
    }

    public Promotion withEndDate(int endDate) {
        setEndDate(endDate);
        return this;
    }

    public Promotion withStartDate(int startDate) {
        setStartDate(startDate);
        return this;
    }

    public Promotion withIsActive(boolean isActive) {
        setIsActive(isActive);
        return this;
    }

    public Promotion withMaxUsers(int maxUsers) {
        setMaxUsers(maxUsers);
        return this;
    }

    public Promotion withNumUsers(int numUsers) {
        setNumUsers(numUsers);
        return this;
    }

    public Promotion withSubWeeks(byte subWeeks) {
        setSubWeeks(subWeeks);
        return this;
    }

    public Promotion withUserGroup(UserGroup userGroup) {
        setUserGroup(userGroup);
        return this;
    }

    public Promotion withShowPromotion(boolean showPromotion) {
        setShowPromotion(showPromotion);
        return this;
    }

    public Promotion withLabel(String label) {
        setLabel(label);
        return this;
    }

    public Promotion withPromoCode(PromoCode promoCode) {
        setPromoCode(promoCode);
        return this;
    }

    public Promotion withIsWhiteListed(boolean isWhiteListed) {
        setWhiteListed(isWhiteListed);
        return this;
    }

    public Promotion withCouldBeAppliedMultipleTimes(boolean couldBeAppliedMultipleTimes) {
        this.couldBeAppliedMultipleTimes = couldBeAppliedMultipleTimes;
        return this;
    }

    public int getEndSeconds(int freeTrialStartSeconds) {
        return period == null || period.getDuration() <= 0 ?
               endDate :
               period.toNextSubPaymentSeconds(freeTrialStartSeconds);
    }

    public boolean isCouldBeAppliedMultipleTimes() {
        return couldBeAppliedMultipleTimes || isNotNull(promoCode) && promoCode.isTwoWeeksOnSubscription();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("i", i).append("subWeeks", subWeeks).append("description", description).append("type", type)
                                                                          .append("endDate", endDate).append("period", period).append("isActive", isActive).append("maxUsers", maxUsers)
                                                                          .append("numUsers", numUsers).append("startDate", startDate).append("showPromotion", showPromotion).append("label", label)
                                                                          .append("isWhiteListed", isWhiteListed).append("userGroupId", getUserGroupId()).append("promoCodeId", getPromoCodeId())
                                                                          .append("isCouldBeAppliedMultipleTimes()", isCouldBeAppliedMultipleTimes()).toString();
    }

}