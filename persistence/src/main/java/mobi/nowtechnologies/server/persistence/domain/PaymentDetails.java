package mobi.nowtechnologies.server.persistence.domain;

import java.util.List;

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
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mobi.nowtechnologies.server.shared.dto.web.PaymentDetailsByPaymentDto;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "paymentType", discriminatorType = DiscriminatorType.STRING)
@Table(name = "tb_paymentDetails")
@NamedQuery(name = PaymentDetails.FIND_BY_USER_ID_AND_PAYMENT_DETAILS_TYPE, query = "select paymentDetails from PaymentDetails paymentDetails join paymentDetails.submittedPayments submittedPayments where paymentDetails.owner.id=?1 and submittedPayments.type=?2 order by paymentDetails.creationTimestampMillis desc")
public abstract class PaymentDetails {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PaymentDetails.class);

	public static final String UNKNOW_TYPE = "unknown";
	public static final String SAGEPAY_CREDITCARD_TYPE = "sagePayCreditCard";
	public static final String PAYPAL_TYPE = "payPal";
	public static final String MIG_SMS_TYPE = "migSms";
	public static final String O2_PSMS_TYPE = "o2Psms";
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
	
	public abstract String getPaymentType();

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
		if (!owner.getPaymentDetailsList().contains(this)) {
			owner.getPaymentDetailsList().add(this);
		}
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
	
	@Override
	public String toString() {
		return "i=" + i + ", activated=" + activated + ", creationTimestampMillis=" + creationTimestampMillis + ", descriptionError=" + descriptionError + ", disableTimestampMillis="
		+ disableTimestampMillis + ", lastPaymentStatus=" + lastPaymentStatus + ", madeRetries=" + madeRetries + ", retriesOnError=" + retriesOnError;
	}
}