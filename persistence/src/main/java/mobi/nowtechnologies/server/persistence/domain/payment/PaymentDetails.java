package mobi.nowtechnologies.server.persistence.domain.payment;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.dto.web.PaymentDetailsByPaymentDto;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNull;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.ACTIVATED;
import static mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus.ERROR;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "paymentType", discriminatorType = DiscriminatorType.STRING)
@Table(name = "tb_paymentDetails")
public class PaymentDetails {

    public static final String UNKNOW_TYPE = "unknown";
    public static final String SAGEPAY_CREDITCARD_TYPE = "sagePayCreditCard";
    public static final String PAYPAL_TYPE = "payPal";
    public static final String MIG_SMS_TYPE = "migSms";
    public static final String O2_PSMS_TYPE = "o2Psms";
    public static final String VF_PSMS_TYPE = "vfPsms";
    public static final String PSMS_TYPE = "psms";
    public static final String ITUNES_SUBSCRIPTION = "iTunesSubscription";
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentDetails.class);
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long i;

    private int madeRetries;

    private int retriesOnError;

    @Enumerated(EnumType.STRING)
    private PaymentDetailsStatus lastPaymentStatus;

    private String descriptionError;

    private String errorCode;

    private long creationTimestampMillis;

    private long disableTimestampMillis;

    @ManyToOne
    @JoinColumn(name = "paymentPolicyId")
    private PaymentPolicy paymentPolicy;

    private boolean activated;

    @OneToOne
    private PromotionPaymentPolicy promotionPaymentPolicy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "paymentDetails")
    private List<SubmittedPayment> submittedPayments;

    @Column(name = "last_failed_payment_notification_millis", nullable = true)
    private Long lastFailedPaymentNotificationMillis;

    @Column(name = "made_attempts", nullable = false)
    private int madeAttempts;

    public Long getI() {
        return i;
    }

    public void setI(Long i) {
        this.i = i;
    }

    public int getMadeRetries() {
        return madeRetries;
    }

    public PaymentDetailsStatus getLastPaymentStatus() {
        return lastPaymentStatus;
    }

    public void setLastPaymentStatus(PaymentDetailsStatus lastPaymentStatus) {
        this.lastPaymentStatus = lastPaymentStatus;
    }

    public String getDescriptionError() {
        return descriptionError;
    }

    public void setDescriptionError(String descriptionError) {
        this.descriptionError = descriptionError;
    }

    public long getCreationTimestampMillis() {
        return creationTimestampMillis;
    }

    public void setCreationTimestampMillis(long creationTimestampMillis) {
        this.creationTimestampMillis = creationTimestampMillis;
    }

    public long getDisableTimestampMillis() {
        return disableTimestampMillis;
    }

    public void setDisableTimestampMillis(long disableTimestampMillis) {
        this.disableTimestampMillis = disableTimestampMillis;
    }

    public PaymentPolicy getPaymentPolicy() {
        return paymentPolicy;
    }

    public void setPaymentPolicy(PaymentPolicy paymentPolicy) {
        this.paymentPolicy = paymentPolicy;
    }

    public int getRetriesOnError() {
        return retriesOnError;
    }

    public void setRetriesOnError(int retriesOnError) {
        this.retriesOnError = retriesOnError;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public boolean isDeactivated() {
        return !isActivated();
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public PromotionPaymentPolicy getPromotionPaymentPolicy() {
        return promotionPaymentPolicy;
    }

    public void setPromotionPaymentPolicy(PromotionPaymentPolicy promotionPaymentPolicy) {
        this.promotionPaymentPolicy = promotionPaymentPolicy;
    }

    public String getPaymentType() {
        return UNKNOW_TYPE;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public int getMadeAttempts() {
        return madeAttempts;
    }

    public Long getLastFailedPaymentNotificationMillis() {
        return lastFailedPaymentNotificationMillis;
    }

    public PaymentDetailsByPaymentDto toPaymentDetailsByPaymentDto() {
        PaymentDetailsByPaymentDto paymentDetailsByPaymentDto = new PaymentDetailsByPaymentDto();

        paymentDetailsByPaymentDto.setPaymentType(getPaymentType());
        paymentDetailsByPaymentDto.setPaymentDetailsId(i);
        paymentDetailsByPaymentDto.setActivated(activated);

        paymentDetailsByPaymentDto.setPaymentPolicyDto(paymentPolicy.toPaymentPolicyDto(paymentDetailsByPaymentDto));

        LOGGER.debug("Output parameter [{}]", paymentDetailsByPaymentDto);
        return paymentDetailsByPaymentDto;
    }

    public PaymentDetails withLastPaymentStatus(PaymentDetailsStatus lastPaymentStatus) {
        setLastPaymentStatus(lastPaymentStatus);
        return this;
    }

    public PaymentDetails withPaymentPolicy(PaymentPolicy paymentPolicy) {
        setPaymentPolicy(paymentPolicy);
        return this;
    }

    public PaymentDetails withActivated(boolean activated) {
        setActivated(activated);
        return this;
    }

    public PaymentDetails withDisableTimestampMillis(long disableTimestampMillis) {
        this.disableTimestampMillis = disableTimestampMillis;
        return this;
    }

    public PaymentDetails withDescriptionError(String descriptionError) {
        this.descriptionError = descriptionError;
        return this;
    }

    public PaymentDetails withLastFailedPaymentNotificationMillis(Long lastFailedPaymentNotificationMillis) {
        this.lastFailedPaymentNotificationMillis = lastFailedPaymentNotificationMillis;
        return this;
    }

    public PaymentDetails withMadeRetries(int madeRetries) {
        this.madeRetries = madeRetries;
        return this;
    }

    public PaymentDetails withRetriesOnError(int retriesOnError) {
        this.retriesOnError = retriesOnError;
        return this;
    }

    public PaymentDetails withOwner(User user) {
        this.owner = user;
        return this;
    }

    public PaymentDetails withI(Long i) {
        this.i = i;
        return this;
    }

    @PrePersist
    public void validate() {
        ActivationStatus activationStatus = owner.getActivationStatus();
        if (!ACTIVATED.equals(activationStatus)) {
            throw new RuntimeException("Unexpected activation status [" + activationStatus + "]. Payment details' owner should be in ACTIVATED activation status");
        }
    }

    public PaymentDetails withMadeAttempts(int madeAttempts) {
        this.madeAttempts = madeAttempts;
        return this;
    }

    public boolean shouldBeUnSubscribed() {
        return shouldBeUnSubscribedOnReSubscription() || areAllAttemptSpent();
    }

    private boolean shouldBeUnSubscribedOnReSubscription() {
        PaymentDetails lastSuccessfulPaymentDetails = owner.getLastSuccessfulPaymentDetails();
        return (isNull(lastSuccessfulPaymentDetails) || !lastSuccessfulPaymentDetails.getI().equals(i)) && madeAttempts > 0;
    }

    public void resetMadeAttemptsForFirstPayment() {
        this.madeAttempts = 0;
        this.madeRetries = -1;
    }

    public void resetMadeAttempts() {
        this.madeAttempts = 0;
        resetMadeRetries();
    }

    public boolean isCurrentAttemptFailed() {
        return madeAttempts > 0 && madeRetries == 0 && lastPaymentStatus.equals(ERROR);
    }

    public int incrementMadeAttemptsAccordingToMadeRetries() {
        incrementRetries();
        if (madeRetries == retriesOnError) {
            incrementMadeAttempts();
            resetMadeRetries();
        }
        return madeAttempts;
    }

    private void resetMadeRetries() {
        this.madeRetries = 0;
    }

    private boolean areAllAttemptSpent() {
        return areAll3AttemptsSpent() || areAll2AttemptsSpent() || all1AttemptRetriesAreSpent();
    }

    private boolean all1AttemptRetriesAreSpent() {
        int afterNextSubPaymentSeconds = paymentPolicy.getAfterNextSubPaymentSeconds();
        int advancedPaymentSeconds = paymentPolicy.getAdvancedPaymentSeconds();
        return madeAttempts == 1 && afterNextSubPaymentSeconds == 0 && advancedPaymentSeconds == 0;
    }

    private boolean areAll2AttemptsSpent() {
        int afterNextSubPaymentSeconds = paymentPolicy.getAfterNextSubPaymentSeconds();
        int advancedPaymentSeconds = paymentPolicy.getAdvancedPaymentSeconds();
        return madeAttempts == 2 && (afterNextSubPaymentSeconds == 0 || (advancedPaymentSeconds == 0 && afterNextSubPaymentSeconds > 0));
    }

    private boolean areAll3AttemptsSpent() {
        return madeAttempts == 3;
    }

    private void incrementRetries() {
        this.madeRetries++;
    }

    private void incrementMadeAttempts() {
        madeAttempts++;
    }

    public boolean isAwaiting() {
        return PaymentDetailsStatus.AWAITING.equals(getLastPaymentStatus());
    }

    public boolean isErrorAndCanRetry() {
        return PaymentDetailsStatus.ERROR.equals(getLastPaymentStatus()) && !areAllAttemptSpent() && isActivated();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("i", i).append("madeAttempts", madeAttempts).append("madeRetries", madeRetries).append("retriesOnError", retriesOnError)
                                        .append("lastPaymentStatus", lastPaymentStatus).append("descriptionError", descriptionError).append("errorCode", errorCode)
                                        .append("creationTimestampMillis", creationTimestampMillis).append("disableTimestampMillis", disableTimestampMillis)
                                        .append("lastFailedPaymentNotificationMillis", lastFailedPaymentNotificationMillis).append("activated", activated).toString();
    }

    public void disable(String reason, Date epochMillis) {
        withActivated(false);
        withDisableTimestampMillis(epochMillis.getTime());
        withDescriptionError(reason);
    }

    public void completedWithError(String descriptionError) {
        setDescriptionError(descriptionError);
        setErrorCode(null);
        setLastPaymentStatus(PaymentDetailsStatus.ERROR);
    }
}