package mobi.nowtechnologies.server.shared.dto;

import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PromotionPaymentPolicy;
import mobi.nowtechnologies.server.shared.enums.MediaType;
import mobi.nowtechnologies.server.shared.enums.PeriodUnit;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;

@XmlRootElement(name="PaymentPolicy")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class PaymentPolicyDto {

    public static final String PAYMENT_POLICY_DTO = "paymentPolicy";

    private Integer id;
    private BigDecimal subcost;
    private long period;
    private PeriodUnit periodUnit;
    private Integer operator;
    private String operatorName;
    private String paymentType;
    private String shortCode;
    private BigDecimal oldSubcost;
    private Long oldPeriod;
    private PeriodUnit oldPeriodUnit;
    private String currencyISO;
    private boolean videoAndAudio4GSubscription;
    private boolean fourG;
    private boolean threeG;
    private MediaType paymentPolicyMediaType;

    public PaymentPolicyDto() { }

    public PaymentPolicyDto(PaymentPolicy policy, PromotionPaymentPolicy promotionPaymentPolicy){
        this(policy);
        if (isNotNull(promotionPaymentPolicy)) {
            setSubcost(promotionPaymentPolicy.getSubcost());
            setPeriod(promotionPaymentPolicy.getPeriod().getDuration());
            setPeriodUnit(promotionPaymentPolicy.getPeriod().getPeriodUnit());

            setOldSubcost(policy.getSubcost());
            setOldPeriod(policy.getPeriod().getDuration());
            setOldPeriodUnit(policy.getPeriod().getPeriodUnit());
        }
    }

    public PaymentPolicyDto(PaymentPolicy policy) {
        setId(policy.getId());
        setCurrencyISO(policy.getCurrencyISO());
        setSubcost(policy.getSubcost());
        setPeriod(policy.getPeriod().getDuration());
        setPeriodUnit(policy.getPeriod().getPeriodUnit());
        if (isNotNull(policy.getOperator())) {
            setOperator(policy.getOperator().getId());
            setOperatorName(policy.getOperator().getName());
        }
        setPaymentType(policy.getPaymentType());
        setShortCode(policy.getShortCode());
        setCurrencyISO(policy.getCurrencyISO());
        setVideoAndAudio4GSubscription(policy.is4GVideoAudioSubscription());
        setPaymentPolicyMediaType(policy.getMediaType());
        setFourG( Tariff._4G == policy.getTariff() );
        setThreeG( Tariff._3G == policy.getTariff() );
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getSubcost() {
        return subcost;
    }

    public void setSubcost(BigDecimal subcost) {
        this.subcost = subcost;
    }

    public BigDecimal getOldSubcost() {
        return oldSubcost;
    }

    public void setOldSubcost(BigDecimal oldSubcost) {
        this.oldSubcost = oldSubcost;
    }

    public Integer getOperator() {
        return operator;
    }

    public void setOperator(Integer operator) {
        this.operator = operator;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getCurrencyISO() {
        return currencyISO;
    }

    public void setCurrencyISO(String currencyISO) {
        this.currencyISO = currencyISO;
    }
    
    public boolean isVideoAndAudio4GSubscription() {
		return videoAndAudio4GSubscription;
	}

	public void setVideoAndAudio4GSubscription(boolean fourGPaymentPolicy) {
		this.videoAndAudio4GSubscription = fourGPaymentPolicy;
	}

	public MediaType getPaymentPolicyMediaType() {
		return paymentPolicyMediaType;
	}

	public void setPaymentPolicyMediaType(
			MediaType paymentPolicyMediaType) {
		this.paymentPolicyMediaType = paymentPolicyMediaType;
	}

	public boolean isFourG() {
		return fourG;
	}

	public void setFourG(boolean fourG) {
		this.fourG = fourG;
	}

	public boolean isThreeG() {
		return threeG;
	}

	public void setThreeG(boolean threeG) {
		this.threeG = threeG;
	}

    public Long getOldPeriod() {
        return oldPeriod;
    }

    public void setOldPeriod(Long oldPeriod) {
        this.oldPeriod = oldPeriod;
    }

    public PeriodUnit getOldPeriodUnit() {
        return oldPeriodUnit;
    }

    public void setOldPeriodUnit(PeriodUnit oldPeriodUnit) {
        this.oldPeriodUnit = oldPeriodUnit;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public PeriodUnit getPeriodUnit() {
        return periodUnit;
    }

    public void setPeriodUnit(PeriodUnit periodUnit) {
        this.periodUnit = periodUnit;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("subcost", subcost)
                .append("period", period)
                .append("periodUnit", periodUnit)
                .append("operator", operator)
                .append("operatorName", operatorName)
                .append("paymentType", paymentType)
                .append("shortCode", shortCode)
                .append("oldSubcost", oldSubcost)
                .append("oldPeriod", oldPeriod)
                .append("oldPeriodUnit", oldPeriodUnit)
                .append("currencyISO", currencyISO)
                .append("videoAndAudio4GSubscription", videoAndAudio4GSubscription)
                .append("fourG", fourG)
                .append("threeG", threeG)
                .append("paymentPolicyMediaType", paymentPolicyMediaType)
                .toString();
    }
}