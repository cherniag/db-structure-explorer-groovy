package mobi.nowtechnologies.server.shared.dto;


/**
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kolpakov (akolpakov)
 *
 */
public class ResourceFileDto {	
	//-------------------------basic properties----------------------//
	private String type;
	private String filename;
	private String resolution;
	
	//-------------------------additional properties----------------------//
	private String md5;
	private String mediaHash;
	private Integer size;
	
	public ResourceFileDto()
	{
	}
	
	public ResourceFileDto(FileType type, Resolution resolution, String filename)
	{
		this.type = type.name();
		this.resolution = resolution.name();
		this.filename = filename;
	}
	
	public ResourceFileDto(FileType type, Resolution resolution, String filename, String mediaHash)
	{
		this(type, resolution, filename);
		
		this.mediaHash = mediaHash;
	}
	
	public String getFullFilename() {
		FileType type = FileType.valueOf(this.type);
		Resolution resolution = null;
		try{
			resolution = AudioResolution.valueOf(this.resolution);
		}catch(IllegalArgumentException e){
			resolution = ImageResolution.valueOf(this.resolution);
		}		
		
		return filename+(resolution != null ? resolution.getSuffix() : "")+"."+(type != null ? type.getExt() : "");
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public Integer getSize() {
		return size;
	}
	public void setSize(Integer size) {
		this.size = size;
	}
	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public String getMediaHash() {
		return mediaHash;
	}
	public void setMediaHash(String mediaHash) {
		this.mediaHash = mediaHash;
	}
	public String getResolution() {
		return resolution;
	}
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((filename == null) ? 0 : filename.hashCode());
		result = prime * result + ((md5 == null) ? 0 : md5.hashCode());
		result = prime * result + ((mediaHash == null) ? 0 : mediaHash.hashCode());
		result = prime * result + ((resolution == null) ? 0 : resolution.hashCode());
		result = prime * result + ((size == null) ? 0 : size.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ResourceFileDto))
			return false;
		ResourceFileDto other = (ResourceFileDto) obj;
		if (filename == null) {
			if (other.filename != null)
				return false;
		} else if (!filename.equals(other.filename))
			return false;
		if (md5 == null) {
			if (other.md5 != null)
				return false;
		} else if (!md5.equals(other.md5))
			return false;
		if (mediaHash == null) {
			if (other.mediaHash != null)
				return false;
		} else if (!mediaHash.equals(other.mediaHash))
			return false;
		if (resolution == null) {
			if (other.resolution != null)
				return false;
		} else if (!resolution.equals(other.resolution))
			return false;
		if (size == null) {
			if (other.size != null)
				return false;
		} else if (!size.equals(other.size))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ResourceFileDto [type=" + type + ", filename=" + filename + ", resolution=" + resolution + ", md5=" + md5 + ", mediaHash=" + mediaHash
				+ ", size=" + size + "]";
	}
}