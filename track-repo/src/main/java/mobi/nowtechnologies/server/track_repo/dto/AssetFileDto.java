package mobi.nowtechnologies.server.track_repo.dto;

import mobi.nowtechnologies.server.shared.dto.FileType;

public class AssetFileDto {
	private FileType type;
	private String path;
	private String md5;
	private byte[] content;
	
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
	public byte[] getContent() {
		return content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}
	
	@Override
	public String toString() {
		return "TrackDto [type=" + type + ", path=" + path + ", md5=" + md5 + ", content=" + content + "]";
	}
}
