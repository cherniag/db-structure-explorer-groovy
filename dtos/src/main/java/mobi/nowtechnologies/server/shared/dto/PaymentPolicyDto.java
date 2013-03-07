package mobi.nowtechnologies.server.shared.dto;

import mobi.nowtechnologies.server.persistence.domain.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.PromotionPaymentPolicy;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="PaymentPolicy")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class PaymentPolicyDto {

    public static final String PAYMENT_POLICY_DTO = "paymentPolicy";

    private short id;
    private BigDecimal subcost;
    private Integer subweeks;
    private Integer operator;
    private String operatorName;
    private String paymentType;
    private String shortCode;
    private BigDecimal oldSubcost;
    private Integer oldSubweeks;
    private String currencyISO;

    public PaymentPolicyDto() { }

    public PaymentPolicyDto(PaymentPolicy policy, PromotionPaymentPolicy promotion){
        this(policy);
        if (null != promotion) {
            setSubcost(promotion.getSubcost());
            setSubweeks(promotion.getSubweeks());
        }
    }

    public PaymentPolicyDto(PaymentPolicy policy){
        setId(policy.getId());
        setCurrencyISO(policy.getCurrencyISO());
        setOldSubweeks(Integer.valueOf(policy.getSubweeks()));
        setSubcost(policy.getSubcost());
        setOldSubcost(policy.getSubcost());
        setSubweeks(Integer.valueOf(policy.getSubweeks()));
        if (null!=policy.getOperator()) {
            setOperator(policy.getOperator().getId());
            setOperatorName(policy.getOperator().getName());
        }
        setPaymentType(policy.getPaymentType());
        setShortCode(policy.getShortCode());
        setCurrencyISO(policy.getCurrencyISO());
    }

    public boolean isO2FiveWeekPsmsSubscription(){
        return subweeks.equals(5) && isO2PsmsSubscribtion();
    }

    public boolean isO2OneWeekPsmsSubscription(){
        return subweeks.equals(1) && isO2PsmsSubscribtion();
    }

    private boolean isO2PsmsSubscribtion() {
        return isPsmsPolicy() && isO2Operator();
    }

    private boolean isPsmsPolicy() {
        return paymentType.equalsIgnoreCase("");
    }

    private boolean isO2Operator() {
        return "O2 UK".equals(operatorName);
    }

    public boolean isO2TwoWeekPsmsSubscription(){
        return subweeks.equals(2) && isO2PsmsSubscribtion();
    }

    public short getId() {
        return id;
    }
    public void setId(short id) {
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

    public BigDecimal getOldSubcost() {
        return oldSubcost;
    }

    public void setOldSubcost(BigDecimal oldSubcost) {
        this.oldSubcost = oldSubcost;
    }

    public Integer getOldSubweeks() {
        return oldSubweeks;
    }

    public void setOldSubweeks(Integer oldSubweeks) {
        this.oldSubweeks = oldSubweeks;
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

    public boolean isO2BusinessPolicy() {
        return isO2Operator() && (isPayPalPolicy() || isCreditCardPolicy());
    }

    public boolean isNonO2Policy() {
        return isO2Operator() && (isPayPalPolicy() || isCreditCardPolicy() || isInAppPolicy());
    }

    private boolean isInAppPolicy() {
        return true;
    }

    private boolean isCreditCardPolicy() {
        return true;
    }

    private boolean isPayPalPolicy() {
        return true;
    }

    public boolean isO2ConsumerPolicy() {
        return isO2OneWeekPsmsSubscription()|| isO2TwoWeekPsmsSubscription() || isO2FiveWeekPsmsSubscription();
    }
}