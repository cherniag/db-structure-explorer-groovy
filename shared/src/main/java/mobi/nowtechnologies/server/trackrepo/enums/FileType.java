package mobi.nowtechnologies.server.trackrepo.enums;

/**
 * 
 * @author Alexander Kolpakov (akolpakov)
 *
 */
public enum FileType {
	ORIGINAL_MP3(0, "", "mp3", "audio/mpeg3"),
	ORIGINAL_ACC(0, "", "m4a", "audio/x-m4a"),
	MOBILE_HEADER(1, "header", "hdr", "application/octet-stream"), 
	MOBILE_AUDIO(2, "audio", "aud", "application/octet-stream"),
	MOBILE_ENCODED(2, "encoded", "enc", "application/octet-stream"),
	DOWNLOAD(1, "purchased", "mp3", "audio/mpeg3"),
    IMAGE(3, "image", "jpg", "image/jpeg"),
    VIDEO(4, "video", null, "video/mp4");

	private Integer id;
	private String pack;
	private String ext;
	private String mime;

    private FileType(Integer id, String pack, String ext, String mime)
	{
		this.id = id;
		this.pack = pack;
		this.ext = ext;
		this.mime = mime;
	}
	
	public Integer getId() {
		return id;
	}
    public byte getIdAsByte() {
        return id.byteValue();
    }
	public String getPack() {
		return pack;
	}
	public String getExt() {
		return ext;
	}
	public String getMime() {
		return mime;
	}
}