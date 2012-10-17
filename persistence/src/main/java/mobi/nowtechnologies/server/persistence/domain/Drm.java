package mobi.nowtechnologies.server.persistence.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import mobi.nowtechnologies.server.shared.dto.DrmItemDto;


/**
 * The persistent class for the tb_drm database table.
 * 
 */
@Entity
@Table(name="tb_drm")
@XmlRootElement(name="item")
@NamedQueries({
	@NamedQuery(name=Drm.NQ_FIND_BY_USER_AND_DRM_TYPE
	, query="select drm from Drm drm " +
			"join FETCH drm.drmType drmType " +
			"join FETCH drm.media media " +
			"join FETCH media.artist artist " +
			"join FETCH media.genre genre2 " + 
			"join FETCH media.headerFile headerFile " +
			"join FETCH media.audioFile audioFile " + 
			"join FETCH media.imageFIleLarge imageFileLarge " + 
			"join FETCH media.imageFileSmall imageFileSmall " +
			"where drm.userId=? and drmType=?")
})
public class Drm implements Serializable {
	public static final String NQ_FIND_BY_USER_AND_DRM_TYPE = "Drm.findByUserAndDrmType";
	private static final long serialVersionUID = 1L;
	
	public static enum Fields{
		mediaId,i;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int i;

	@Column(name="drmType", insertable=false, updatable=false)
	private byte drmTypeId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "drmType")
	private DrmType drmType;

	private byte drmValue;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "media")
	private Media media;
	
	@Column(name="media", insertable=false, updatable=false)
	private int mediaId;

	private int timestamp;

	@Column(name="user", insertable=false, updatable=false)
	private int userId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user")
	private User user;

    public Drm() {
    }

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
	
	public DrmItemDto toDrmItemDto(){
		DrmItemDto drmDto = new DrmItemDto();
		drmDto.setDrmType(getDrmType().getName());
		drmDto.setDrmValue(getDrmValue());
		drmDto.setMediaUID(getMedia().getIsrc());
		return drmDto;
	}
	
	public static List<DrmItemDto> toDrmItemDtoList(List<Drm> drmList){
		List<DrmItemDto> drmDtoList = new ArrayList<DrmItemDto>();
		for (Drm drm : drmList) {
			drmDtoList.add(drm.toDrmItemDto());
		}
		return drmDtoList;
		
	}

	@Override
	public String toString() {
		return "Drm [drmTypeId=" + drmTypeId + ", drmValue=" + drmValue + ", i=" + i + ", mediaId=" + mediaId + ", timestamp=" + timestamp
				+ ", userId=" + userId + "]";
	}

}