package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.dto.ImageDTO;
import mobi.nowtechnologies.server.service.file.image.ImageCloudFileMetadata;
import mobi.nowtechnologies.server.service.file.image.ImageInfo;
import mobi.nowtechnologies.server.service.file.image.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;

/**
 * Created by oar on 2/25/14.
 */
public class CloudFileImagesService {

    private CloudFileService cloudFileService;

    private ImageService imageService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public void setCloudFileService(CloudFileService cloudFileService) {
        this.cloudFileService = cloudFileService;
    }

    public void setImageService(ImageService imageService) {
        this.imageService = imageService;
    }

    public ImageDTO uploadImageWithGivenName(byte[] imageBytes, String fileNameInCloud) {
        ImageInfo imageInfo = imageService.getImageFormat(imageBytes);

        ImageCloudFileMetadata imageCloudFileMetadata = ImageCloudFileMetadata.fromImageInfo(imageInfo, fileNameInCloud);

        cloudFileService.uploadFromStream(new ByteArrayInputStream(imageBytes), fileNameInCloud, imageCloudFileMetadata.toMap());

        logger.info("File with name {} is uploaded", fileNameInCloud);

        ImageDTO result = new ImageDTO();
        result.setFileName(imageCloudFileMetadata.getFileName());
        result.setHeight(imageCloudFileMetadata.getHeight());
        result.setWidth(imageCloudFileMetadata.getWidth());
        result.setUrl(cloudFileService.getFilesURL() + imageCloudFileMetadata.getFileName());

        return result;
    }


}
