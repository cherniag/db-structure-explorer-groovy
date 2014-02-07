package mobi.nowtechnologies.server.trackrepo.utils.image;

public class ThumbnailType {

	private static final String DEFAULT_FILE_EXTENSION = "jpg";
	
	private String fileNameTail;
	private int imageSizeForAudio;
	private int imageSizeForVideo;
	private String additionalParams;
	private String fileExtension = DEFAULT_FILE_EXTENSION;
	
	public String getFileNameTail() {
		return fileNameTail;
	}
	public void setFileNameTail(String fileNameTail) {
		this.fileNameTail = fileNameTail;
	}
	public int getImageSizeForAudio() {
		return imageSizeForAudio;
	}
	public void setImageSizeForAudio(int imageSizeForAudio) {
		this.imageSizeForAudio = imageSizeForAudio;
	}
	public int getImageSizeForVideo() {
		return imageSizeForVideo;
	}
	public void setImageSizeForVideo(int imageSizeForVideo) {
		this.imageSizeForVideo = imageSizeForVideo;
	}
	public String getFileExtension() {
		return fileExtension;
	}
	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}
	public String getAdditionalParams() {
		return additionalParams;
	}
	public void setAdditionalParams(String additionalParams) {
		this.additionalParams = additionalParams;
	}
	
	public void setImageSize(int imageSize) {
		setImageSizeForAudio(imageSize);
		setImageSizeForVideo(imageSize);
	}
}
