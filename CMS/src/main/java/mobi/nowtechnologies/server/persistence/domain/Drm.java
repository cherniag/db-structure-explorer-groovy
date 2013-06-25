package mobi.nowtechnologies.server.persistence.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;


/**
 * The persistent class for the tb_drm database table.
 * 
 */
@Entity
@Table(name="tb_drm")
public class Drm implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int i;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "drmType")
	@Fetch(FetchMode.JOIN)
	private DrmType drmType;

	private byte drmValue;

//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "media")
//	@Fetch(FetchMode.JOIN)
//	private Media media;
	
	@Column(name="media")
	private int mediaId;

	public int getMediaId() {
		return mediaId;
	}

	public void setMediaId(int mediaId) {
		this.mediaId = mediaId;
	}

	private int timestamp;

	private int user;

    public Drm() {
    }

	public int getI() {
		return this.i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public DrmType getDrmType() {
		return this.drmType;
	}

	public void setDrmType(DrmType drmType) {
		this.drmType = drmType;
	}

	public byte getDrmValue() {
		return this.drmValue;
	}

	public void setDrmValue(byte drmValue) {
		this.drmValue = drmValue;
	}

//	@XmlElement(name="mediaUID")
//	public Media getMedia() {
//		return this.media;
//	}
//
//	public void setMedia(Media media) {
//		this.media = media;
//	}

	public int getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public int getUser() {
		return this.user;
	}

	public void setUser(int user) {
		this.user = user;
	}

	/**
	 * @return
	 */
	public String getDrmTypeName() {
		return drmType.getName();
	}

}