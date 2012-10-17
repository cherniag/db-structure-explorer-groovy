package mobi.nowtechnologies.server.persistence.domain;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
abstract public class Device {
	
	@Id
	@Column(nullable = false)
	private String deviceUID;
	
	@ManyToOne
	@JoinColumn(name="community_id")
	private Community community;

	public String getDeviceUID() {
		return deviceUID;
	}

	public void setDeviceUID(String deviceUID) {
		this.deviceUID = deviceUID;
	}

	public Community getCommunity() {
		return community;
	}

	public void setCommunity(Community community) {
		this.community = community;
	}
	
}