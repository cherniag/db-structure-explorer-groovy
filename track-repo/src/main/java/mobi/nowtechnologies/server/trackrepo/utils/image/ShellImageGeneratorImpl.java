package mobi.nowtechnologies.server.trackrepo.utils.image;

import mobi.nowtechnologies.server.trackrepo.utils.ExternalCommand;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.io.Resource;

public class ShellImageGeneratorImpl implements ImageGenerator {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ShellImageGeneratorImpl.class);
    private List<ThumbnailType> thumbnails;
    private ExternalCommand commandResizeImage;
    private ExternalCommand commandCoverPreviewImage;
    private Resource convertPath;
    private Resource compositePath;

    @Override
    public List<File> generateThumbnails(File dir, String sourceFilePath, String trackId, boolean isVideo) throws IOException, InterruptedException {

        LOGGER.debug("generateThumbnails started for {} in {}", trackId, sourceFilePath);

        List<File> result = new ArrayList<>(thumbnails.size());

        for (ThumbnailType thumbnailType : thumbnails) {
            result.add(generateImage(dir, sourceFilePath, trackId, thumbnailType, isVideo));
        }

        LOGGER.debug("generateThumbnails successfully finished");

        return result;
    }

    @Override
    public List<File> generateThumbnailsWithWatermark(File dir, String sourceFilePath, String trackId, boolean isVideo) throws IOException, InterruptedException {

        LOGGER.debug("generate Thumbnails With Watermark started for {} in {}", trackId, sourceFilePath);

        List<File> result = new ArrayList<>(thumbnails.size());

        for (ThumbnailType thumbnailType : thumbnails) {

            File resultFile = generateImage(dir, sourceFilePath, trackId, thumbnailType, isVideo);
            if (!isVideo) {
                coverPreviewImage(resultFile.getAbsolutePath(), thumbnailType);
            }
            result.add(resultFile);
        }

        LOGGER.debug("ShellImageGeneratorImpl.generateThumbnailsWithWatermark successfuly finished");

        return result;
    }

    protected File generateImage(File dir, String sourceFilePath, String trackId, ThumbnailType thumbnailType, boolean isVideo) throws IOException, InterruptedException {

        LOGGER.info("Generate image {} for {} in {}, isVideo {}", thumbnailType, trackId, sourceFilePath, isVideo);

        if (StringUtils.isBlank(sourceFilePath)) {
            throw new RuntimeException("Image Generation: source file path is null or empty");
        }
        if (StringUtils.isBlank(trackId)) {
            throw new RuntimeException("Image Generation: Track id is null or empty");
        }

        String resultFilePath = dir.getAbsolutePath() + File.separator + trackId + thumbnailType.getFileNameTail() + "." + thumbnailType.getFileExtension();

        int imageSize = isVideo ?
                        thumbnailType.getImageSizeForVideo() :
                        thumbnailType.getImageSizeForAudio();

        commandResizeImage.executeCommand(convertPath.getFile().getAbsolutePath(), sourceFilePath, "" + imageSize, "" + imageSize, thumbnailType.getAdditionalParams() == null ?
                                                                                                                                   "" :
                                                                                                                                   thumbnailType.getAdditionalParams(), resultFilePath);
        return new File(resultFilePath);
    }

    protected void coverPreviewImage(String fileName, ThumbnailType type) throws IOException, InterruptedException {

        if (type.getCoverFilePath() == null ||
            type.getCoverFilePath().getFile() == null ||
            !type.getCoverFilePath().getFile().exists()) {

            return;
        }

        commandCoverPreviewImage.executeCommand(compositePath.getFile().getAbsolutePath(), type.getCoverFilePath().getFile().getAbsolutePath(), fileName, fileName);
    }

    public void setThumbnails(List<ThumbnailType> thumbnails) {
        this.thumbnails = thumbnails;
    }

    public void setCommandResizeImage(ExternalCommand commandResizeImage) {
        this.commandResizeImage = commandResizeImage;
    }

    public void setConvertPath(Resource convertPath) {
        this.convertPath = convertPath;
    }

    public void setCommandCoverPreviewImage(ExternalCommand commandCoverPreviewImage) {
        this.commandCoverPreviewImage = commandCoverPreviewImage;
    }

    public void setCompositePath(Resource compositePath) {
        this.compositePath = compositePath;
    }
}
