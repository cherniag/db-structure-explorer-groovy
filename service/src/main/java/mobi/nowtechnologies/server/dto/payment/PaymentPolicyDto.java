package mobi.nowtechnologies.server.dto.payment;

import mobi.nowtechnologies.server.persistence.domain.enums.PaymentPolicyType;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PromotionPaymentPolicy;
import mobi.nowtechnologies.server.shared.enums.DurationUnit;
import mobi.nowtechnologies.server.shared.enums.MediaType;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;
import static mobi.nowtechnologies.server.shared.enums.DurationUnit.MONTHS;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import java.math.BigDecimal;
import java.util.Comparator;

import org.apache.commons.lang3.builder.ToStringBuilder;

@XmlRootElement(name = "PaymentPolicy")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class PaymentPolicyDto {

    public static final String PAYMENT_POLICY_DTO = "paymentPolicy";

    private Integer id;
    private BigDecimal subcost;
    private int duration;
    private DurationUnit durationUnit;
    private Integer operator;
    private String operatorName;
    private String paymentType;
    private String shortCode;
    private BigDecimal oldSubcost;
    private Integer oldDuration;
    private DurationUnit oldDurationUnit;
    private String currencyISO;
    private boolean videoAndAudio4GSubscription;
    private boolean fourG;
    private boolean threeG;
    private MediaType paymentPolicyMediaType;
    private PaymentPolicyType paymentPolicyType;
    private String appStoreProductId;
    private int order;
    private String messageKey;

    public PaymentPolicyDto() { }

    public PaymentPolicyDto(PaymentPolicy policy, PromotionPaymentPolicy promotionPaymentPolicy) {
        this(policy);
        if (isNotNull(promotionPaymentPolicy)) {
            setSubcost(promotionPaymentPolicy.getSubcost());
            setDuration(promotionPaymentPolicy.getPeriod().getDuration());
            setDurationUnit(promotionPaymentPolicy.getPeriod().getDurationUnit());

            setOldSubcost(policy.getSubcost());
            setOldDuration(policy.getPeriod().getDuration());
            setOldDurationUnit(policy.getPeriod().getDurationUnit());
        }
    }

    public PaymentPolicyDto(PaymentPolicy policy) {
        setId(policy.getId());
        setCurrencyISO(policy.getCurrencyISO());
        setSubcost(policy.getSubcost());
        setDuration(policy.getPeriod().getDuration());
        setDurationUnit(policy.getPeriod().getDurationUnit());
        if (isNotNull(policy.getOperator())) {
            setOperator(policy.getOperator().getId());
            setOperatorName(policy.getOperator().getName());
        }
        setPaymentType(policy.getPaymentType());
        setShortCode(policy.getShortCode());
        setCurrencyISO(policy.getCurrencyISO());
        setVideoAndAudio4GSubscription(policy.is4GVideoAudioSubscription());
        setPaymentPolicyMediaType(policy.getMediaType());
        setFourG(Tariff._4G == policy.getTariff());
        setThreeG(Tariff._3G == policy.getTariff());
        setPaymentPolicyType(policy.getPaymentPolicyType());
        setAppStoreProductId(policy.getAppStoreProductId());
        this.order = policy.getOrder();
        this.messageKey = policy.getMessageKey();
    }

    public boolean isMonthly() {
        return durationUnit.equals(MONTHS);
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

    public void setPaymentPolicyMediaType(MediaType paymentPolicyMediaType) {
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

    public Integer getOldDuration() {
        return oldDuration;
    }

    public void setOldDuration(Integer oldDuration) {
        this.oldDuration = oldDuration;
    }

    public DurationUnit getOldDurationUnit() {
        return oldDurationUnit;
    }

    public void setOldDurationUnit(DurationUnit oldDurationUnit) {
        this.oldDurationUnit = oldDurationUnit;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public DurationUnit getDurationUnit() {
        return durationUnit;
    }

    public void setDurationUnit(DurationUnit durationUnit) {
        this.durationUnit = durationUnit;
    }

    public PaymentPolicyType getPaymentPolicyType() {
        return paymentPolicyType;
    }

    public void setPaymentPolicyType(PaymentPolicyType paymentPolicyType) {
        this.paymentPolicyType = paymentPolicyType;
    }

    public String getAppStoreProductId() {
        return appStoreProductId;
    }

    public void setAppStoreProductId(String appStoreProductId) {
        this.appStoreProductId = appStoreProductId;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("subcost", subcost).append("duration", duration).append("periodUnit", durationUnit).append("operator", operator)
                                        .append("operatorName", operatorName).append("paymentType", paymentType).append("shortCode", shortCode).append("oldSubcost", oldSubcost)
                                        .append("oldDuration", oldDuration).append("oldPeriodUnit", oldDurationUnit).append("currencyISO", currencyISO)
                                        .append("videoAndAudio4GSubscription", videoAndAudio4GSubscription).append("fourG", fourG).append("threeG", threeG)
                                        .append("paymentPolicyMediaType", paymentPolicyMediaType).append("paymentPolicyType", paymentPolicyType).append("appStoreProductId", appStoreProductId)
                                        .append("order", order).append("messageKey", messageKey).toString();
    }

    public static class ByOrderAscAndDurationAsc implements Comparator<PaymentPolicyDto> {
        @Override
        public int compare(PaymentPolicyDto dto1, PaymentPolicyDto dto2) {
            int compareResult = Integer.compare(dto1.order, dto2.order);

            if (compareResult == 0) {
                compareResult = dto1.getDurationUnit().compareTo(dto2.getDurationUnit());
            }

            if (compareResult == 0) {
                compareResult = Integer.compare(dto1.getDuration(), dto2.getDuration());
            }

            return compareResult;
        }
    }

    public static class ByOrderAscAndDurationDesc implements Comparator<PaymentPolicyDto> {

        @Override
        public int compare(PaymentPolicyDto dto1, PaymentPolicyDto dto2) {
            int compareResult = Integer.compare(dto1.order, dto2.order);

            if (compareResult == 0) {
                compareResult = dto2.getDurationUnit().compareTo(dto1.getDurationUnit());
            }

            if (compareResult == 0) {
                compareResult = Integer.compare(dto2.getDuration(), dto1.getDuration());
            }

            return compareResult;
        }
    }
}