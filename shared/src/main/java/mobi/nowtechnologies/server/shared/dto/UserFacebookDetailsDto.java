package mobi.nowtechnologies.server.shared.dto;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class UserFacebookDetailsDto {

	public static final String NAME = "UserFacebookDetailsDto";

	@NotEmpty
	private String facebookToken;

	@NotEmpty
	private String communityName;

	private String ipAddress;

	@NotEmpty
	private String apiVersion;

	@NotEmpty
	private String appVersion;

	@NotEmpty
	private String deviceUID;
	
	@NotEmpty
	private String storedToken;

	public String getCommunityName() {
		return communityName;
	}

	public void setCOMMUNITY_NAME(String communityName) {
		this.communityName = communityName;
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

	public String getFacebookToken() {
		return facebookToken;
	}

	public void setFACEBOOK_TOKEN(String facebookToken) {
		this.facebookToken = facebookToken;
	}

	public String getDeviceUID() {
		return deviceUID;
	}

	public void setDEVICE_UID(String deviceUID) {
		this.deviceUID = deviceUID;
	}

	public String getStoredToken() {
		return storedToken;
	}

	public void setSTORED_TOKEN(String storedToken) {
		this.storedToken = storedToken;
	}

	@Override
	public String toString() {
		return "UserFacebookDetailsDto [facebookToken=" + facebookToken + "apiVersion=" + apiVersion + ", appVersion=" + appVersion + ", communityName="
				+ communityName + ", ipAddress=" + ipAddress + ", deviceUID=" + deviceUID + "]";
	}

}
