package mobi.nowtechnologies.server.persistence.domain.payment;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.dto.web.PaymentDetailsByPaymentDto;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "paymentType", discriminatorType = DiscriminatorType.STRING)
@Table(name = "tb_paymentDetails")
@NamedQuery(name = PaymentDetails.FIND_BY_USER_ID_AND_PAYMENT_DETAILS_TYPE, query = "select paymentDetails from PaymentDetails paymentDetails join paymentDetails.submittedPayments submittedPayments where paymentDetails.owner.id=?1 and submittedPayments.type=?2 order by paymentDetails.creationTimestampMillis desc")
public class PaymentDetails {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PaymentDetails.class);

	public static final String UNKNOW_TYPE = "unknown";
	public static final String SAGEPAY_CREDITCARD_TYPE = "sagePayCreditCard";
	public static final String PAYPAL_TYPE = "payPal";
	public static final String MIG_SMS_TYPE = "migSms";
	public static final String O2_PSMS_TYPE = "o2Psms";
	public static final String VF_PSMS_TYPE = "vfPsms";
	public static final String PSMS_TYPE = "psms";
	public static final String ITUNES_SUBSCRIPTION="iTunesSubscription";
	public static final String FIND_BY_USER_ID_AND_PAYMENT_DETAILS_TYPE = "FIND_BY_USER_ID_AND_PAYMENT_DETAILS_TYPE";

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
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="paymentDetails")
	private List<SubmittedPayment> submittedPayments;

    @Column(name = "last_failed_payment_notification_millis", nullable = true)
    private Long lastFailedPaymentNotificationMillis;

	public void incrementRetries() {
		this.madeRetries++;
	}
	
	public void decrementRetries() {
		if (madeRetries > 0) {
			this.madeRetries--;
		}
	}

	public Long getI() {
		return i;
	}

	public void setI(Long i) {
		this.i = i;
	}

	public int getMadeRetries() {
		return madeRetries;
	}

	public void setMadeRetries(int madeRetries) {
		this.madeRetries = madeRetries;
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

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public PromotionPaymentPolicy getPromotionPaymentPolicy() {
		return promotionPaymentPolicy;
	}

	public void setPromotionPaymentPolicy(PromotionPaymentPolicy promotionPaymentPolicy) {
		this.promotionPaymentPolicy = promotionPaymentPolicy;
	}
	
	public String getPaymentType(){
        return UNKNOW_TYPE;
    }

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
		if (!owner.getPaymentDetailsList().contains(this)) {
			owner.getPaymentDetailsList().add(this);
		}
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

    public PaymentDetails withPaymentPolicy(PaymentPolicy paymentPolicy){
        setPaymentPolicy(paymentPolicy);
        return this;
    }

    public PaymentDetails withActivated(boolean activated) {
        setActivated(activated);
        return this;
    }

    public PaymentDetails withDisableTimestampMillis(long disableTimestampMillis){
        this.disableTimestampMillis = disableTimestampMillis;
        return this;
    }

    public PaymentDetails withDescriptionError(String descriptionError){
        this.descriptionError = descriptionError;
        return this;
    }

    public PaymentDetails withLastFailedPaymentNotificationMillis(Long lastFailedPaymentNotificationMillis){
        this.lastFailedPaymentNotificationMillis = lastFailedPaymentNotificationMillis;
        return this;
    }

    public PaymentDetails withMadeRetries(int madeRetries){
        this.madeRetries = madeRetries;
        return this;
    }

    public PaymentDetails withRetriesOnError(int retriesOnError){
        this.retriesOnError = retriesOnError;
        return this;
    }

    public PaymentDetails withOwner(User user) {
        this.owner = user;
        return this;
    }

    @PrePersist
    public void validate() {
        ActivationStatus activationStatus = owner.getActivationStatus();
        if (!ActivationStatus.ACTIVATED.equals(activationStatus)) throw new RuntimeException("Unexpected activation status ["+activationStatus+"]. Payment details' owner should be in ACTIVATED activation status");
    }

	@Override
	public String toString() {
        return new ToStringBuilder(this)
                .append("i", i)
                .append("madeRetries", madeRetries)
                .append("retriesOnError", retriesOnError)
                .append("lastPaymentStatus", lastPaymentStatus)
                .append("descriptionError", descriptionError)
                .append("errorCode", errorCode)
                .append("creationTimestampMillis", creationTimestampMillis)
                .append("disableTimestampMillis", disableTimestampMillis)
                .append("lastFailedPaymentNotificationMillis", lastFailedPaymentNotificationMillis)
                .append("activated", activated)
                .toString();
	}
}