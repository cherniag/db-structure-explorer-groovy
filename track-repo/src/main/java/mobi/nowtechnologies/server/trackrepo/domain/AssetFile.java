package mobi.nowtechnologies.server.trackrepo.domain;

import javax.persistence.*;

/**
 * 
 * @author Alexander Kolpakov (akolpakov)
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class AssetFile extends AbstractEntity {
	 
	public enum FileType {MOBILE, DOWNLOAD, IMAGE, PREVIEW, VIDEO;}

	@Enumerated(EnumType.ORDINAL) 
	protected FileType type;
	@Basic(optional=false)
	protected String path;
	
	@Basic(optional=true)
	@Column(name="MD5")
	protected String md5;

    protected Integer duration;

    @Column(name="external_id")
    protected String externalId;

    @ManyToOne
    @JoinColumn(name="TrackId", insertable=false, updatable=false)

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
		this.type = type;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    @Override
    public String toString() {
        return "AssetFile{" +
                "type=" + type +
                ", path='" + path + '\'' +
                ", md5='" + md5 + '\'' +
                ", duration=" + duration +
                ", externalId='" + externalId + '\'' +
                "} " + super.toString();
    }
}