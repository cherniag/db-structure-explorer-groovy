package mobi.nowtechnologies.server.trackrepo.utils.image;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.springframework.core.io.Resource;

public class ThumbnailType {

    private static final String DEFAULT_FILE_EXTENSION = "jpg";

    private String fileNameTail;
    private int imageSizeForAudio;
    private int imageSizeForVideo;
    private String additionalParams;
    private Resource coverFilePath;
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

    public Resource getCoverFilePath() {
        return coverFilePath;
    }

    public void setCoverFilePath(Resource coverFilePath) {
        this.coverFilePath = coverFilePath;
    }

    public void setImageSize(int imageSize) {
        setImageSizeForAudio(imageSize);
        setImageSizeForVideo(imageSize);
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("fileNameTail", fileNameTail).append("imageSizeForAudio", imageSizeForAudio)
                                                                          .append("imageSizeForVideo", imageSizeForVideo).append("additionalParams", additionalParams)
                                                                          .append("coverFilePath", coverFilePath).append("fileExtension", fileExtension).toString();
    }
}
