package mobi.nowtechnologies.server.shared.dto.web;

import java.io.Serializable;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class UserRegDetailsDto implements Serializable {

	private static final long serialVersionUID = 8241491538199898452L;

	public static final String USER_REG_DETAILS_DTO = "UserRegDetailsDto";

	@Email
	@NotEmpty
	private String email;

	@NotEmpty
	@Pattern(regexp = ".{6,20}")
	private String password;

	@NotEmpty
	@Pattern(regexp = ".{6,20}")
	private String confirmPassword;

	@NotEmpty
	private String communityName;

	private boolean termsConfirmed;

	private boolean newsDeliveringConfirmed;

	private String ipAddress;

	private String promotionCode;

	private String facebookId;

	@NotEmpty
	private String apiVersion;

	@NotEmpty
	private String appVersion;
	
	private String deviceType;
	
	private String deviceString;
	
	public UserRegDetailsDto() {
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public boolean isTermsConfirmed() {
		return termsConfirmed;
	}

	public void setTermsConfirmed(boolean termsConfirmed) {
		this.termsConfirmed = termsConfirmed;
	}

	public boolean isNewsDeliveringConfirmed() {
		return newsDeliveringConfirmed;
	}

	public void setNewsDeliveringConfirmed(boolean newsDeliveringConfirmed) {
		this.newsDeliveringConfirmed = newsDeliveringConfirmed;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}

	public String getFacebookId() {
		return facebookId;
	}

	public String getCommunityName() {
		return communityName;
	}

	public void setCommunityName(String communityName) {
		this.communityName = communityName;
	}

	public String getPromotionCode() {
		return promotionCode;
	}

	public void setPromotionCode(String promotionCode) {
		this.promotionCode = promotionCode;
	}

	public String getApiVersion() {
		return apiVersion;
	}

	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceString() {
		return deviceString;
	}

	public void setDeviceString(String deviceString) {
		this.deviceString = deviceString;
	}

	@Override
	public String toString() {
		return "UserRegDetailsDto [email=" + email + ", communityName=" + communityName + ", newsDeliveringConfirmed=" + newsDeliveringConfirmed
				+ ", termsConfirmed=" + termsConfirmed + ", ipAddress=" + ipAddress + ", facebookId=" + facebookId + ", promotionCode=" + promotionCode
				+ ", apiVersion=" + apiVersion + ", appVersion=" + appVersion + ", deviceType=" + deviceType + ", deviceString=" + deviceString + "]";
	}

}
