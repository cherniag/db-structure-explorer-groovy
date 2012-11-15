package mobi.nowtechnologies.server.trackrepo.dto;

import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.enums.FileType;

import java.util.LinkedList;
import java.util.List;

public class AssetFileDto {
	private FileType type;
	private String path;
	private String md5;
	private byte[] content;
	
	public AssetFileDto(){
		
	}
	
	public AssetFileDto(AssetFile file){
		this.setType(toFileType(file.getType()));
		this.setMd5(file.getMd5());
		this.setPath(file.getPath());
	}
	
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
	
	public static List<AssetFileDto> toList(List<AssetFile> files) {
		List<AssetFileDto> fileDtos = new LinkedList<AssetFileDto>();

		for (AssetFile track : files) {
			fileDtos.add(new AssetFileDto(track));
		}

		return fileDtos;
	}

	public static FileType toFileType(AssetFile.FileType fileType)
	{
		switch (fileType) {
		case DOWNLOAD:
			return FileType.ORIGINAL_MP3;
		case MOBILE:
			return FileType.ORIGINAL_ACC;
		case IMAGE:
			return FileType.IMAGE;
		case PREVIEW:
			return FileType.ORIGINAL_ACC;
		}

		return null;
	}
	
	@Override
	public String toString() {
		return "TrackDto [type=" + type + ", path=" + path + ", md5=" + md5 + ", content=" + content + "]";
	}
}
