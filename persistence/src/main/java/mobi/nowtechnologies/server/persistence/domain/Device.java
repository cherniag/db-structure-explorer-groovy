package mobi.nowtechnologies.server.persistence.domain;

import org.apache.commons.lang.builder.ToStringBuilder;

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

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("deviceUID", deviceUID)
                .append("community", community)
                .toString();
    }
}