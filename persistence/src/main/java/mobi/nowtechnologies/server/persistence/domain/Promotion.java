package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.shared.ObjectUtils;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

import static mobi.nowtechnologies.server.shared.ObjectUtils.*;

/**
 * The persistent class for the tb_promotions database table.
 * 
 */
@Entity
@Table(name = "tb_promotions")
@NamedQueries( { @NamedQuery(name = Promotion.NQ_GET_PROMOTION_WITH_FILTER, query = "select distinct prom from Promotion prom join prom.filters where isActive=true and (numUsers<maxUsers or maxUsers=0) and endDate>?1 and startDate<?1 and userGroup=?2") })
public class Promotion implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String NQ_GET_PROMOTION_WITH_FILTER = "getPromotionWithFilter";

	public static final String ADD_FREE_WEEKS_PROMOTION = "promoCode";
	public static final String ADD_SUBBALANCE_PROMOTION = "noPromoCode";
	public static final String UPDATE_PAYMENTPOLICY_PROMOTION = "updatePaymentPolicy";
	public static final String UPDATE_PAYMENTPOLICY_FREE_TRIAL_PERIOD_PROMOTION = "updatePPFreeTPeriod";

	public static enum Fields {
		i, description, endDate, freeWeeks, isActive, maxUsers, numUsers, startDate, subWeeks, userGroup, type, showPromotion
	}

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

	@Column(length = 20, nullable = true)
	private String label;

	@OneToMany(mappedBy = "promotion", fetch = FetchType.LAZY)
	private List<PromotionPaymentPolicy> promotionPaymentPolicies;

	@ManyToMany(fetch = FetchType.LAZY)
	private List<AbstractFilter> filters;

	@OneToOne(fetch = FetchType.EAGER, mappedBy="promotion")
	private PromoCode promoCode;

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
        if (ObjectUtils.isNotNull(promoCode)){
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

    public Byte getUserGroupId() {
        if(isNotNull(userGroup)){
            return userGroup.getI();
        }
        return null;
    }

    public UserGroup getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
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

    @Override
	public String toString() {
		return "Promotion [i=" + i + ", description=" + description + ", type=" + type + ", endDate=" + endDate + ", freeWeeks=" + freeWeeks + ", isActive="
				+ isActive + ", maxUsers=" + maxUsers + ", numUsers=" + numUsers + ", startDate=" + startDate + ", subWeeks=" + subWeeks + ", userGroupId="
				+ getUserGroupId() + ", showPromotion=" + showPromotion + ", label=" + label + ", promoCodeId=" + getPromoCodeId() + "]";
	}

}