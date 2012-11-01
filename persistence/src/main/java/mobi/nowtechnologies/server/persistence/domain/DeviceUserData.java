package mobi.nowtechnologies.server.persistence.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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
	
	@Column(name="xtify_token", nullable=false)
	private String xtifyToken;
	
	@Column(name="device_uid", nullable=false)
	private String deviceUID;
	
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
	public String getDeviceUID() {
		return deviceUID;
	}
	public void setDeviceUID(String deviceUID) {
		this.deviceUID = deviceUID;
	}
			
}
