package mobi.nowtechnologies.server.shared.dto.admin;

import java.util.Date;

import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;


/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class PaymentDetailsDto {
	
	private Long id;

	private int madeRetries;

	private int retriesOnError;

	private PaymentDetailsStatus lastPaymentStatus;

	private String descriptionError;

	private Date creationTimestamp;

	private Date disableTimestamp;

	private PaymentPolicyDto paymentPolicy;

	private boolean activated;

	private PromotionPaymentPolicyDto promotionPaymentPolicyDto;

	private UserDto userDto;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getMadeRetries() {
		return madeRetries;
	}

	public void setMadeRetries(int madeRetries) {
		this.madeRetries = madeRetries;
	}

	public int getRetriesOnError() {
		return retriesOnError;
	}

	public void setRetriesOnError(int retriesOnError) {
		this.retriesOnError = retriesOnError;
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

	public Date getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(Date creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	public Date getDisableTimestamp() {
		return disableTimestamp;
	}

	public void setDisableTimestamp(Date disableTimestamp) {
		this.disableTimestamp = disableTimestamp;
	}

	public PaymentPolicyDto getPaymentPolicy() {
		return paymentPolicy;
	}

	public void setPaymentPolicy(PaymentPolicyDto paymentPolicy) {
		this.paymentPolicy = paymentPolicy;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public PromotionPaymentPolicyDto getPromotionPaymentPolicyDto() {
		return promotionPaymentPolicyDto;
	}

	public void setPromotionPaymentPolicyDto(PromotionPaymentPolicyDto promotionPaymentPolicyDto) {
		this.promotionPaymentPolicyDto = promotionPaymentPolicyDto;
	}

	public UserDto getUserDto() {
		return userDto;
	}

	public void setUserDto(UserDto userDto) {
		this.userDto = userDto;
	}

	@Override
	public String toString() {
		return "PaymentDetailsDto [activated=" + activated + ", creationTimestamp=" + creationTimestamp + ", descriptionError=" + descriptionError
				+ ", disableTimestamp=" + disableTimestamp + ", id=" + id + ", lastPaymentStatus=" + lastPaymentStatus + ", madeRetries=" + madeRetries
				+ ", paymentPolicy=" + paymentPolicy + ", promotionPaymentPolicyDto=" + promotionPaymentPolicyDto + ", retriesOnError=" + retriesOnError
				+ ", userDto=" + userDto + "]";
	}

}