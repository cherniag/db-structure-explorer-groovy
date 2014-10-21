package mobi.nowtechnologies.server.shared.dto.admin;

import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;


/**
 * @author Titov Mykhaylo (titov)
 */
public class PaymentDetailsDto {
	
	private Long id;

	private int madeRetries;

	private int retriesOnError;

	private PaymentDetailsStatus lastPaymentStatus;

	private String descriptionError;

	private Date creationTimestamp;

	private Date disableTimestamp;

	private Object paymentPolicy;

	private boolean activated;

	private Object promotionPaymentPolicyDto;

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

	public Object getPaymentPolicy() {
		return paymentPolicy;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public Object getPromotionPaymentPolicyDto() {
		return promotionPaymentPolicyDto;
	}

	public void setPromotionPaymentPolicyDto(Object promotionPaymentPolicyDto) {
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
		return new ToStringBuilder(this)
				.append("id", id)
				.append("madeRetries", madeRetries)
				.append("retriesOnError", retriesOnError)
				.append("lastPaymentStatus", lastPaymentStatus)
				.append("descriptionError", descriptionError)
				.append("creationTimestamp", creationTimestamp)
				.append("disableTimestamp", disableTimestamp)
				.append("paymentPolicy", paymentPolicy)
				.append("activated", activated)
				.append("promotionPaymentPolicyDto", promotionPaymentPolicyDto)
				.append("userDto", userDto)
				.toString();
	}
}