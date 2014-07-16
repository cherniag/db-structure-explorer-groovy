package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.persistence.domain.payment.PromotionPaymentPolicy;
import mobi.nowtechnologies.server.shared.Utils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;
import static mobi.nowtechnologies.server.shared.Utils.WEEK_SECONDS;

@Entity
@Table(name = "tb_promotions")
@NamedQueries( { @NamedQuery(name = Promotion.NQ_GET_PROMOTION_WITH_FILTER, query = "select distinct prom from Promotion prom join prom.filters where isActive=true and (numUsers<maxUsers or maxUsers=0) and endDate>?1 and startDate<?1 and userGroup=?2") })
public class Promotion implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String NQ_GET_PROMOTION_WITH_FILTER = "getPromotionWithFilter";

	public static final String ADD_FREE_WEEKS_PROMOTION = "promoCode";
	public static final String ADD_SUBBALANCE_PROMOTION = "noPromoCode";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer i;

	@Column(name = "description", columnDefinition = "char(100)")
	private String description;

	@Column(name = "type", columnDefinition = "char(20)")
	private String type;

	private int endDate;

	private byte freeWeeks;

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

	@OneToOne(fetch = FetchType.EAGER, mappedBy="promotion")
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

	public byte getFreeWeeks() {
		return this.freeWeeks;
	}

	public void setFreeWeeks(byte freeWeeks) {
		this.freeWeeks = freeWeeks;
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
        if (isNotNull(promoCode)){
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
        if(isNotNull(userGroup)){
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

    public Promotion withDescription(String description){
        setDescription(description);
        return this;
    }

    public Promotion withType(String type){
        setType(type);
        return this;
    }

    public Promotion withEndDate(int endDate){
        setEndDate(endDate);
        return this;
    }

    public Promotion withStartDate(int startDate){
        setStartDate(startDate);
        return this;
    }

    public Promotion withFreeWeeks(byte freeWeeks){
        setFreeWeeks(freeWeeks);
        return this;
    }

    public Promotion withIsActive(boolean isActive){
        setIsActive(isActive);
        return this;
    }

    public Promotion withMaxUsers(int maxUsers){
        setMaxUsers(maxUsers);
        return this;
    }

    public Promotion withNumUsers(int numUsers){
        setNumUsers(numUsers);
        return this;
    }

    public Promotion withSubWeeks(byte subWeeks){
        setSubWeeks(subWeeks);
        return this;
    }

    public Promotion withUserGroup(UserGroup userGroup){
        setUserGroup(userGroup);
        return this;
    }

    public Promotion withShowPromotion(boolean showPromotion){
        setShowPromotion(showPromotion);
        return this;
    }

    public Promotion withLabel(String label){
        setLabel(label);
        return this;
    }

    public Promotion withPromoCode(PromoCode promoCode){
        setPromoCode(promoCode);
        return this;
    }

    public Promotion withIsWhiteListed(boolean isWhiteListed){
        setWhiteListed(isWhiteListed);
        return this;
    }

    public Promotion withCouldBeAppliedMultipleTimes(boolean couldBeAppliedMultipleTimes){
        this.couldBeAppliedMultipleTimes = couldBeAppliedMultipleTimes;
        return this;
    }

    public int getFreeWeeks(int freeTrialStartedTimestampSeconds){
        return freeWeeks == 0 ? (endDate - freeTrialStartedTimestampSeconds) / WEEK_SECONDS : freeWeeks;
    }

    public int getFreeWeeksEndDate(int freeTrialStartedTimestampSeconds){
        return freeWeeks == 0 ? endDate:  freeTrialStartedTimestampSeconds + freeWeeks*WEEK_SECONDS;
    }

    public boolean isCouldBeAppliedMultipleTimes(){
        return couldBeAppliedMultipleTimes || isNotNull(promoCode) && promoCode.isTwoWeeksOnSubscription();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("i", i)
                .append("subWeeks", subWeeks)
                .append("description", description)
                .append("type", type)
                .append("endDate", endDate)
                .append("freeWeeks", freeWeeks)
                .append("isActive", isActive)
                .append("maxUsers", maxUsers)
                .append("numUsers", numUsers)
                .append("startDate", startDate)
                .append("showPromotion", showPromotion)
                .append("label", label)
                .append("isWhiteListed", isWhiteListed)
                .append("userGroupId", getUserGroupId())
                .append("promoCodeId", getPromoCodeId())
                .append("isCouldBeAppliedMultipleTimes()", isCouldBeAppliedMultipleTimes())
                .toString();
    }

}