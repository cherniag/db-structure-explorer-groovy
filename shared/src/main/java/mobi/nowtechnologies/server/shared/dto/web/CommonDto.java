package mobi.nowtechnologies.server.shared.dto.web;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public abstract class CommonDto {

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

	public void setCommunityName(String communityName) {
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

	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	@Override
	public String toString() {
		return "apiVersion=" + apiVersion + ", appVersion=" + appVersion + ", communityName=" + communityName + ", ipAddress=" + ipAddress;
	}
}
