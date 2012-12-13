package mobi.nowtechnologies.server.persistence.domain;

import javax.persistence.*;

@Entity
@Table(name="device_user_data")
public class DeviceUserData {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name="community_url", nullable=false)
	private String communityUrl;
	
	@Column(name="user_id", nullable=false)
	private Integer userId;
	
	@Column(name="xtify_token", nullable=false, unique = true)
	private String xtifyToken;
	
	@Column(name="device_uid", nullable=false)
	private String deviceUid;

    public DeviceUserData() {
    }

    public DeviceUserData(String communityUrl, Integer userId, String xtifyToken, String deviceUID) {
        this.communityUrl = communityUrl;
        this.userId = userId;
        this.xtifyToken = xtifyToken;
        this.deviceUid = deviceUID;
    }

    public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getCommunityUrl() {
		return communityUrl;
	}
	public void setCommunityUrl(String communityUrl) {
		this.communityUrl = communityUrl;
	}
	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getXtifyToken() {
		return xtifyToken;
	}
	public void setXtifyToken(String xtifyToken) {
		this.xtifyToken = xtifyToken;
	}

    public String getDeviceUid() {
        return deviceUid;
    }

    public void setDeviceUid(String deviceUid) {
        this.deviceUid = deviceUid;
    }

    @Override
    public String toString() {
        return "DeviceUserData{" +
                "id=" + id +
                ", communityUrl='" + communityUrl + '\'' +
                ", userId=" + userId +
                ", xtifyToken='" + xtifyToken + '\'' +
                ", deviceUID='" + deviceUid + '\'' +
                '}';
    }
}