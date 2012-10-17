package mobi.nowtechnologies.server.shared.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
@XmlRootElement(name="item")
public class DrmItemDto {

	private byte drmValue;
	
	private String mediaUID;
	
	private String drmType; 

    public DrmItemDto() {
    }

	public String getMediaUID() {
		return mediaUID;
	}

	public void setMediaUID(String mediaUID) {
		this.mediaUID = mediaUID;
	}

	public byte getDrmValue() {
		return this.drmValue;
	}

	public void setDrmValue(byte drmValue) {
		this.drmValue = drmValue;
	}

	public String getDrmType() {
		return drmType;
	}
	
	public void setDrmType(String drmType) {
		this.drmType = drmType;
	}

	@Override
	public String toString() {
		return "DrmDto [drmType=" + drmType + ", drmValue=" + drmValue + ", mediaUID=" + mediaUID + "]";
	}
}


