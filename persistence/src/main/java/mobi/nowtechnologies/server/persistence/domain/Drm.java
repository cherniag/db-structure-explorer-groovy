package mobi.nowtechnologies.server.persistence.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name="tb_drm")
@XmlRootElement(name="item")
public class Drm implements Serializable {
	@Id
	@GeneratedValue(strategy= IDENTITY)
	private int i;

	@Column(name="drmType", insertable=false, updatable=false)
	private byte drmTypeId;
	
	@ManyToOne
	@JoinColumn(name = "drmType")
	private DrmType drmType;

	private byte drmValue;

	@ManyToOne
	@JoinColumn(name = "media")
	private Media media;
	
	@Column(name="media", insertable=false, updatable=false)
	private int mediaId;

	private int timestamp;

	@Column(name="user", insertable=false, updatable=false)
	private int userId;
	
	@ManyToOne
	@JoinColumn(name="user")
	private User user;

    @XmlTransient
	public int getI() {
		return this.i;
	}

	public void setI(int i) {
		this.i = i;
	}
	
	public byte getDrmTypeId() {
		return drmTypeId;
	}

	@XmlTransient
	public DrmType getDrmType() {
		return this.drmType;
	}

	public void setDrmType(DrmType drmType) {
		this.drmType = drmType;
		drmTypeId=drmType.getI();
	}

	@XmlElement(name="drmValue")
	public byte getDrmValue() {
		return this.drmValue;
	}

	public void setDrmValue(byte drmValue) {
		this.drmValue = drmValue;
	}

	@XmlElement(name="mediaUID")
	public Media getMedia() {
		return this.media;
	}
	
	public int getMediaId() {
		return mediaId;
	}

	public void setMedia(Media media) {
		this.media = media;
		mediaId = media.getI();
	}

	@XmlTransient
	public int getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
		userId = user.getId();
	}

	public int getUserId() {
		return userId;
	}

    public Drm withDrmType(DrmType drmType) {
        setDrmType(drmType);
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("i", i)
                .append("drmTypeId", drmTypeId)
                .append("drmValue", drmValue)
                .append("mediaId", mediaId)
                .append("timestamp", timestamp)
                .append("userId", userId)
                .toString();
    }

}