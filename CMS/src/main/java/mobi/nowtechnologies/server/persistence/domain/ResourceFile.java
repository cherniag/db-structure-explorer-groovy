package mobi.nowtechnologies.server.persistence.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class ResourceFile extends CNAbstractEntity implements Serializable {

	
	 
	private static final long serialVersionUID = 1L;
	
	public enum FileType {MOBILE_HEADER, MOBILE_AUDIO,  DOWNLOAD, IMAGE, PREVIEW;}

	
	@Enumerated(EnumType.ORDINAL) 
	protected FileType type;
	@Basic(optional=false)
	protected String path;
	
	@Basic(optional=true)
	protected String MD5;
	
	@Basic(optional=true)
	protected String mediaHash;
	
	@Basic(optional=false)
	protected String resolution;

	public ResourceFile(String path, FileType type, String resolution) {
		this.path = path;
		this.type = type;
		this.resolution = resolution;
	}
	public ResourceFile() {
		
	}

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
	public String getMD5() {
		return MD5;
	}
	public void setMD5(String mD5) {
		MD5 = mD5;
	}
	public String getResolution() {
		return resolution;
	}
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
	public String getMediaHash() {
		return mediaHash;
	}
	public void setMediaHash(String mediaHash) {
		this.mediaHash = mediaHash;
	}
	

	

}
