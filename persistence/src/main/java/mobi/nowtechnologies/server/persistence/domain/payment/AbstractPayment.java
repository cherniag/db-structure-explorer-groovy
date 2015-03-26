package mobi.nowtechnologies.server.persistence.domain.payment;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.dto.web.PaymentHistoryItemDto;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetailsType.RETRY;

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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MappedSuperclass
public abstract class AbstractPayment {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPayment.class);
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
    private Integer offerId;
    private String currencyISO;
    private String paymentSystem;
    @Enumerated(EnumType.STRING)
    private PaymentDetailsType type;
    @Column(insertable = false, updatable = false)
    private Long paymentDetailsId;

    public AbstractPayment() {
        internalTxId = UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static List<PaymentHistoryItemDto> toPaymentHistoryItemDto(List<AbstractPayment> abstractPayments) {
        LOGGER.debug("input parameters abstractPayments: [{}]", abstractPayments);

        List<PaymentHistoryItemDto> paymentHistoryItemDtos = new LinkedList<PaymentHistoryItemDto>();
        for (AbstractPayment abstractPayment : abstractPayments) {
            paymentHistoryItemDtos.add(abstractPayment.toPaymentHistoryItemDto());
        }

        LOGGER.debug("Output parameter paymentHistoryItemDtos=[{}]", paymentHistoryItemDtos);
        return paymentHistoryItemDtos;
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

    public Integer getOfferId() {
        return offerId;
    }

    public void setOfferId(Integer offerId) {
        this.offerId = offerId;
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

    public PaymentHistoryItemDto toPaymentHistoryItemDto() {
        PaymentHistoryItemDto paymentHistoryItemDto = new PaymentHistoryItemDto();

        paymentHistoryItemDto.setTransactionId(internalTxId);
        paymentHistoryItemDto.setDate(new Date(timestamp));
        if (type.equals(PaymentDetailsType.FIRST)) {
            paymentHistoryItemDto.setDescription("1");
        } else {
            paymentHistoryItemDto.setDescription("2");
        }
        paymentHistoryItemDto.setDuration(period.getDuration());
        paymentHistoryItemDto.setDurationUnit(period.getDurationUnit());
        paymentHistoryItemDto.setPaymentMethod(paymentSystem);
        paymentHistoryItemDto.setAmount(amount);

        LOGGER.debug("Output parameter paymentHistoryItemDto=[{}]", paymentHistoryItemDto);
        return paymentHistoryItemDto;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("i", i).append("internalTxId", internalTxId).append("externalTxId", externalTxId).append("amount", amount)
                                                                          .append("timestamp", timestamp).append("userId", userId).append("period", period).append("offerId", offerId)
                                                                          .append("currencyISO", currencyISO).append("paymentSystem", paymentSystem).append("type", type)
                                                                          .append("paymentDetails", paymentDetails).append("paymentDetailsId", paymentDetailsId).toString();
    }
}