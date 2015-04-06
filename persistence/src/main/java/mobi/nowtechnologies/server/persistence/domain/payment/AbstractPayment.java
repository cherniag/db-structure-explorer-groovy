package mobi.nowtechnologies.server.persistence.domain.payment;

import mobi.nowtechnologies.server.persistence.domain.User;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import java.math.BigDecimal;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@MappedSuperclass
public abstract class AbstractPayment {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "paymentDetailsId")
    protected PaymentDetails paymentDetails;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long i;
    private String internalTxId;
    private String externalTxId;
    private BigDecimal amount;
    private long timestamp;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId")
    private User user;
    @Column(insertable = false, updatable = false)
    private int userId;
    @Embedded
    private Period period;
    private String currencyISO;
    private String paymentSystem;
    @Enumerated(EnumType.STRING)
    private PaymentDetailsType type;
    @Column(insertable = false, updatable = false)
    private Long paymentDetailsId;

    public AbstractPayment() {
        internalTxId = UUID.randomUUID().toString().replaceAll("-", "");
    }

    public Long getI() {
        return i;
    }

    public void setI(Long i) {
        this.i = i;
    }

    public String getInternalTxId() {
        return internalTxId;
    }

    public void setInternalTxId(String internalTxId) {
        this.internalTxId = internalTxId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            userId = user.getId();
        }
    }

    public String getCurrencyISO() {
        return currencyISO;
    }

    public void setCurrencyISO(String currencyISO) {
        this.currencyISO = currencyISO;
    }

    public String getPaymentSystem() {
        return paymentSystem;
    }

    public void setPaymentSystem(String paymentSystem) {
        this.paymentSystem = paymentSystem;
    }

    public PaymentDetailsType getType() {
        return type;
    }

    public void setType(PaymentDetailsType type) {
        this.type = type;
    }

    public String getExternalTxId() {
        return externalTxId;
    }

    public void setExternalTxId(String externalTxId) {
        this.externalTxId = externalTxId;
    }

    public int getUserId() {
        return userId;
    }

    public PaymentDetails getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(PaymentDetails paymentDetails) {
        this.paymentDetails = paymentDetails;
        if (paymentDetails != null) {
            paymentDetailsId = paymentDetails.getI();
        }
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("i", i).append("internalTxId", internalTxId).append("externalTxId", externalTxId).append("amount", amount)
                                                                          .append("timestamp", timestamp).append("userId", userId).append("period", period)
                                                                          .append("currencyISO", currencyISO).append("paymentSystem", paymentSystem).append("type", type)
                                                                          .append("paymentDetails", paymentDetails).append("paymentDetailsId", paymentDetailsId).toString();
    }
}