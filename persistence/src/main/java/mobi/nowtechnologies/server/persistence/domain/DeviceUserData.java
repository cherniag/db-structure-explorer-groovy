package mobi.nowtechnologies.server.persistence.domain;


import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

@Entity
@Table(name="device_user_data", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "device_uid"}),
        @UniqueConstraint(columnNames = {"xtify_token"})
})
public class DeviceUserData {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name="community_url", nullable=false)
	private String communityUrl;
	
	@Column(name="user_id", nullable=false)
	private Integer userId;
	
	@Column(name="xtify_token", nullable=false, unique = true, columnDefinition="char(255)")
	private String xtifyToken;
	
	@Column(name="device_uid", nullable=false, columnDefinition="char(255)")
	private String deviceUid;

    public DeviceUserData() {
    }

    public DeviceUserData(String communityUrl, Integer userId, String xtifyToken, String deviceUID) {
        this.communityUrl = communityUrl;
        this.userId = userId;
        this.xtifyToken = xtifyToken;
        this.deviceUid = deviceUID;
    }

    public DeviceUserData(User user, String xtifyToken) {
        this(user.getCommunityRewriteUrl(), user.getId(), xtifyToken, user.getDeviceUID());
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
        return new ToStringBuilder(this)
                .append("id", id)
                .append("communityUrl", communityUrl)
                .append("userId", userId)
                .append("xtifyToken", xtifyToken)
                .append("deviceUid", deviceUid)
                .toString();
    }


}
