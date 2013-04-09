package mobi.nowtechnologies.server.shared.dto.web;

import com.google.common.base.Objects;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class UserDeviceRegDetailsDto {

	public static final String NAME = "UserDeviceRegDetailsDto";

	@NotEmpty
	private String deviceUID;
	
	private String deviceModel;

	@NotEmpty
	private String deviceType;

	@NotEmpty
	private String communityName;

	private String ipAddress;

	@NotEmpty
	private String apiVersion;

	@NotEmpty
	private String appVersion;

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

	public String getDeviceUID() {
		return deviceUID;
	}

	public void setDEVICE_UID(String deviceUID) {
		this.deviceUID = deviceUID;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDEVICE_TYPE(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceModel() {
		return deviceModel;
	}

	public void setDEVICE_MODEL(String deviceModel) {
		this.deviceModel = deviceModel;
	}

	@Override
	public String toString() {
        return Objects.toStringHelper(this)
                .add("deviceUID", deviceUID)
                .add("deviceType", deviceType)
                .add("apiVersion", apiVersion)
                .add("community", communityName)
                .add("IP", ipAddress)
                .toString();
	}

}
