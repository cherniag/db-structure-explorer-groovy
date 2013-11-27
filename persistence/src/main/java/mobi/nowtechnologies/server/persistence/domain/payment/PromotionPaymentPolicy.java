package mobi.nowtechnologies.server.persistence.domain.payment;

import mobi.nowtechnologies.server.persistence.domain.Promotion;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name="tb_promotionPaymentPolicy")
@NamedQueries({
	@NamedQuery(name=PromotionPaymentPolicy.NQ_GET_PROMOTION_PAYMENT_WITH_PAYMENT_POLICY, query="select promo from PromotionPaymentPolicy promo join promo.paymentPolicies pPolicy where promo.promotion=? and pPolicy=?")
})
public class PromotionPaymentPolicy {
	
	public static final String NQ_GET_PROMOTION_PAYMENT_WITH_PAYMENT_POLICY = "getPromotionPaymentWithPaymentPolicy";
	
	@Id
	@GeneratedValue(strategy = javax.persistence.GenerationType.AUTO)
	private Long id;
	
	private BigDecimal subcost;
	
	private Integer subweeks;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private Promotion promotion;
	
	@ManyToMany(fetch=FetchType.LAZY)
	private List<PaymentPolicy> paymentPolicies;

	public PromotionPaymentPolicy() {
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getSubcost() {
		return subcost;
	}

	public void setSubcost(BigDecimal subcost) {
		this.subcost = subcost;
	}

	public Integer getSubweeks() {
		return subweeks;
	}

	public void setSubweeks(Integer subweeks) {
		this.subweeks = subweeks;
	}

	public Promotion getPromotion() {
		return promotion;
	}

	public void setPromotion(Promotion promotion) {
		this.promotion = promotion;
	}

	public List<PaymentPolicy> getPaymentPolicies() {
		return paymentPolicies;
	}

	public void setPaymentPolicies(List<PaymentPolicy> paymentPolicies) {
		this.paymentPolicies = paymentPolicies;
	}
}