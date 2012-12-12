package mobi.nowtechnologies.server.shared.dto;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class UserDetailsDto {

	public static final String NAME = "UserDetailsDto";

	@Email
	@NotEmpty
	private String email;

	@NotEmpty
	private String deviceId;

	@NotEmpty
	private String storedToken;
	
	@NotEmpty
	@Pattern(regexp = ".{6,20}")
	private String newPassword;
	
	@NotEmpty
	@Pattern(regexp = ".{6,20}")
	private String newConfirmPassword;

	@NotEmpty
	private String communityRedirectUrl;

	private String ipAddress;

	@NotEmpty
	private String apiVersion;

	@NotEmpty
	private String appVersion;

	public String getCommunityName() {
		return communityRedirectUrl;
	}
	
	public void setCOMMUNITY_NAME(String communityRedirectUrl) {
		this.communityRedirectUrl = communityRedirectUrl;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getApiVersion() {
		return apiVersion;
	}

	public void setAPI_VERSION(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAPP_VERSION(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getEmail() {
		return email;
	}

	public void setUSER_NAME(String email) {
		this.email = email;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDEVICE_UID(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getStoredToken() {
		return storedToken;
	}

	public void setSTORED_TOKEN(String storedToken) {
		this.storedToken = storedToken;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNEW_PASSWORD(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getNewConfirmPassword() {
		return newConfirmPassword;
	}

	public void setNEW_CONFIRM_PASSWORD(String newConfirmPassword) {
		this.newConfirmPassword = newConfirmPassword;
	}

	@Override
	public String toString() {
		return "UserDetailsDto [deviceId=" + deviceId + ", email=" + email + "apiVersion=" + apiVersion + ", appVersion=" + appVersion + ", communityRedirectUrl="
				+ communityRedirectUrl + ", ipAddress=" + ipAddress + "]";
	}
}