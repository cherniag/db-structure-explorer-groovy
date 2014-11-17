package mobi.nowtechnologies.server.shared.dto.web;

import org.apache.commons.lang3.builder.ToStringBuilder;
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

	private String ipAddress;

    private String communityUri;

    private String xtifyToken;

    private String appsFlyerUid;

    public UserDeviceRegDetailsDto withCommunityUri(String communityUri) {
        this.communityUri = communityUri;

        return this;
    }

    public void setCommunityUri(String communityUri) {
        this.communityUri = communityUri;
    }

    public String getCommunityUri() {
        return communityUri;
    }

    public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
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

    public String getXtifyToken() {
        return xtifyToken;
    }

    public void setXTIFY_TOKEN(String xtifyToken) {
        this.xtifyToken = xtifyToken;
    }

    public UserDeviceRegDetailsDto withDeviceUID(String deviceUID) {
        this.deviceUID = deviceUID;
        return this;
    }

    public UserDeviceRegDetailsDto withDeviceType(String deviceType) {
        this.deviceType = deviceType;
        return this;
    }

    public UserDeviceRegDetailsDto withDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
        return this;
    }

    public String getAppsFlyerUid() {
        return appsFlyerUid;
    }

    public void setAPPSFLYER_UID(String appsFlyerUid) {
        this.appsFlyerUid = appsFlyerUid;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("deviceUID", deviceUID)
                .append("deviceType", deviceType)
                .append("ipAddress", ipAddress)
                .append("communityUri", communityUri)
                .append("xtifyToken", xtifyToken)
                .append("appsFlyerUid", appsFlyerUid)
                .toString();
    }
}
