package com.musicqubed.cloudassets.uploader;

public class FileWithName {
	private String filePath;
	private String contentType;
	private String nameToUse;
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getNameToUse() {
		return nameToUse;
	}
	public void setNameToUse(String nameToUse) {
		this.nameToUse = nameToUse;
	}
	@Override
	public String toString() {
		return "FileWithName [filePath=" + filePath + ", contentType=" + contentType + ", nameToUse=" + nameToUse + "]";
	}

	
}
