package mobi.nowtechnologies.server.persistence.domain.payment;

import mobi.nowtechnologies.server.persistence.domain.Promotion;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(name = "tb_promotionPaymentPolicy")
public class PromotionPaymentPolicy {
    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.AUTO)
    private Long id;

    private BigDecimal subcost;

    @Embedded
    private Period period;

    @ManyToOne(fetch = FetchType.LAZY)
    private Promotion promotion;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<PaymentPolicy> paymentPolicies;

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

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("subcost", subcost).append("period", period).toString();
    }
}