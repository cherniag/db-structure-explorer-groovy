package mobi.nowtechnologies.domain;

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
public class AssetFile extends AbstractEntity {

	
	 
	public enum FileType {MOBILE, DOWNLOAD, IMAGE, PREVIEW;}

	
	@Enumerated(EnumType.ORDINAL) 
	protected FileType type;
	@Basic(optional=false)
	protected String path;
	
	@Basic(optional=true)
	protected String MD5;

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
	

	

}
