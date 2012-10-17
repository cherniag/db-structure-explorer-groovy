package mobi.nowtechnologies.server.shared.dto.web;

import java.util.Date;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class AccountDto {
	
	public static final String ACCOUNT_DTO = "accountDto";
	
	@Pattern(regexp="^(07(\\d ?){9})$")
	private String phoneNumber;
	
	private Date timeOfMovingToLimitedStatus;
	
	private String email;
	
	private Subscription subscription;

	@Pattern(regexp=".{6,20}")
	private String currentPassword;
	
	@NotEmpty
	@Pattern(regexp=".{6,20}")
	private String newPassword;

	@NotEmpty
	@Pattern(regexp=".{6,20}")
	private String confirmPassword;
	
	private String potentialPromotion; 

	private Integer subBalance;
	
	public enum Subscription{
		freeTrialSubscription, subscribedSubscription, unsubscribedSubscription;
		
		public String toString() {
			return name();
		};
		
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Date getTimeOfMovingToLimitedStatus() {
		return timeOfMovingToLimitedStatus;
	}

	public void setTimeOfMovingToLimitedStatus(Date timeOfMovingToLimitedStatus) {
		this.timeOfMovingToLimitedStatus = timeOfMovingToLimitedStatus;
	}

	public String getCurrentPassword() {
		return currentPassword;
	}

	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public String getPotentialPromotion() {
		return potentialPromotion;
	}

	public void setPotentialPromotion(String potentialPromotion) {
		this.potentialPromotion = potentialPromotion;
	}

	public Integer getSubBalance() {
		return subBalance;
	}

	public void setSubBalance(Integer subBalance) {
		this.subBalance = subBalance;
	}

	@Override
	public String toString() {
		return "AccountDto [email=" + email + ", timeOfMovingToLimitedStatus=" + timeOfMovingToLimitedStatus + ", phoneNumber=" + phoneNumber
				+ ", subscription=" + subscription +", potentialPromotion="+potentialPromotion+ "]";
	}

}
